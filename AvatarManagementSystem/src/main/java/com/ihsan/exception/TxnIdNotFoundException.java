package com.ihsan.exception;

public class TxnIdNotFoundException extends RuntimeException {
    public TxnIdNotFoundException() {
      super(ErrorCode.TXNID_IS_MUST.getMessage());
    }
}
