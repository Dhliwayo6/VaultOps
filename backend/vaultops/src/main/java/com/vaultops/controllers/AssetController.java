package com.vaultops.controllers;

import com.vaultops.dtos.AssetDTO;
import com.vaultops.dtos.AssetSummaryDTO;
import com.vaultops.model.Asset;
import com.vaultops.services.AssetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@Validated
public class AssetController {
    private final AssetService assetService;

    @GetMapping("/assets/top-four/in-use")
    public ResponseEntity<List<AssetSummaryDTO>> getTopFourAssetsInUse() {
        return ResponseEntity.ok(assetService.getTopFourInUse());
    }

    @GetMapping("/assets/top-four/in-repairs")
    public ResponseEntity<List<AssetSummaryDTO>> getTopFourAssetsInRepairs() {
        return ResponseEntity.ok(assetService.getTopFourInRepairs());
    }

    @PostMapping("/asset")
    public ResponseEntity<AssetDTO> createAsset(@Valid @RequestBody Asset asset) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assetService.create(asset));
    }

    @GetMapping("/asset/{id}")
    public ResponseEntity<AssetDTO> getAssetById(@PathVariable Long id) {
        return ResponseEntity.ok(assetService.getById(id));
    }

    @GetMapping("/assets")
    public ResponseEntity<Object> getAssets(
            @RequestParam(required = false) @Min(value = 0, message = "Page number cannot be negative") Integer page,
            @RequestParam(required = false) @Min(value = 1, message = "Page size must be at least 1") @Max(value = 100, message = "Page size cannot exceed 100") Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction,
            @RequestParam(required = false) Long locationId) {
        
        if (page == null || size == null) {
            return ResponseEntity.ok(assetService.getAllNonPaginated(locationId));
        }
        
        Sort sort = direction.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(assetService.getAll(pageable, locationId));
    }

    @GetMapping("/asset/search")
    public ResponseEntity<List<AssetDTO>> searchAssetByName(
            @RequestParam @NotBlank(message = "Search term cannot be blank") @Size(max = 255, message = "Search term too long") String name) {
        return ResponseEntity.ok(assetService.search(name));
    }

    @PutMapping("/asset/{id}")
    public ResponseEntity<AssetDTO> updateAsset(@PathVariable Long id, @Valid @RequestBody Asset asset) {
        return ResponseEntity.ok(assetService.update(id, asset));
    }

    @DeleteMapping("/asset/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAsset(@PathVariable Long id) {
        assetService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
