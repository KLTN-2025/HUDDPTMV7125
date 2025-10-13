package com.example.KLTN.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ApplicationConfig {
    private final UserDetailsService userDetailsService;
private final JwtUtill jwtUtill;
    public ApplicationConfig(UserDetailsService userDetailsService, JwtUtill jwtUtill) {
        this.userDetailsService = userDetailsService;
        this.jwtUtill = jwtUtill;
    }



    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(jwtUtill.passwordEncoder());
        return authProvider;
    }
}