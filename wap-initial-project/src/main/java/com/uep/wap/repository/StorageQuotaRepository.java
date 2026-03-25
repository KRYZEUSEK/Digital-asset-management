package com.uep.wap.repository;

import com.uep.wap.model.StorageQuota;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StorageQuotaRepository extends JpaRepository<StorageQuota, Long> {
    Optional<StorageQuota> findByUserId(Long userId);
}
