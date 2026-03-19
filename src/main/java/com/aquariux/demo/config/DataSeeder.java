package com.aquariux.demo.config;

import com.aquariux.demo.entity.UserEntity;
import com.aquariux.demo.entity.WalletEntity;
import com.aquariux.demo.repository.UserRepository;
import com.aquariux.demo.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final AppProperties appProperties;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        Long userId = appProperties.getDemoUserId();
        LocalDateTime now = LocalDateTime.now();

        UserEntity user = UserEntity.builder()
                .id(userId)
                .username("demo-user")
                .status("ACTIVE")
                .createdAt(now)
                .build();
        userRepository.save(user);

        walletRepository.save(WalletEntity.builder()
                .userId(userId)
                .asset("USDT")
                .balance(new BigDecimal("50000.00000000"))
                .createdAt(now)
                .updatedAt(now)
                .build());

        walletRepository.save(WalletEntity.builder()
                .userId(userId)
                .asset("BTC")
                .balance(BigDecimal.ZERO)
                .createdAt(now)
                .updatedAt(now)
                .build());

        walletRepository.save(WalletEntity.builder()
                .userId(userId)
                .asset("ETH")
                .balance(BigDecimal.ZERO)
                .createdAt(now)
                .updatedAt(now)
                .build());
    }
}
