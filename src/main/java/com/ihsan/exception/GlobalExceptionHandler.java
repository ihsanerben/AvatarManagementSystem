package com.ihsan.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.http.HttpStatus;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFileNotFound(WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.FILE_NOT_FOUND, request.getDescription(false));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); // 404
    }

    @ExceptionHandler(UnsupportedFileTypeException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedFileType(WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.UNSUPPORTED_FILE_TYPE, request.getDescription(false));
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(errorResponse); // 415
    }

    @ExceptionHandler(FileSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleFileSizeExceeded(WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.FILE_SIZE_EXCEEDED, request.getDescription(false));
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(errorResponse); // 413
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.USER_NOT_FOUND, request.getDescription(false));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); // 404
    }

    @ExceptionHandler(InvalidCredentialException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_CREDENTIAL_EXCEPTION, request.getDescription(false));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse); // 401
    }

    @ExceptionHandler(TxnIdNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTxnIdNotFound(WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.TXNID_IS_MUST, request.getDescription(false));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse); // 400
    }

    @ExceptionHandler(FileNameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleFileNameAlreadyExists(WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.FILE_NAME_ALREADY_EXIST, request.getDescription(false));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse); // 409
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, request.getDescription(false));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse); // 500
    }
}
