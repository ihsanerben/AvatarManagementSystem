package com.ihsan.exception;

public enum ErrorCode {
    UNSUPPORTED_FILE_TYPE(111, "file type sikinti."),
    FILE_SIZE_EXCEEDED(222, "file size cok buyuk (5mb)."),
    FILE_NOT_FOUND(333, "dosya bulunamadi."),
    INTERNAL_SERVER_ERROR(444, "genel bi hata oldu."),
    USER_NOT_FOUND(555, "user bulunamadi."),
    INVALID_CREDENTIAL_EXCEPTION(666, "kimlik dogrulaması tamamlanamadı."),
    TXNID_IS_MUST(777, "TxnId girmen lazım kanki."),
    FILE_NAME_ALREADY_EXIST(888, "bu file name önceden kullanılmış kanki.");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
