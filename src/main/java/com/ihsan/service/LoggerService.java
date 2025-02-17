package com.ihsan.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoggerService {

    private static final Logger logger = LoggerFactory.getLogger(LoggerService.class);

    public void info(String txnId, String message) {
        logger.info("[TxnId: {}] {}", txnId, message);
    }

    public void error(String txnId, String message) {
        logger.error("[TxnId: {}] {}", txnId, message);
    }
}
