package com.example.web.security;

import com.example.domain.model.Roles;
import com.example.web.model.JwtAuthentication;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    public JwtAuthentication createAuthentication(Claims claims) {
        UUID userId = UUID.fromString(claims.getSubject());

        @SuppressWarnings("unchecked")
        List<String> roles = claims.get("roles", List.class);

        Collection<GrantedAuthority> authorities = roles.stream()
                .map(Roles::valueOf)
                .collect(Collectors.toList());

        return new JwtAuthentication(userId, authorities, true);
    }
}
