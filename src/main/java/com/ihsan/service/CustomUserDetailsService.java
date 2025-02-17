package com.ihsan.service;

import com.ihsan.entity.UserEntity;
import com.ihsan.exception.UserNotFoundException;
import com.ihsan.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)  {
        Optional<UserEntity> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            throw new UserNotFoundException();
        }

        UserEntity user = userOptional.get();

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword()) // AES çözme işlemi kaldırıldı
                .roles("USER")
                .build();
    }
}
