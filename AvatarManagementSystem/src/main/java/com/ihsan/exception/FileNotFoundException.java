package com.ihsan.exception;

public class FileNotFoundException extends RuntimeException {
    public FileNotFoundException() {
        super(ErrorCode.FILE_NOT_FOUND.getMessage());
    }
}
