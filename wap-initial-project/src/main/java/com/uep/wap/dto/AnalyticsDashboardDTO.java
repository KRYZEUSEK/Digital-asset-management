package com.uep.wap.dto;

import java.util.LinkedHashMap;
import java.util.Map;

public class AnalyticsDashboardDTO {
    private long totalAssets;
    private long publishedAssets;
    private long archivedAssets;
    private long activeUsersLast30Days;
    private Map<String, Long> assetTypeDistribution = new LinkedHashMap<>();

    public long getTotalAssets() { return totalAssets; }
    public void setTotalAssets(long totalAssets) { this.totalAssets = totalAssets; }
    public long getPublishedAssets() { return publishedAssets; }
    public void setPublishedAssets(long publishedAssets) { this.publishedAssets = publishedAssets; }
    public long getArchivedAssets() { return archivedAssets; }
    public void setArchivedAssets(long archivedAssets) { this.archivedAssets = archivedAssets; }
    public long getActiveUsersLast30Days() { return activeUsersLast30Days; }
    public void setActiveUsersLast30Days(long activeUsersLast30Days) { this.activeUsersLast30Days = activeUsersLast30Days; }
    public Map<String, Long> getAssetTypeDistribution() { return assetTypeDistribution; }
    public void setAssetTypeDistribution(Map<String, Long> assetTypeDistribution) { this.assetTypeDistribution = assetTypeDistribution; }
}
