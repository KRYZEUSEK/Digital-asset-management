package com.uep.wap.service;

import com.uep.wap.dto.AssetDTO;
import com.uep.wap.dto.AssetVersionDTO;
import com.uep.wap.dto.CreateAssetDTO;
import com.uep.wap.dto.UpdateAssetDTO;
import com.uep.wap.model.Asset;
import com.uep.wap.model.AssetVersion;
import com.uep.wap.model.Category;
import com.uep.wap.model.Metadata;
import com.uep.wap.model.StorageQuota;
import com.uep.wap.model.Tag;
import com.uep.wap.model.User;
import com.uep.wap.repository.AssetRepository;
import com.uep.wap.repository.AssetVersionRepository;
import com.uep.wap.repository.CategoryRepository;
import com.uep.wap.repository.StorageQuotaRepository;
import com.uep.wap.repository.TagRepository;
import com.uep.wap.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class AssetService {

    private final AssetRepository assetRepository;
    private final AssetVersionRepository assetVersionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final StorageQuotaRepository storageQuotaRepository;

    public AssetService(AssetRepository assetRepository,
                        AssetVersionRepository assetVersionRepository,
                        UserRepository userRepository,
                        CategoryRepository categoryRepository,
                        TagRepository tagRepository,
                        StorageQuotaRepository storageQuotaRepository) {
        this.assetRepository = assetRepository;
        this.assetVersionRepository = assetVersionRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.storageQuotaRepository = storageQuotaRepository;
    }

    public List<AssetDTO> getAllAssets() {
        return assetRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public AssetDTO getAsset(Long id) {
        return mapToDto(findAsset(id));
    }

    public AssetDTO createAsset(CreateAssetDTO dto) {
        User owner = userRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));

        validateAssetPayload(dto.getTitle(), dto.getOriginalFilename(), dto.getStoragePath(),
                dto.getMimeType(), dto.getFileSizeBytes());
        ensureQuotaAvailable(owner.getId(), dto.getFileSizeBytes());

        Asset asset = new Asset();
        asset.setTitle(dto.getTitle().trim());
        asset.setDescription(dto.getDescription());
        asset.setOriginalFilename(dto.getOriginalFilename());
        asset.setStoragePath(dto.getStoragePath());
        asset.setThumbnailPath(dto.getThumbnailPath());
        asset.setMimeType(dto.getMimeType());
        asset.setFileSizeBytes(dto.getFileSizeBytes());
        asset.setType(dto.getType());
        asset.setStatus(dto.getStatus());
        asset.setLicenceType(dto.getLicenceType());
        asset.setPublicDownload(Boolean.TRUE.equals(dto.getPublicDownload()));
        asset.setOwner(owner);
        asset.setUpdatedAt(LocalDateTime.now());

        if (dto.getCategoryId() != null) {
            asset.setCategory(findCategory(dto.getCategoryId()));
        }
        asset.setTags(resolveTags(dto.getTagIds()));

        Metadata metadata = new Metadata();
        metadata.setAsset(asset);
        metadata.setTitle(dto.getTitle());
        metadata.setDescription(dto.getDescription());
        asset.setMetadata(metadata);

        Asset savedAsset = assetRepository.save(asset);

        AssetVersion version = new AssetVersion();
        version.setAsset(savedAsset);
        version.setUploadedBy(owner);
        version.setVersionNumber(savedAsset.getVersionNumber());
        version.setFilename(savedAsset.getOriginalFilename());
        version.setStoragePath(savedAsset.getStoragePath());
        version.setFileSizeBytes(savedAsset.getFileSizeBytes());
        savedAsset.getVersions().add(version);
        assetVersionRepository.save(version);

        updateQuotaUsage(owner.getId(), dto.getFileSizeBytes());
        return mapToDto(savedAsset);
    }

    public AssetDTO updateAsset(Long id, UpdateAssetDTO dto) {
        Asset asset = findAsset(id);
        Long previousFileSize = asset.getFileSizeBytes();

        if (dto.getTitle() != null && dto.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title must not be blank");
        }
        if (dto.getStatus() != null) {
            asset.setStatus(dto.getStatus());
        }
        if (dto.getLicenceType() != null) {
            asset.setLicenceType(dto.getLicenceType());
        }
        if (dto.getPublicDownload() != null) {
            asset.setPublicDownload(dto.getPublicDownload());
        }
        if (dto.getCategoryId() != null) {
            asset.setCategory(findCategory(dto.getCategoryId()));
        }
        if (dto.getTitle() != null) {
            asset.setTitle(dto.getTitle().trim());
        }
        if (dto.getDescription() != null) {
            asset.setDescription(dto.getDescription());
        }
        if (dto.getThumbnailPath() != null) {
            asset.setThumbnailPath(dto.getThumbnailPath());
        }
        if (!dto.getTagIds().isEmpty()) {
            asset.setTags(resolveTags(dto.getTagIds()));
        }
        if (asset.getMetadata() != null) {
            Metadata metadata = asset.getMetadata();
            if (dto.getTitle() != null) {
                metadata.setTitle(dto.getTitle());
            }
            if (dto.getDescription() != null) {
                metadata.setDescription(dto.getDescription());
            }
        }

        boolean createNewVersion = false;
        if (dto.getOriginalFilename() != null) {
            asset.setOriginalFilename(dto.getOriginalFilename());
            createNewVersion = true;
        }
        if (dto.getStoragePath() != null) {
            asset.setStoragePath(dto.getStoragePath());
            createNewVersion = true;
        }
        if (dto.getMimeType() != null) {
            asset.setMimeType(dto.getMimeType());
        }
        if (dto.getFileSizeBytes() != null) {
            ensureQuotaAvailable(asset.getOwner().getId(), dto.getFileSizeBytes() - previousFileSize);
            asset.setFileSizeBytes(dto.getFileSizeBytes());
            createNewVersion = true;
        }

        if (createNewVersion) {
            User uploadedBy = dto.getUploadedById() != null
                    ? userRepository.findById(dto.getUploadedById())
                    .orElseThrow(() -> new IllegalArgumentException("Uploading user not found"))
                    : asset.getOwner();
            asset.setVersionNumber(asset.getVersionNumber() + 1);

            AssetVersion version = new AssetVersion();
            version.setAsset(asset);
            version.setUploadedBy(uploadedBy);
            version.setVersionNumber(asset.getVersionNumber());
            version.setFilename(asset.getOriginalFilename());
            version.setStoragePath(asset.getStoragePath());
            version.setFileSizeBytes(asset.getFileSizeBytes());
            asset.getVersions().add(version);
        }

        asset.setUpdatedAt(LocalDateTime.now());
        Asset savedAsset = assetRepository.save(asset);
        updateQuotaUsage(asset.getOwner().getId(), savedAsset.getFileSizeBytes() - previousFileSize);
        return mapToDto(savedAsset);
    }

    public void deleteAsset(Long id) {
        Asset asset = findAsset(id);
        Long ownerId = asset.getOwner().getId();
        Long fileSize = asset.getFileSizeBytes();
        assetRepository.delete(asset);
        updateQuotaUsage(ownerId, -fileSize);
    }

    private AssetDTO mapToDto(Asset asset) {
        AssetDTO dto = new AssetDTO();
        dto.setId(asset.getId());
        dto.setTitle(asset.getTitle());
        dto.setDescription(asset.getDescription());
        dto.setOriginalFilename(asset.getOriginalFilename());
        dto.setStoragePath(asset.getStoragePath());
        dto.setThumbnailPath(asset.getThumbnailPath());
        dto.setMimeType(asset.getMimeType());
        dto.setType(asset.getType());
        dto.setStatus(asset.getStatus());
        dto.setLicenceType(asset.getLicenceType());
        dto.setFileSizeBytes(asset.getFileSizeBytes());
        dto.setPublicDownload(asset.getPublicDownload());
        dto.setDownloadCount(asset.getDownloadCount());
        dto.setVersionNumber(asset.getVersionNumber());
        dto.setCreatedAt(asset.getCreatedAt());
        dto.setUpdatedAt(asset.getUpdatedAt());
        if (asset.getOwner() != null) {
            dto.setOwnerId(asset.getOwner().getId());
        }
        if (asset.getCategory() != null) {
            dto.setCategoryId(asset.getCategory().getId());
        }
        dto.setTagIds(asset.getTags().stream().map(Tag::getId).collect(Collectors.toList()));
        dto.setTagNames(asset.getTags().stream().map(Tag::getName).sorted().collect(Collectors.toList()));
        dto.setVersions(asset.getVersions().stream()
                .sorted(Comparator.comparing(AssetVersion::getVersionNumber))
                .map(this::mapVersionToDto)
                .collect(Collectors.toList()));
        if (asset.getMetadata() != null) {
            Metadata metadata = asset.getMetadata();
            dto.setTitle(metadata.getTitle());
            dto.setDescription(metadata.getDescription());
        }
        return dto;
    }

    private AssetVersionDTO mapVersionToDto(AssetVersion version) {
        AssetVersionDTO dto = new AssetVersionDTO();
        dto.setId(version.getId());
        dto.setVersionNumber(version.getVersionNumber());
        dto.setFilename(version.getFilename());
        dto.setFileSizeBytes(version.getFileSizeBytes());
        dto.setUploadedAt(version.getUploadedAt());
        return dto;
    }

    private Asset findAsset(Long id) {
        return assetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Asset not found"));
    }

    private Category findCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }

    private Set<Tag> resolveTags(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return new LinkedHashSet<>();
        }
        List<Tag> tags = tagRepository.findAllById(tagIds);
        if (tags.size() != new LinkedHashSet<>(tagIds).size()) {
            throw new IllegalArgumentException("One or more tags were not found");
        }
        return new LinkedHashSet<>(tags);
    }

    private void validateAssetPayload(String title, String originalFilename, String storagePath,
                                      String mimeType, Long fileSizeBytes) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("Original filename is required");
        }
        if (storagePath == null || storagePath.isBlank()) {
            throw new IllegalArgumentException("Storage path is required");
        }
        if (mimeType == null || mimeType.isBlank()) {
            throw new IllegalArgumentException("MIME type is required");
        }
        if (fileSizeBytes == null || fileSizeBytes < 0) {
            throw new IllegalArgumentException("File size must be a non-negative number");
        }
    }

    private void ensureQuotaAvailable(Long userId, Long sizeDelta) {
        if (sizeDelta == null || sizeDelta <= 0) {
            return;
        }
        storageQuotaRepository.findByUserId(userId).ifPresent(quota -> {
            long nextUsage = quota.getUsedStorageBytes() + sizeDelta;
            if (nextUsage > quota.getMaxStorageBytes()) {
                throw new IllegalStateException("Storage quota exceeded for the selected owner");
            }
        });
    }

    private void updateQuotaUsage(Long userId, Long sizeDelta) {
        if (sizeDelta == null || sizeDelta == 0) {
            return;
        }
        storageQuotaRepository.findByUserId(userId).ifPresent(quota -> {
            long updatedUsage = Math.max(0L, quota.getUsedStorageBytes() + sizeDelta);
            quota.setUsedStorageBytes(updatedUsage);
            storageQuotaRepository.save(quota);
        });
    }
}
