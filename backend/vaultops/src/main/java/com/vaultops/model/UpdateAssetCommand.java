package com.vaultops.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateAssetCommand {
    private Long id;
    private Asset asset;

}
