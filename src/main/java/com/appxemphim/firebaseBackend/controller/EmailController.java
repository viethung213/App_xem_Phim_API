package com.appxemphim.firebaseBackend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appxemphim.firebaseBackend.dto.response.EmailDTO;
import com.appxemphim.firebaseBackend.service.EmailService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    
    @GetMapping("/{email}")
    public boolean IsEmailExist(@PathVariable String email) {
        return emailService.checkEmail(email);
    }

    @PostMapping("/senDTO/{email}")
    public EmailDTO sendDto(@PathVariable String email) {
        return emailService.senDto(email);
    }
    
    

    
}
