package com.vaultops.services.stats;

import com.vaultops.Query;
import org.springframework.http.ResponseEntity;

public class GetAssetsInStorage implements Query<Void, Integer> {

    @Override
    public ResponseEntity<Integer> execute(Void input) {
        return null;
    }
}
