package com.ihsan.exception;

public class FileSizeExceededException extends RuntimeException {
    public FileSizeExceededException() {
        super(ErrorCode.FILE_SIZE_EXCEEDED.getMessage());
    }
}
