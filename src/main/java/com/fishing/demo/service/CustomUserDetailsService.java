package com.fishing.demo.service;

import com.fishing.demo.model.User;
import com.fishing.demo.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Transformă entitatea User într-un UserDetails folosit de Spring Security.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        // 1) Găsește User-ul în baza de date
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User nu a fost găsit: " + username)
                );

        // 2) Convertește-l într-un UserDetails Spring Security
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())      // parola este deja criptată
                .authorities("ROLE_USER")          // în viitor puteți mapa un câmp role în User
                .build();
    }
}
