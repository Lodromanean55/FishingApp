package com.fishing.demo.config;

import com.fishing.demo.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          CustomUserDetailsService userDetailsService,
                          AuthenticationConfiguration authenticationConfiguration) {
        this.jwtAuthenticationFilter     = jwtAuthenticationFilter;
        this.userDetailsService          = userDetailsService;
        this.authenticationConfiguration = authenticationConfiguration;
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CORS + CSRF
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Regulile de acces:
                .authorizeHttpRequests(auth -> auth

                        // --- Permite anume GET-uri publice ---
                        .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()                       // poze
                        .requestMatchers(HttpMethod.GET, "/api/locations/**").permitAll()               // listare și detaliu locații
                        .requestMatchers(HttpMethod.GET, "/api/locations/*/reviews").permitAll()        // listare recenzii

                        // --- Protejează POST/PUT/DELETE ---
                        .requestMatchers(HttpMethod.POST,   "/api/auth/**").permitAll()                 // login/register
                        .requestMatchers(HttpMethod.POST,   "/api/locations").authenticated()           // creare locație
                        .requestMatchers(HttpMethod.PUT,    "/api/locations/**").authenticated()        // update locație
                        .requestMatchers(HttpMethod.DELETE, "/api/locations/**").authenticated()        // ștergere locație
                        .requestMatchers(HttpMethod.POST,   "/api/locations/*/reviews").authenticated() // adăugare review

                        // Preflight CORS pentru orice endpoint API
                        .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()

                        // Orice altă cerere la /api/** necesită autentificare
                        .requestMatchers("/api/**").authenticated()
                        .requestMatchers(HttpMethod.POST,   "/api/locations/*/reservations").authenticated()
                        .requestMatchers(HttpMethod.GET,    "/api/reservations/me").authenticated()
                        .requestMatchers(HttpMethod.GET,    "/api/locations/*/reservations").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/reservations/*").authenticated()
                )
                .userDetailsService(userDetailsService)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of("http://localhost:4200"));
        cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", cfg);
        source.registerCorsConfiguration("/uploads/**", cfg);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
