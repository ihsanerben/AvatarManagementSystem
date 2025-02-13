package com.ihsan.controller;

import com.ihsan.entity.UserProcessEntity;
import com.ihsan.entity.UserEntity;
import com.ihsan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserProcessEntity> register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestHeader(value = "TxnId") String txnId) throws Exception {

        UserEntity currentUserEntityUserEntity = userService.registerUser(username, password, txnId);
        UserProcessEntity processEntity = new UserProcessEntity();
        processEntity.setUser(currentUserEntityUserEntity);
        processEntity.setProcess("Register işlemi başarılı");
        return ResponseEntity.ok(processEntity);
    }
}
