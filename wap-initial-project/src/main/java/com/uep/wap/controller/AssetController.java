package com.uep.wap.controller;

import com.uep.wap.dto.AssetDTO;
import com.uep.wap.dto.CreateAssetDTO;
import com.uep.wap.dto.UpdateAssetDTO;
import com.uep.wap.service.AssetService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping
    public List<AssetDTO> getAssets() {
        return assetService.getAllAssets();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssetDTO createAsset(@RequestBody CreateAssetDTO dto) {
        return assetService.createAsset(dto);
    }

    @PutMapping("/{id}")
    public AssetDTO updateAsset(@PathVariable Long id, @RequestBody UpdateAssetDTO dto) {
        return assetService.updateAsset(id, dto);
    }
}
