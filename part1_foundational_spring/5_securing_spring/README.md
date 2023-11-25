# 5. Securing Spring

1. Enable Spring Security
2. Configuring authentication
3. Securing web requests
4. Applying method-level security
5. Knowing your user
6. Summary

> ### This chapter covers
>
> - Spring Security 자동 설정
> - custom user storage
> - login page
> - CSRF attack 대응
> - user 식별

---

## 1. Enable Spring Security

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-security'
}
```

```
...
Using generated security password: ffce4499-286d-4972-acd4-38257e05f1ba

This generated password is for development use only. Your security configuration must be updated before running your application in production.
...
```

<img src="img.png"  width="50%"/>

#### Spring Security AutoConfiguration

- 모든 HTTP 요청에 authentication 추가
- 특정 role, authority 필요 없음
- 로그인 페이지로 Authentication 수행
- 사용자는 `user` 한명

## 2. Configuring authentication

- xml, java config 으로 가능 (최근에는 java config 추천, 가독성)

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    // 인증 시 사용할 PasswordEncoder 를 Bean 으로 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

```

- DB의 PWD 를 복호화하지 않음
- 사용자가 입력한 PWD를 동일한 알고리즘 (`BCryptPasswordEncoder`) 으로 암호화하여 비교 (`PasswordEncoder.matches()`)

#### user store 구현 (in-memory)

```java
package com.example.inaction.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserDetailService {

    // username으로 UserDetails 객체를 탐색, 없으면 UsernameNotFoundException 발생
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}

```

### 2.1 In-memory user details service

- 사용자 정보가 저장될 in-memory user store 구현

```
@Bean
public UserDetailService userDetailService() {
    List<UserDetails> users = new ArrayList<>();
    // 인증할 user 정보가 2명일 때
    users.add(new User("user01", passwordEncoder().encode("pwd01")
            , Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"))));
    users.add(new User("user02", passwordEncoder().encode("pwd02")
            , Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"))));
    return new InMemoryUserDetailsManager(users);
}
```

### 2.2 Customizing user authentication

- 데이터를 RDB에 보관

#### DEFINING THE USER DOMAIN AND PERSISTENCE

```java
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true) // force=true: final 필드를 초기화하는 생성자를 생성
@RequiredArgsConstructor // final 필드를 위한 생성자를 생성
@ToString
public class User implements UserDetails {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private final String username;
    private final String password;
    private final String fullname;
    private final String street;
    private final String city;
    private final String state;
    private final String zip;
    private final String phoneNumber;

    // user에게 부여된 권한을 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    // user가 만료되지 않았는지
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // user가 잠겨있지 않은지 
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // user의 credentials가 만료되지 않았는지
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // user가 활성화되었는지
    @Override
    public boolean isEnabled() {
        return true;
    }

}


```

```java
import com.example.inaction.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

    User findByUsername(String username);
}

```

- `org.springframework.data.repository.CrudRepository` : CRUD 기능을 제공하는 인터페이스
    - Spring Data JPA가 자동으로 구현체를 runtime에 생성
- `findByUsername()` : username으로 user를 찾는 메서드

#### CREATING A USER DETAILS SERVICE

```java
package com.example.inaction.config;

import com.example.inaction.entity.User;
import com.example.inaction.repo.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {
    // ...

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepo) {

        // UserDetailsService 인터페이스의 loadUserByUsername() 메서드를 구현
        return username ->
                Optional.ofNullable(userRepo.findByUsername(username))
                        .orElseThrow(() ->
                                new UsernameNotFoundException("User '" + username + "' not found"));
    }
}

```

#### REGISTERING USERS

- 사용자 등록은 Spring MVC를 통해
- /registerForm 에 login 없이 접근 불가능

```java
import com.example.inaction.config.security.RegistrationForm;
import com.example.inaction.repo.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@org.springframework.stereotype.Controller
@RequestMapping("/register")
public class Controller {

    private UserRepository userRepo;
    private PasswordEncoder passwordEncoder;

    public Controller(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String registerForm() {
        return "registration";
    }

    @PostMapping
    public String processRegistration(RegistrationForm form) {
        userRepo.save(form.toUser(passwordEncoder));
        return "redirect:/login";
    }
}
```

````java
package com.example.inaction.config.security;

import com.example.inaction.entity.User;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
public class RegistrationForm {
    private String username;
    private String password;
    private String fullname;
    private String street;
    private String city;
    private String state;
    private String zip;
    private String phone;

    public User toUser(PasswordEncoder passwordEncoder) {
        return new User(
                username, passwordEncoder.encode(password),
                fullname, street, city, state, zip, phone);
    }
}

````

## 3. Securing web requests

## 4. Applying method-level security

## 5. Knowing your user

## 6. Summary
