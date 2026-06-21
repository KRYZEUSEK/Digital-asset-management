package com.uep.wap.config;

import com.uep.wap.model.Asset;
import com.uep.wap.repository.AssetRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@Order(100)
public class AssetStoragePathRepairer implements CommandLineRunner {

    private final AssetRepository assetRepository;

    public AssetStoragePathRepairer(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Override
    public void run(String... args) {
        Path storageDir = Paths.get("uploads");
        if (!Files.isDirectory(storageDir)) {
            return;
        }

        assetRepository.findAll().forEach(asset -> repairAssetPath(asset, storageDir));
    }

    private void repairAssetPath(Asset asset, Path storageDir) {
        if (pathExists(asset.getStoragePath()) || asset.getOriginalFilename() == null || asset.getOriginalFilename().isBlank()) {
            return;
        }

        findLocalUpload(storageDir, asset.getOriginalFilename()).ifPresent(localPath -> {
            asset.setStoragePath(localPath.toAbsolutePath().toString());
            asset.setUpdatedAt(LocalDateTime.now());
            assetRepository.save(asset);
        });
    }

    private boolean pathExists(String storagePath) {
        return storagePath != null && !storagePath.isBlank() && Files.exists(Paths.get(storagePath));
    }

    private Optional<Path> findLocalUpload(Path storageDir, String originalFilename) {
        try (Stream<Path> files = Files.list(storageDir)) {
            return files
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith("_" + originalFilename))
                    .max(Comparator.comparingLong(path -> path.toFile().lastModified()));
        } catch (IOException ex) {
            return Optional.empty();
        }
    }
}
