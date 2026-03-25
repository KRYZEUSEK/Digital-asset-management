package com.uep.wap.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "analytics_snapshots")
public class AnalyticsSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate snapshotDate;

    private Long totalAssets = 0L;

    private Long publishedAssets = 0L;

    private Long archivedAssets = 0L;

    private Long activeUsers = 0L;

    @Column(length = 4000)
    private String payloadJson;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getSnapshotDate() {
        return snapshotDate;
    }

    public void setSnapshotDate(LocalDate snapshotDate) {
        this.snapshotDate = snapshotDate;
    }

    public Long getTotalAssets() {
        return totalAssets;
    }

    public void setTotalAssets(Long totalAssets) {
        this.totalAssets = totalAssets;
    }

    public Long getPublishedAssets() {
        return publishedAssets;
    }

    public void setPublishedAssets(Long publishedAssets) {
        this.publishedAssets = publishedAssets;
    }

    public Long getArchivedAssets() {
        return archivedAssets;
    }

    public void setArchivedAssets(Long archivedAssets) {
        this.archivedAssets = archivedAssets;
    }

    public Long getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(Long activeUsers) {
        this.activeUsers = activeUsers;
    }

    public String getPayloadJson() {
        return payloadJson;
    }

    public void setPayloadJson(String payloadJson) {
        this.payloadJson = payloadJson;
    }
}
