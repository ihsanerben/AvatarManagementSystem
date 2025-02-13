package com.ihsan.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND.getMessage());
    }
}
