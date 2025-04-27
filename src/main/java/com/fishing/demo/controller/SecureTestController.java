package com.fishing.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/secure")
public class SecureTestController {

    /**
     * Acest endpoint va fi accesibil doar dacă trimiți un JWT valid
     * în header-ul Authorization: Bearer <token>
     */
    @GetMapping("/hello")
    public ResponseEntity<String> hello(Authentication auth) {
        // auth.getName() e username-ul extras din JWT
        String user = auth.getName();
        return ResponseEntity.ok("Salut, " + user + "! Acesta e un endpoint securizat.");
    }
}
