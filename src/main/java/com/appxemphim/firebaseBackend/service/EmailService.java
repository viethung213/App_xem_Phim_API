package com.appxemphim.firebaseBackend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.MailException;
import com.appxemphim.firebaseBackend.dto.response.EmailDTO;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
public class EmailService {
    @Value("${abstractapi.key}")
    private String apiKey;
    @Autowired
    private JavaMailSender emailSender;
    

    public boolean checkEmail(String email) {
        String apiUrl = "https://emailvalidation.abstractapi.com/v1/?api_key=" + apiKey + "&email=" + email;
        RestTemplate restTemplate = new RestTemplate();
        
        Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);
        
        if (response == null || !response.containsKey("is_valid_format")) return false;

        Map<String, Object> isValidFormat = (Map<String, Object>) response.get("is_valid_format");
        Object value = isValidFormat.get("value");

        return value instanceof Boolean && (Boolean) value;
    }

    public EmailDTO senDto(String email){
         EmailDTO result = new EmailDTO();
        String DTO = String.valueOf(new Random().nextInt(900000) + 100000);
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Mã xác nhận APP");
            helper.setText("Mã xác nhận của bạn là: " + DTO, false);
            emailSender.send(message);
            result.setEmail(email);
            result.setMa(DTO);
            return result;
        } catch (MessagingException | MailException e) {
            throw new RuntimeException("Không thể gửi email: " + e.getMessage(), e);
        }
    }
    
}
