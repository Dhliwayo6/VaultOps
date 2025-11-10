package com.vaultops.services.stats;

import com.vaultops.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class GetAssetsInStorageService implements Query<Void, Integer> {

    @Override
    public ResponseEntity<Integer> execute(Void input) {
        return null;
    }
}
