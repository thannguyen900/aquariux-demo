package com.aquariux.demo.repository;

import com.aquariux.demo.entity.WalletEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<WalletEntity, Long> {

    List<WalletEntity> findByUserIdOrderByAssetAsc(Long userId);

    Optional<WalletEntity> findByUserIdAndAsset(Long userId, String asset);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from WalletEntity w where w.userId = :userId and w.asset = :asset")
    Optional<WalletEntity> findByUserIdAndAssetForUpdate(@Param("userId") Long userId,
                                                         @Param("asset") String asset);
}
