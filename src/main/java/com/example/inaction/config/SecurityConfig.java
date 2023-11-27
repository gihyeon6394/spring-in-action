package com.example.inaction.config;

import com.example.inaction.entity.Idol;
import com.example.inaction.entity.Member;
import com.example.inaction.repo.IdolRepository;
import com.example.inaction.repo.MemberRepository;
import com.example.inaction.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.time.LocalDateTime;
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
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    public CommandLineRunner dataLoader(
            MemberRepository repoMember,
            IdolRepository repoIdol,
            PasswordEncoder encoder) {
        return args -> {

            Member karina = new Member();
            karina.setUserName("karina");
            karina.setPassword(encoder.encode("1234"));
            karina.setName("karina");
            karina.setCreatedAt(LocalDateTime.now());

            Member giselle = new Member();
            giselle.setUserName("giselle");
            giselle.setPassword(encoder.encode("1234"));
            giselle.setName("giselle");
            giselle.setCreatedAt(LocalDateTime.now());

            Member winter = new Member();
            winter.setUserName("winter");
            winter.setPassword(encoder.encode("1234"));
            winter.setName("winter");
            winter.setCreatedAt(LocalDateTime.now());

            Member ningning = new Member();
            ningning.setUserName("ningning");
            ningning.setPassword(encoder.encode("1234"));
            ningning.setName("ningning");
            ningning.setCreatedAt(LocalDateTime.now());

            Idol aespa = new Idol();
            aespa.setName("aespa");
            aespa.addIdol(karina);
            aespa.addIdol(giselle);
            aespa.addIdol(winter);
            aespa.setCreatedAt(LocalDateTime.now());

            repoIdol.save(aespa);

            Member minzi = new Member();
            minzi.setUserName("minzi");
            minzi.setPassword(encoder.encode("1234"));
            minzi.setName("minzi");
            minzi.setCreatedAt(LocalDateTime.now());

            Member haerin = new Member();
            haerin.setUserName("haerin");
            haerin.setPassword(encoder.encode("1234"));
            haerin.setName("haerin");
            haerin.setCreatedAt(LocalDateTime.now());


            Member hani = new Member();
            hani.setUserName("hani");
            hani.setPassword(encoder.encode("1234"));
            hani.setName("hani");
            hani.setCreatedAt(LocalDateTime.now());

            Member dainel = new Member();
            dainel.setUserName("dainel");
            dainel.setPassword(encoder.encode("1234"));
            dainel.setName("dainel");
            dainel.setCreatedAt(LocalDateTime.now());

            Member hyein = new Member();
            hyein.setUserName("hyein");
            hyein.setPassword(encoder.encode("1234"));
            hyein.setName("hyein");
            hyein.setCreatedAt(LocalDateTime.now());

            Idol newJeans = new Idol();
            newJeans.setName("newJeans");
            newJeans.addIdol(minzi);
            newJeans.addIdol(haerin);
            newJeans.addIdol(hani);
            newJeans.addIdol(dainel);
            newJeans.addIdol(hyein);
            newJeans.setCreatedAt(LocalDateTime.now());

            repoIdol.save(newJeans);


        };
    }
}
