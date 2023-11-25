package com.example.inaction.config.security;

import com.example.inaction.entity.User;
import com.example.inaction.repo.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepo) {

        // UserDetailsService 인터페이스의 loadUserByUsername() 메서드를 구현
        return username ->
                Optional.ofNullable(userRepo.findByUsername(username))
                        .orElseThrow(() ->
                                new UsernameNotFoundException("User '" + username + "' not found"));
    }
}
