package com.example.inaction.config;

import com.example.inaction.repo.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeRequests()
                .requestMatchers("/design", "/orders").hasRole("USER") // /design, /orders 경로에 대한 요청은 USER 권한이 있어야 함
                .requestMatchers("/", "/**").permitAll() // /, /** 경로에 대한 요청은 모두 허용
                .and()
                .formLogin((formLogin) ->
                        formLogin.loginPage("/login") // 로그인 페이지 경로
                                .defaultSuccessUrl("/something_after_login") // 로그인 성공 시 이동할 경로
                ).logout((logout) ->
                        logout.logoutSuccessUrl("/login") // 로그아웃 성공 시 이동할 경로
                                .invalidateHttpSession(true) // 로그아웃 시 세션 무효화
                )
                .build();
    }
}
