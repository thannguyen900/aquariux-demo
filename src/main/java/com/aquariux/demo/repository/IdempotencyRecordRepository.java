package com.aquariux.demo.repository;

import com.aquariux.demo.entity.IdempotencyRecordEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IdempotencyRecordRepository extends JpaRepository<IdempotencyRecordEntity, Long> {

    Optional<IdempotencyRecordEntity> findByUserIdAndIdempotencyKey(Long userId, String idempotencyKey);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from IdempotencyRecordEntity i where i.userId = :userId and i.idempotencyKey = :idempotencyKey")
    Optional<IdempotencyRecordEntity> findByUserIdAndIdempotencyKeyForUpdate(@Param("userId") Long userId,
                                                                              @Param("idempotencyKey") String idempotencyKey);
}
