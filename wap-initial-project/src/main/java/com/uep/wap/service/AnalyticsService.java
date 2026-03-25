package com.uep.wap.service;

import com.uep.wap.dto.AnalyticsDashboardDTO;
import com.uep.wap.model.Asset;
import com.uep.wap.model.AssetStatus;
import com.uep.wap.model.User;
import com.uep.wap.repository.AssetRepository;
import com.uep.wap.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    private final AssetRepository assetRepository;
    private final UserRepository userRepository;

    public AnalyticsService(AssetRepository assetRepository, UserRepository userRepository) {
        this.assetRepository = assetRepository;
        this.userRepository = userRepository;
    }

    public AnalyticsDashboardDTO getDashboard() {
        List<Asset> assets = assetRepository.findAll();
        List<User> users = userRepository.findAll();

        AnalyticsDashboardDTO dto = new AnalyticsDashboardDTO();
        dto.setTotalAssets(assets.size());
        dto.setPublishedAssets(assets.stream().filter(asset -> asset.getStatus() == AssetStatus.PUBLISHED).count());
        dto.setArchivedAssets(assets.stream().filter(asset -> asset.getStatus() == AssetStatus.ARCHIVED).count());
        dto.setActiveUsersLast30Days(users.stream()
                .filter(user -> user.getLastLoginAt() != null)
                .filter(user -> user.getLastLoginAt().isAfter(LocalDateTime.now().minusDays(30)))
                .count());

        Map<String, Long> distribution = new LinkedHashMap<>();
        assets.forEach(asset -> distribution.merge(asset.getType().name(), 1L, Long::sum));
        dto.setAssetTypeDistribution(distribution);
        return dto;
    }
}
