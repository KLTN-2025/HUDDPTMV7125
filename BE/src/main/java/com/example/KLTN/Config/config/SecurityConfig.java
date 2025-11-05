package com.example.KLTN.Config.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .authorizeHttpRequests(auth -> auth
                        // Public API
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/oauth2/**", "/login/**", "/success", "/error").permitAll()
                        .requestMatchers("/api/vnpay/**").permitAll()
                        // Role-based API
                        .requestMatchers(HttpMethod.POST, "/api/withdraw/create").hasAnyRole("OWNER", "USER")
                        .requestMatchers(HttpMethod.PUT, "/api/withdraw/approve/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/withdraw/reject/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/hotel/create").hasRole("OWNER")
                        .requestMatchers(HttpMethod.GET, "/api/hotel/list").permitAll()
                        // All others
                        .anyRequest().authenticated()
                )
                // OAuth2 login
                .oauth2Login(oauth -> oauth
                        .defaultSuccessUrl("/api/auth/success", true)
                        .failureUrl("/api/auth/login?error=true")
                )
                // Add JWT filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
