package com.uep.wap.service;

import com.uep.wap.dto.AssetDTO;
import com.uep.wap.dto.CreateAssetDTO;
import com.uep.wap.dto.UpdateAssetDTO;
import com.uep.wap.model.Asset;
import com.uep.wap.model.Category;
import com.uep.wap.model.Metadata;
import com.uep.wap.model.User;
import com.uep.wap.repository.AssetRepository;
import com.uep.wap.repository.CategoryRepository;
import com.uep.wap.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssetService {

    private final AssetRepository assetRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public AssetService(AssetRepository assetRepository,
                        UserRepository userRepository,
                        CategoryRepository categoryRepository) {
        this.assetRepository = assetRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<AssetDTO> getAllAssets() {
        return assetRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public AssetDTO createAsset(CreateAssetDTO dto) {
        User owner = userRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));

        Asset asset = new Asset();
        asset.setOriginalFilename(dto.getOriginalFilename());
        asset.setStoragePath(dto.getStoragePath());
        asset.setThumbnailPath(dto.getThumbnailPath());
        asset.setMimeType(dto.getMimeType());
        asset.setFileSizeBytes(dto.getFileSizeBytes());
        asset.setType(dto.getType());
        asset.setStatus(dto.getStatus());
        asset.setLicenceType(dto.getLicenceType());
        asset.setOwner(owner);
        asset.setUpdatedAt(LocalDateTime.now());

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            asset.setCategory(category);
        }

        Metadata metadata = new Metadata();
        metadata.setAsset(asset);
        metadata.setTitle(dto.getTitle());
        metadata.setDescription(dto.getDescription());
        asset.setMetadata(metadata);

        return mapToDto(assetRepository.save(asset));
    }

    public AssetDTO updateAsset(Long id, UpdateAssetDTO dto) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Asset not found"));
        if (dto.getStatus() != null) {
            asset.setStatus(dto.getStatus());
        }
        if (dto.getLicenceType() != null) {
            asset.setLicenceType(dto.getLicenceType());
        }
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            asset.setCategory(category);
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
        asset.setUpdatedAt(LocalDateTime.now());
        return mapToDto(assetRepository.save(asset));
    }

    private AssetDTO mapToDto(Asset asset) {
        AssetDTO dto = new AssetDTO();
        dto.setId(asset.getId());
        dto.setOriginalFilename(asset.getOriginalFilename());
        dto.setType(asset.getType());
        dto.setStatus(asset.getStatus());
        dto.setLicenceType(asset.getLicenceType());
        dto.setFileSizeBytes(asset.getFileSizeBytes());
        if (asset.getOwner() != null) {
            dto.setOwnerId(asset.getOwner().getId());
        }
        if (asset.getCategory() != null) {
            dto.setCategoryId(asset.getCategory().getId());
        }
        if (asset.getMetadata() != null) {
            Metadata metadata = asset.getMetadata();
            dto.setTitle(metadata.getTitle());
            dto.setDescription(metadata.getDescription());
        }
        return dto;
    }
}
