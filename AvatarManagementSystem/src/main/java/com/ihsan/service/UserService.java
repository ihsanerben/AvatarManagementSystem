package com.ihsan.service;

import com.ihsan.entity.UserEntity;
import com.ihsan.exception.UserNotFoundException;
import com.ihsan.repository.UserRepository;
import com.ihsan.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final long CACHE_EXPIRATION = 30; // Kullanıcılar 30 dakika cache'lenir

    // Kullanıcı kayıt işlemi
    public UserEntity registerUser(String username, String password, String txnId) throws Exception {
        String encryptedPassword = passwordEncoder.encode(password);

        UserEntity userEntity = new UserEntity(username, encryptedPassword);
        userRepository.save(userEntity);

        // Kullanıcıyı Redis'e ekleyelim
        redisTemplate.opsForValue().set("user:" + username, userEntity, CACHE_EXPIRATION, TimeUnit.MINUTES);

        return userEntity;
    }

    // Kullanıcı kimlik doğrulama işlemi
    public UserEntity authenticate(String username, String password, String txnId) throws Exception {
        // Önce Redis’te kontrol edelim
        UserEntity cachedUser = (UserEntity) redisTemplate.opsForValue().get("user:" + username);
        if (cachedUser != null) {
            if (passwordEncoder.matches(password, cachedUser.getPassword())) {
                return cachedUser;
            } else {
                throw new Exception("Geçersiz kullanıcı adı veya şifre.");
            }
        }

        // Eğer Redis’te yoksa, DB’den al ve Redis’e ekleyelim
        Optional<UserEntity> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException();
        }

        UserEntity userEntity = userOptional.get();
        if (!passwordEncoder.matches(password, userEntity.getPassword())) {
            throw new Exception("Geçersiz kullanıcı adı veya şifre.");
        }

        // Redis'e kaydedelim
        redisTemplate.opsForValue().set("user:" + username, userEntity, CACHE_EXPIRATION, TimeUnit.MINUTES);

        return userEntity;
    }
}


