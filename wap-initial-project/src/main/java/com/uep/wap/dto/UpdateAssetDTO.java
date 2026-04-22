package com.uep.wap.dto;

import com.uep.wap.model.AssetStatus;
import com.uep.wap.model.LicenceType;

import java.util.ArrayList;
import java.util.List;

public class UpdateAssetDTO {
    private String title;
    private String description;
    private String originalFilename;
    private String storagePath;
    private String thumbnailPath;
    private String mimeType;
    private Long fileSizeBytes;
    private AssetStatus status;
    private LicenceType licenceType;
    private Boolean publicDownload;
    private Long categoryId;
    private List<Long> tagIds = new ArrayList<>();
    private Long uploadedById;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }
    public String getStoragePath() { return storagePath; }
    public void setStoragePath(String storagePath) { this.storagePath = storagePath; }
    public String getThumbnailPath() { return thumbnailPath; }
    public void setThumbnailPath(String thumbnailPath) { this.thumbnailPath = thumbnailPath; }
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    public Long getFileSizeBytes() { return fileSizeBytes; }
    public void setFileSizeBytes(Long fileSizeBytes) { this.fileSizeBytes = fileSizeBytes; }
    public AssetStatus getStatus() { return status; }
    public void setStatus(AssetStatus status) { this.status = status; }
    public LicenceType getLicenceType() { return licenceType; }
    public void setLicenceType(LicenceType licenceType) { this.licenceType = licenceType; }
    public Boolean getPublicDownload() { return publicDownload; }
    public void setPublicDownload(Boolean publicDownload) { this.publicDownload = publicDownload; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public List<Long> getTagIds() { return tagIds; }
    public void setTagIds(List<Long> tagIds) { this.tagIds = tagIds; }
    public Long getUploadedById() { return uploadedById; }
    public void setUploadedById(Long uploadedById) { this.uploadedById = uploadedById; }
}
