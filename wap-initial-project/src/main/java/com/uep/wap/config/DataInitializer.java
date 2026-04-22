package com.uep.wap.config;

import com.uep.wap.model.Asset;
import com.uep.wap.model.AssetStatus;
import com.uep.wap.model.AssetType;
import com.uep.wap.model.Category;
import com.uep.wap.model.LicenceType;
import com.uep.wap.model.Metadata;
import com.uep.wap.model.Role;
import com.uep.wap.model.StorageQuota;
import com.uep.wap.model.Tag;
import com.uep.wap.model.User;
import com.uep.wap.repository.AssetRepository;
import com.uep.wap.repository.CategoryRepository;
import com.uep.wap.repository.StorageQuotaRepository;
import com.uep.wap.repository.TagRepository;
import com.uep.wap.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initializeData(UserRepository userRepository,
                                     CategoryRepository categoryRepository,
                                     TagRepository tagRepository,
                                     AssetRepository assetRepository,
                                     StorageQuotaRepository storageQuotaRepository,
                                     PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() > 0 || categoryRepository.count() > 0 || tagRepository.count() > 0) {
                return;
            }

            User admin = new User();
            admin.setEmail("admin@dam.local");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setFirstName("System");
            admin.setLastName("Administrator");
            admin.setRoles(Set.of(Role.ADMINISTRATOR, Role.CONTENT_MANAGER));
            admin.setLastLoginAt(LocalDateTime.now().minusDays(1));
            admin = userRepository.save(admin);

            StorageQuota adminQuota = new StorageQuota();
            adminQuota.setUser(admin);
            adminQuota.setMaxStorageBytes(5_368_709_120L);
            adminQuota.setUsedStorageBytes(0L);
            storageQuotaRepository.save(adminQuota);

            Category marketing = new Category();
            marketing.setName("Marketing");
            marketing.setDescription("Marketing campaign assets");
            marketing.setColor("#FFB703");
            marketing.setDisplayOrder(1);
            marketing = categoryRepository.save(marketing);

            Category product = new Category();
            product.setName("Product");
            product.setDescription("Product images and brochures");
            product.setColor("#219EBC");
            product.setDisplayOrder(2);
            product = categoryRepository.save(product);

            Tag hero = new Tag();
            hero.setName("hero");
            hero.setDescription("Primary promotional material");
            hero = tagRepository.save(hero);

            Tag approved = new Tag();
            approved.setName("approved");
            approved.setDescription("Ready for publishing");
            approved = tagRepository.save(approved);

            Asset asset = new Asset();
            asset.setTitle("Spring Campaign Banner");
            asset.setDescription("Starter asset created to validate the backend setup");
            asset.setOriginalFilename("spring-campaign-banner.png");
            asset.setStoragePath("/demo-assets/spring-campaign-banner.png");
            asset.setThumbnailPath("/demo-assets/thumbnails/spring-campaign-banner.png");
            asset.setMimeType("image/png");
            asset.setFileSizeBytes(245_760L);
            asset.setType(AssetType.PNG);
            asset.setStatus(AssetStatus.PUBLISHED);
            asset.setLicenceType(LicenceType.INTERNAL);
            asset.setPublicDownload(Boolean.TRUE);
            asset.setOwner(admin);
            asset.setCategory(marketing);
            asset.setTags(Set.of(hero, approved));

            Metadata metadata = new Metadata();
            metadata.setAsset(asset);
            metadata.setTitle(asset.getTitle());
            metadata.setDescription(asset.getDescription());
            asset.setMetadata(metadata);

            assetRepository.save(asset);
            adminQuota.setUsedStorageBytes(asset.getFileSizeBytes());
            storageQuotaRepository.save(adminQuota);
        };
    }
}
