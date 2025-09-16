package com.vaultops.exceptions;

import com.vaultops.enums.ErrorMessages;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AssetNotFoundException extends RuntimeException{
    public AssetNotFoundException() {
        super(ErrorMessages.ASSET_NOT_FOUND.getMessage());
    }
}
