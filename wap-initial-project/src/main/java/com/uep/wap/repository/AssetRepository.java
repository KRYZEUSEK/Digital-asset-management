package com.uep.wap.repository;

import com.uep.wap.model.Asset;
import com.uep.wap.model.AssetStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    List<Asset> findByStatus(AssetStatus status);

    List<Asset> findByOwnerId(Long ownerId);
}
