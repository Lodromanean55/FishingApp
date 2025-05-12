package com.fishing.demo.controller;

import com.fishing.demo.model.AuthResponseDTO;
import com.fishing.demo.model.LoginRequestDTO;
import com.fishing.demo.model.User;
import com.fishing.demo.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", // ← allow any origin
        allowedHeaders = "*",
        methods = { RequestMethod.GET,
                RequestMethod.POST,
                RequestMethod.PUT,
                RequestMethod.DELETE,
                RequestMethod.OPTIONS })
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        User saved = authService.registerUserWithValidation(user);
        // nu returna tot userul, doar dto
        // Poți să returnezi un DTO fără password, sau pur și simplu:
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        AuthResponseDTO resp = authService.authenticate(dto.username, dto.password);
        return ResponseEntity.ok(resp);
    }
}
