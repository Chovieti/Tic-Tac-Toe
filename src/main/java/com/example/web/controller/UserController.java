package com.example.web.controller;

import com.example.domain.service.UserService;
import com.example.web.mapper.MapperDomainWeb;
import com.example.web.model.WebUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<WebUser> getUserInfo(@PathVariable UUID userId) {
        WebUser response = MapperDomainWeb.toWebUser(service.findById(userId));
        return ResponseEntity.ok(response);
    }
}