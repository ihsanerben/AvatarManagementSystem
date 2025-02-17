package com.ihsan.exception;

public class InvalidCredentialException extends RuntimeException {
    public InvalidCredentialException(String message) {
        super(ErrorCode.INVALID_CREDENTIAL_EXCEPTION.getMessage());
    }
}

