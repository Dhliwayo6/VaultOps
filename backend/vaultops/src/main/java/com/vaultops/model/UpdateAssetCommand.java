package com.vaultops.model;

public class UpdateAssetCommand {
    private Long id;
    private Asset asset;

    public UpdateAssetCommand(Long id, Asset asset) {
        this.id = id;
        this.asset = asset;
    }

    public Long getId() {
        return id;
    }

    public Asset getAsset() {
        return asset;
    }
}
