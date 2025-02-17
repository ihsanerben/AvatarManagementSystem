package com.ihsan.exception;

public class UnsupportedFileTypeException extends RuntimeException {
    public UnsupportedFileTypeException() {
        super(ErrorCode.UNSUPPORTED_FILE_TYPE.getMessage());
    }
}