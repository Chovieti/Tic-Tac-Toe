package com.example.web.security;

import com.example.domain.model.Roles;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class JwtProvider {
    private final String secret;
    private final long accessExpiration;
    private final long refreshExpiration;

    public JwtProvider(@Value("${jwt.secret}") String secret,
                       @Value("${jwt.access-expiration}") long accessExpiration,
                       @Value("${jwt.refresh-expiration}") long refreshExpiration) {
        this.secret = secret;
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //    для генерации токенов используй io.jsonwebtoken.Jwts.builder();
    //    метод генерации accessToken по User, в токен необходимо сохранить информацию о UUID и роли в claims (в claims сохраняют часть информации об объекте для последующего использования);
    public String generateAccessToken(UUID userId, List<Roles> roles) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessExpiration);

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("type", "access")
                .claim("roles", roles.stream().map(Roles::name).toList())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey())
                .compact();
    }
    //    метод генерации refreshToken по User, в токен необходимо сохранить информацию о UUID в claims;
    public String generateRefreshToken(UUID userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshExpiration);

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("type", "refresh")
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey())
                .compact();
    }
    // Пока буду просто проверять, что нужный тип токена, затем посмотрим
    //    метод валидации accessToken;
    public boolean validateAccessToken(String token) {
        try {
            Claims claims = getClaims(token);

            return claims.get("type", String.class).equals("access");
        } catch (JwtException e) {
            return false;
        }
    }
    //    метод валидации refreshToken;
    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = getClaims(token);

            return claims.get("type", String.class).equals("refresh");
        } catch (JwtException e) {
            return false;
        }
    }
    //    метод получения claims.
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
