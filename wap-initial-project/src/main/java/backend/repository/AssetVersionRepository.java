package backend.repository;

import backend.model.AssetVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssetVersionRepository extends JpaRepository<AssetVersion, Long> {
    List<AssetVersion> findByAssetIdOrderByVersionNumberDesc(Long assetId);
}
