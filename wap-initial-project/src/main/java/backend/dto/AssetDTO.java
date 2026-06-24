package backend.dto;

import backend.model.AssetStatus;
import backend.model.AssetType;
import backend.model.LicenceType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AssetDTO {
    private Long id;
    private String title;
    private String description;
    private String originalFilename;
    private String storagePath;
    private String thumbnailPath;
    private String mimeType;
    private AssetType type;
    private AssetStatus status;
    private LicenceType licenceType;
    private Long fileSizeBytes;
    private Boolean publicDownload;
    private Long downloadCount;
    private Integer versionNumber;
    private Long ownerId;
    private Long categoryId;
    private List<Long> tagIds = new ArrayList<>();
    private List<String> tagNames = new ArrayList<>();
    private List<AssetVersionDTO> versions = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public AssetType getType() { return type; }
    public void setType(AssetType type) { this.type = type; }
    public AssetStatus getStatus() { return status; }
    public void setStatus(AssetStatus status) { this.status = status; }
    public LicenceType getLicenceType() { return licenceType; }
    public void setLicenceType(LicenceType licenceType) { this.licenceType = licenceType; }
    public Long getFileSizeBytes() { return fileSizeBytes; }
    public void setFileSizeBytes(Long fileSizeBytes) { this.fileSizeBytes = fileSizeBytes; }
    public Boolean getPublicDownload() { return publicDownload; }
    public void setPublicDownload(Boolean publicDownload) { this.publicDownload = publicDownload; }
    public Long getDownloadCount() { return downloadCount; }
    public void setDownloadCount(Long downloadCount) { this.downloadCount = downloadCount; }
    public Integer getVersionNumber() { return versionNumber; }
    public void setVersionNumber(Integer versionNumber) { this.versionNumber = versionNumber; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public List<Long> getTagIds() { return tagIds; }
    public void setTagIds(List<Long> tagIds) { this.tagIds = tagIds; }
    public List<String> getTagNames() { return tagNames; }
    public void setTagNames(List<String> tagNames) { this.tagNames = tagNames; }
    public List<AssetVersionDTO> getVersions() { return versions; }
    public void setVersions(List<AssetVersionDTO> versions) { this.versions = versions; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
