package com.uep.wap.dto;

import com.uep.wap.model.AssetStatus;
import com.uep.wap.model.AssetType;
import com.uep.wap.model.LicenceType;

public class AssetDTO {
    private Long id;
    private String title;
    private String description;
    private String originalFilename;
    private AssetType type;
    private AssetStatus status;
    private LicenceType licenceType;
    private Long fileSizeBytes;
    private Long ownerId;
    private Long categoryId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }
    public AssetType getType() { return type; }
    public void setType(AssetType type) { this.type = type; }
    public AssetStatus getStatus() { return status; }
    public void setStatus(AssetStatus status) { this.status = status; }
    public LicenceType getLicenceType() { return licenceType; }
    public void setLicenceType(LicenceType licenceType) { this.licenceType = licenceType; }
    public Long getFileSizeBytes() { return fileSizeBytes; }
    public void setFileSizeBytes(Long fileSizeBytes) { this.fileSizeBytes = fileSizeBytes; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
}
