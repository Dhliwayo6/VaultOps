package com.vaultops.exceptions;

import com.vaultops.enums.ErrorMessages;

public class NoAssetsMessageException extends RuntimeException{
    public NoAssetsMessageException() {
        super(ErrorMessages.NO_ASSETS.getMessage());
    }
}
