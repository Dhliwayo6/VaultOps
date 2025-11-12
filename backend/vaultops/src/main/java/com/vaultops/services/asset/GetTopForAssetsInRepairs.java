package com.vaultops.services.asset;

import com.vaultops.Query;
import com.vaultops.dtos.AssetDTO2;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class GetTopForAssetsInRepairs implements Query<Void, List<AssetDTO2>> {
    @Override
    public ResponseEntity<List<AssetDTO2>> execute(Void input) {
        return null;
    }
}
