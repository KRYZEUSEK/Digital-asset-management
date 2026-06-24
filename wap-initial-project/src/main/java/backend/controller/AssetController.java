package backend.controller;

import backend.model.AssetType;
import backend.dto.AssetDTO;
import backend.dto.CreateAssetDTO;
import backend.dto.UpdateAssetDTO;
import backend.service.AssetService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping
    public Map<String,Object> getAssets(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "20") int size,
                                        @RequestParam(required = false) String q,
                                        @RequestParam(required = false) Long category,
                                        @RequestParam(required = false) String tag) {
        List<AssetDTO> all = assetService.getAllAssets();
        List<AssetDTO> filtered = all.stream()
                .filter(a -> q == null || q.isBlank() || (a.getTitle() != null && a.getTitle().toLowerCase().contains(q.toLowerCase())) || (a.getDescription() != null && a.getDescription().toLowerCase().contains(q.toLowerCase())))
                .filter(a -> category == null || (a.getCategoryId() != null && a.getCategoryId().equals(category)))
                .filter(a -> tag == null || tag.isBlank() || (a.getTagNames() != null && a.getTagNames().stream().anyMatch(tn -> tn.equalsIgnoreCase(tag))))
                .collect(Collectors.toList());

        int total = filtered.size();
        int from = Math.max(0, Math.min(page * size, total));
        int to = Math.min(from + size, total);
        List<AssetDTO> content = from < to ? filtered.subList(from, to) : new ArrayList<>();

        Map<String,Object> result = new HashMap<>();
        result.put("content", content);
        result.put("totalElements", total);
        result.put("page", page);
        result.put("size", size);
        return result;
    }

    @GetMapping("/{id}")
    public AssetDTO getAsset(@PathVariable Long id) {
        return assetService.getAsset(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssetDTO createAsset(@RequestBody CreateAssetDTO dto) {
        return assetService.createAsset(dto);
    }

    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    @ResponseStatus(HttpStatus.CREATED)
    public AssetDTO uploadAsset(@RequestParam("file") MultipartFile file,
                                @RequestParam(required = false) Long ownerId,
                                @RequestParam(required = false) Long categoryId,
                                @RequestParam(required = false) List<Long> tagIds,
                                @RequestParam(required = false) String title) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }

        Path storageDir = Paths.get("uploads");
        if (!Files.exists(storageDir)) {
            Files.createDirectories(storageDir);
        }
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path target = storageDir.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        CreateAssetDTO dto = new CreateAssetDTO();
        dto.setTitle(title != null && !title.isBlank() ? title : file.getOriginalFilename());
        dto.setOriginalFilename(file.getOriginalFilename());
        dto.setStoragePath(target.toAbsolutePath().toString());
        dto.setMimeType(file.getContentType() != null ? file.getContentType() : "application/octet-stream");
        dto.setFileSizeBytes(file.getSize());
        dto.setOwnerId(ownerId != null ? ownerId : 1L);
        dto.setCategoryId(categoryId);
        dto.setTagIds(tagIds != null ? tagIds : new ArrayList<>());

        String lower = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) dto.setType(AssetType.JPG);
        else if (lower.endsWith(".png")) dto.setType(AssetType.PNG);
        else if (lower.endsWith(".gif")) dto.setType(AssetType.GIF);
        else if (lower.endsWith(".webp")) dto.setType(AssetType.WEBP);
        else if (lower.endsWith(".pdf")) dto.setType(AssetType.PDF);
        else if (lower.endsWith(".mp4")) dto.setType(AssetType.MP4);
        else dto.setType(AssetType.OTHER);

        return assetService.createAsset(dto);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadAsset(@PathVariable Long id) throws IOException {
        AssetDTO dto = assetService.getAsset(id);
        Optional<Path> resolvedPath = resolveAssetPath(dto);
        if (resolvedPath.isEmpty()) {
            throw new IllegalArgumentException("File not found");
        }
        Path path = resolvedPath.get();
        Resource resource = new UrlResource(path.toUri());
        String contentType = dto.getMimeType() != null ? dto.getMimeType() : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dto.getOriginalFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/{id}/preview")
    public ResponseEntity<Resource> previewAsset(@PathVariable Long id) throws IOException {
        AssetDTO dto = assetService.getAsset(id);
        Optional<Path> resolvedPath = resolveAssetPath(dto);
        if (resolvedPath.isEmpty()) {
            throw new IllegalArgumentException("File not found");
        }
        Path path = resolvedPath.get();
        Resource resource = new UrlResource(path.toUri());
        String contentType = dto.getMimeType() != null ? dto.getMimeType() : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + dto.getOriginalFilename() + "\"")
                .body(resource);
    }

    @PutMapping("/{id}")
    public AssetDTO updateAsset(@PathVariable Long id, @RequestBody UpdateAssetDTO dto) {
        return assetService.updateAsset(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAsset(@PathVariable Long id) {
        assetService.deleteAsset(id);
    }

    private Optional<Path> resolveAssetPath(AssetDTO dto) throws IOException {
        if (dto.getStoragePath() != null && !dto.getStoragePath().isBlank()) {
            Path storedPath = Paths.get(dto.getStoragePath());
            if (Files.exists(storedPath)) {
                return Optional.of(storedPath);
            }
        }

        if (dto.getOriginalFilename() == null || dto.getOriginalFilename().isBlank()) {
            return Optional.empty();
        }

        Path storageDir = Paths.get("uploads");
        if (!Files.isDirectory(storageDir)) {
            return Optional.empty();
        }

        try (Stream<Path> files = Files.list(storageDir)) {
            return files
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith("_" + dto.getOriginalFilename()))
                    .max(Comparator.comparingLong(path -> path.toFile().lastModified()));
        }
    }
}
