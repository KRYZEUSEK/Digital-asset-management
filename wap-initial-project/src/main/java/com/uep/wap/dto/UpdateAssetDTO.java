package com.uep.wap.dto;

import com.uep.wap.model.AssetStatus;
import com.uep.wap.model.LicenceType;

public class UpdateAssetDTO {
    private String title;
    private String description;
    private AssetStatus status;
    private LicenceType licenceType;
    private Long categoryId;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public AssetStatus getStatus() { return status; }
    public void setStatus(AssetStatus status) { this.status = status; }
    public LicenceType getLicenceType() { return licenceType; }
    public void setLicenceType(LicenceType licenceType) { this.licenceType = licenceType; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
}
