package com.ihsan.exception;

import java.time.LocalDateTime;

public class ErrorResponse {
    private int code;
    private String message;
    private String path;
    private LocalDateTime timestamp;

    public ErrorResponse(ErrorCode errorCode, String path) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
