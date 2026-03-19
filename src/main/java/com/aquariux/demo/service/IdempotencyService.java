package com.aquariux.demo.service;

import com.aquariux.demo.dto.request.TradeRequest;
import com.aquariux.demo.entity.IdempotencyRecordEntity;
import com.aquariux.demo.entity.TradeEntity;
import com.aquariux.demo.entity.TradeOrderEntity;
import com.aquariux.demo.enums.IdempotencyStatus;
import com.aquariux.demo.exception.BusinessException;
import com.aquariux.demo.repository.IdempotencyRecordRepository;
import com.aquariux.demo.repository.TradeOrderRepository;
import com.aquariux.demo.repository.TradeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final IdempotencyRecordRepository idempotencyRecordRepository;
    private final TradeOrderRepository tradeOrderRepository;
    private final TradeRepository tradeRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public IdempotencyDecision begin(Long userId, String idempotencyKey, TradeRequest request) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return IdempotencyDecision.noKey();
        }

        String requestHash = hashRequest(request);
        Optional<IdempotencyRecordEntity> existingOpt = idempotencyRecordRepository
                .findByUserIdAndIdempotencyKeyForUpdate(userId, idempotencyKey);

        if (existingOpt.isPresent()) {
            IdempotencyRecordEntity existing = existingOpt.get();
            if (!existing.getRequestHash().equals(requestHash)) {
                throw new BusinessException("Idempotency-Key was already used with a different request payload");
            }

            IdempotencyStatus status = IdempotencyStatus.valueOf(existing.getStatus());
            return switch (status) {
                case COMPLETED -> IdempotencyDecision.replay(buildCompletedResult(existing));
                case FAILED -> throw new BusinessException(existing.getFailureReason() != null
                        ? existing.getFailureReason()
                        : "The previous request with the same Idempotency-Key already failed");
                case PROCESSING, CREATED -> throw new BusinessException("The previous request with the same Idempotency-Key is still being processed");
            };
        }

        LocalDateTime now = LocalDateTime.now();
        IdempotencyRecordEntity record = idempotencyRecordRepository.save(IdempotencyRecordEntity.builder()
                .userId(userId)
                .idempotencyKey(idempotencyKey)
                .requestHash(requestHash)
                .status(IdempotencyStatus.CREATED.name())
                .createdAt(now)
                .updatedAt(now)
                .build());

        return IdempotencyDecision.created(record);
    }

    @Transactional
    public void markProcessing(IdempotencyRecordEntity record, Long orderId) {
        if (record == null) return;
        record.setOrderId(orderId);
        record.setStatus(IdempotencyStatus.PROCESSING.name());
        record.setUpdatedAt(LocalDateTime.now());
        idempotencyRecordRepository.save(record);
    }

    @Transactional
    public void markCompleted(IdempotencyRecordEntity record, Long orderId, Long tradeId) {
        if (record == null) return;
        record.setOrderId(orderId);
        record.setTradeId(tradeId);
        record.setStatus(IdempotencyStatus.COMPLETED.name());
        record.setUpdatedAt(LocalDateTime.now());
        idempotencyRecordRepository.save(record);
    }

    @Transactional
    public void markFailed(IdempotencyRecordEntity record, Long orderId, String reason) {
        if (record == null) return;
        record.setOrderId(orderId);
        record.setStatus(IdempotencyStatus.FAILED.name());
        record.setFailureReason(reason);
        record.setUpdatedAt(LocalDateTime.now());
        idempotencyRecordRepository.save(record);
    }

    private ReplayTradeResponse buildCompletedResult(IdempotencyRecordEntity existing) {
        TradeOrderEntity order = tradeOrderRepository.findById(existing.getOrderId())
                .orElseThrow(() -> new BusinessException("Stored order was not found for the completed idempotent request"));
        TradeEntity trade = tradeRepository.findById(existing.getTradeId())
                .orElseThrow(() -> new BusinessException("Stored trade was not found for the completed idempotent request"));
        return new ReplayTradeResponse(order, trade);
    }

    private String hashRequest(TradeRequest request) {
        try {
            String json = objectMapper.writeValueAsString(request);
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] digest = messageDigest.digest(json.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (JsonProcessingException | NoSuchAlgorithmException e) {
            throw new BusinessException("Failed to create idempotency request hash");
        }
    }

    public record IdempotencyDecision(boolean hasKey,
                                      boolean replay,
                                      IdempotencyRecordEntity record,
                                      ReplayTradeResponse replayResponse) {
        public static IdempotencyDecision noKey() {
            return new IdempotencyDecision(false, false, null, null);
        }

        public static IdempotencyDecision created(IdempotencyRecordEntity record) {
            return new IdempotencyDecision(true, false, record, null);
        }

        public static IdempotencyDecision replay(ReplayTradeResponse replayResponse) {
            return new IdempotencyDecision(true, true, null, replayResponse);
        }
    }

    public record ReplayTradeResponse(TradeOrderEntity order, TradeEntity trade) {}
}
