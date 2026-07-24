package com.rafael.usuario.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@SuppressWarnings("deprecation")
@Service
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration:3600000}")
    private Long expiration;

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public boolean validateToken(String token, String username) {
        // Se for um token de serviço gerado pelo BFF, valida apenas se não expirou e se a assinatura confere
        if (isServiceToken(token)) {
            return !isTokenExpired(token);
        }
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    public String extractTokenType(String token) {
        Claims claims = extractClaims(token);
        // Compatibilidade com ambas as formas de escrita da claim ("tokentype" e "tokenType")
        String type = claims.get("tokentype", String.class);
        if (type == null) {
            type = claims.get("tokenType", String.class);
        }
        return type;
    }

    public boolean isServiceToken(String token) {
        try {
            String tokenType = extractTokenType(token);
            return "SERVICE".equals(tokenType) || "bff-servico".equals(extractUsername(token));
        } catch (Exception e) {
            return false;
        }
    }
}