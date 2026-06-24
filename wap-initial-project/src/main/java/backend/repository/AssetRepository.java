package backend.repository;

import backend.model.Asset;
import backend.model.AssetStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    List<Asset> findByStatus(AssetStatus status);

    List<Asset> findByOwnerId(Long ownerId);

    List<Asset> findByTitleContainingIgnoreCase(String title);
}
