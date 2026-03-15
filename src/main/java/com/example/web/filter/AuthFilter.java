package com.example.web.filter;

import com.example.domain.exception.BadCredentialsException;
import com.example.domain.service.AuthService;
import com.example.web.model.ErrorResponse;
import com.example.web.model.SecurityUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class AuthFilter extends GenericFilterBean {

    private final AuthService service;

    public AuthFilter(AuthService service) {
        this.service = service;
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String authHeader = httpRequest.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            byte[] decodedBytes = Base64.getDecoder().decode(authHeader.substring("Basic ".length()));
            String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

            String[] values = decodedString.split(":", 2);
            if (values.length != 2) throw new IllegalArgumentException("Invalid basic authentication format");
            String login = values[0], password = values[1];
            if (login.isEmpty() || password.isEmpty()) {
                throw new BadCredentialsException("Login or password cannot be empty");
            }
            UUID userId = service.authenticate(login, password);

            SecurityUserDetails user = new SecurityUserDetails(userId, login, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            chain.doFilter(request, response);
        } catch (BadCredentialsException ex) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json");
            ErrorResponse errorResponse = new ErrorResponse("Unauthorized", ex.getMessage());
            new ObjectMapper().writeValue(response.getWriter(), errorResponse);
//            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            httpResponse.getWriter().write("Authentication failed");
        }
    }
}
