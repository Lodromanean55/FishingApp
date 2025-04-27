// AuthService.java
package com.fishing.demo.service;

import com.fishing.demo.config.JwtUtils;
import com.fishing.demo.exceptions.UserValidationException;
import com.fishing.demo.model.AuthResponseDTO;
import com.fishing.demo.model.User;
import com.fishing.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Autowired
    public AuthService(UserRepository userRepository,
                       JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtUtils = jwtUtils;
    }

    /**
     * Înregistrează un user după ce validează unicitatea username/email
     */
    public User registerUserWithValidation(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UserValidationException("Username is already in use");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserValidationException("Email is already in use");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Autentifică user-ul și generează un JWT:
     * 1) verifică existența și parola
     * 2) generează token-ul cu JwtUtils
     * 3) returnează DTO-ul cu token și username
     */
    public AuthResponseDTO authenticate(String username, String rawPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserValidationException("Invalid username or password"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new UserValidationException("Invalid username or password");
        }

        // Generează token-ul
        String jwt = jwtUtils.generateToken(user.getUsername());
        return new AuthResponseDTO(jwt, user.getUsername());
    }
}
