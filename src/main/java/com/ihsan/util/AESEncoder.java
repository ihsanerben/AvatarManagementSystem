package com.ihsan.util;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AESEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        try {
            return AESUtil.encrypt(rawPassword.toString());
        } catch (Exception e) {
            throw new RuntimeException("Encryption error", e);
        }
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        try {
            return AESUtil.decrypt(encodedPassword).equals(rawPassword.toString());
        } catch (Exception e) {
            throw new RuntimeException("Decryption error", e);
        }
    }
}
