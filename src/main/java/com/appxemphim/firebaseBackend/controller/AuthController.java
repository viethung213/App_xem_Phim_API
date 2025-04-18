package com.appxemphim.firebaseBackend.controller;

import org.checkerframework.checker.interning.qual.UsesObjectEquals;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appxemphim.firebaseBackend.dto.request.SetPassWordRequest;
import com.appxemphim.firebaseBackend.service.AccountService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final AccountService accountService;

    @PostMapping("/login/{uid}")
    public ResponseEntity<String> login(@PathVariable String uid) {
        try {
            String jwtToken = accountService.createToken(uid);
            return ResponseEntity.ok(jwtToken);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PatchMapping("/repass")
    public ResponseEntity<String> repass(@RequestBody SetPassWordRequest request) {
        try{
            String result = accountService.setPass(request);
            return ResponseEntity.ok(result);
        }catch(RuntimeException e){
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    //register được thực hiện ở app (client)
}
