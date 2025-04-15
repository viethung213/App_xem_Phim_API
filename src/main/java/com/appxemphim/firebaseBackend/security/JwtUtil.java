package com.appxemphim.firebaseBackend.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    private long validityInMilliseconds = 1000 * 60 * 60 * 24 * 364; // 1 năm

    // Tạo token
    public String createJwtToken(String uid, String role) {
        long now = System.currentTimeMillis();
        long expirationTime = now + validityInMilliseconds;

        return Jwts.builder()
                .setSubject(uid)  
                .claim("role", role)  
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(expirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // Lấy UID từ token
    public String getUid(String token) {
        return parseClaims(token).getSubject();
    }

    // Lấy Role từ token
    public String getRole(String token) {
        return (String) parseClaims(token).get("role");
    }

    // Kiểm tra xem token có hợp lệ không
    public boolean isTokenExpired(String token) {
        return parseClaims(token).getExpiration().before(new Date());
    }

    // Xác thực token
    public boolean validateToken(String token, String uid) {
        return (uid.equals(getUid(token)) && !isTokenExpired(token));
    }

    // Phân tích token và lấy thông tin
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }
}
