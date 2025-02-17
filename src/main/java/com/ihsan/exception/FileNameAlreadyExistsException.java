package com.ihsan.exception;

public class FileNameAlreadyExistsException extends RuntimeException {
    public FileNameAlreadyExistsException() {
        super(ErrorCode.FILE_NOT_FOUND.getMessage());
    }
}
