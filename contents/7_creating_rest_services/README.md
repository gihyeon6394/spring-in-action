# 7. Creating REST services

1. Writing Restful controllers
2. Enabling data-backed services
3. Consuming REST services
4. Summary

> ### This chapter covers
>
> - Spring MVC에서 REST endpoint를 작성하는 방법
> - 자동으로 생성되는 repository-based REST endpoint
> - Consuming REST APIs

---

## 1. Writing Restful controllers

- REST API는 HTTP에 대한 응답으로 data-oriented format을 반환 (JSON, XML)

| Annotation        | HTTP method                                                                           | Typical use |
|-------------------|---------------------------------------------------------------------------------------|-------------|
| `@GetMapping`     | HTTP GET                                                                              | Read        |
| `@PostMapping`    | HTTP POST                                                                             | Create      |
| `@PutMapping`     | HTTP PUT                                                                              | Update      |
| `@PatchMapping`   | HTTP PATCH                                                                            | Update      |
| `@DeleteMapping`  | HTTP DELETE                                                                           | Delete      |
| `@RequestMapping` | method attribute에 지정한 method로 HTTP request를 매핑<br/>e.g. `@RequestMapping(method=GET)` |             |

### 1. Retrieving data from the server

````java
import com.example.inaction.entity.Idol;
import com.example.inaction.repo.IdolRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // REST API를 처리하는 컨트롤러
@RequestMapping(path = "/api/members" // request URI : /api/members/**
        , produces = "application/json") // JSON 형태로 응답
@CrossOrigin(origins = "http://taco-cloud.com") // CORS 설정
@AllArgsConstructor
public class IdolController {

    private IdolRepository idolRepository;

    @GetMapping(params = "recent")
    public Iterable<Idol> recentTacos() {
        PageRequest page = PageRequest.of(0, 12, Sort.by("createdAt").descending());
        return idolRepository.findAllBy(page);
    }

}

````

- `@RestController`
    - component scan
    - view에서 사용할 model을 반환하지 않고, HTTP response body에 직접 쓰기 위해 사용
    - `@Controller` + `@ResponseBody`와 같음
- `produces` : client의 HTTP request header에 `Accept`이 포함되어 있을 때만 해당 controller가 response를 생성하도록 함
    - e.g. `produces = "application/json"` : client가 `Accept: application/json`을 포함한 request를 보낼 때만 해당 controller가
      response를 생성
- `@CrossOrigin` : CORS 설정
    - CORS (Cross-Origin Resource Sharing) : 다른 origin에서 리소스에 접근할 수 있도록 허용하는 메커니즘
        - server response header에 `Access-Control-Allow-Origin`을 포함시킴

```java
import com.example.inaction.entity.Idol;
import com.example.inaction.repo.IdolRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // REST API를 처리하는 컨트롤러
@RequestMapping(path = "/api/members" // request URI : /api/members/**
        , produces = "application/json") // JSON 형태로 응답
@CrossOrigin(origins = "http://taco-cloud.com") // CORS 설정
@AllArgsConstructor
public class IdolController {

    private IdolRepository idolRepository;

    @GetMapping(params = "recent")
    public Iterable<Idol> recentTacos() {
        PageRequest page = PageRequest.of(0, 12, Sort.by("createdAt").descending());
        return idolRepository.findAllBy(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Idol> idolById(@PathVariable("id") Long id) {
        return idolRepository.findById(id)
                .map(idol -> new ResponseEntity<>(idol, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

}

```

### 2. Sending data to the server

```java
import com.example.inaction.entity.Idol;
import com.example.inaction.entity.Member;
import com.example.inaction.repo.IdolRepository;
import com.example.inaction.repo.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // REST API를 처리하는 컨트롤러
@RequestMapping(path = "/api/members" // request URI : /api/members/**
        , produces = "application/json") // JSON 형태로 응답
@CrossOrigin(origins = "http://taco-cloud.com") // CORS 설정
@AllArgsConstructor
public class IdolController {

    private IdolRepository idolRepository;
    private MemberRepository memberRepository;

    @GetMapping(params = "recent")
    public Iterable<Idol> recentTacos() {
        PageRequest page = PageRequest.of(0, 12, Sort.by("createdAt").descending());
        return idolRepository.findAllBy(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Idol> idolById(@PathVariable("id") Long id) {
        return idolRepository.findById(id)
                .map(idol -> new ResponseEntity<>(idol, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @PostMapping(consumes = "application/json")// client의 Content-Type이 application/json인 경우에만 처리
    @ResponseStatus(HttpStatus.CREATED) // HTTP response status code를 201로 지정
    public Member postTaco(@RequestBody Member member) {
        return memberRepository.save(member);
    }

}

```

- `consumes` : client의 HTTP request header에 `Content-Type`이 포함되어 있을 때만 해당 controller가 request body를 읽어들이도록 함
    - e.g. `consumes = "application/json"` : client가 `Content-Type: application/json`을 포함한 request를 보낼 때만 해당 controller가
      request body를 읽어들임
- `@RequestBody` : HTTP request body의 JSON을 자바 객체로 변환
    - 매우 중요
    - 생략하면, query parameter, form parameter, multipart form data 등을 읽어들임
- `@ResponseStatus` : HTTP response status code를 지정
    - 생략하면, 기본적으로 `200 OK`를 반환

### 3. Updating data on the server

#### `PUT` vs `PATCH`

- `PUT` 은 `GET`의 반대
    - `GET` : server가 client에게 data를 전달
    - `PUT` : client가 server에게 data를 전달
    - "이 URL에 어떤 데이터가 있건, 내가 보내는 데이터로 바꿔라"
- `PATCH` : `PUT`과 유사하지만, 전달하는 data가 전체 data가 아닌 일부 data임을 명시

````java
import com.example.inaction.entity.Idol;
import com.example.inaction.entity.Member;
import com.example.inaction.repo.IdolRepository;
import com.example.inaction.repo.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // REST API를 처리하는 컨트롤러
@RequestMapping(path = "/api/members" // request URI : /api/members/**
        , produces = "application/json") // JSON 형태로 응답
@CrossOrigin(origins = "http://taco-cloud.com") // CORS 설정
@AllArgsConstructor
public class IdolController {

    private IdolRepository idolRepository;
    private MemberRepository memberRepository;

    @GetMapping(params = "recent")
    public Iterable<Idol> recentIdols() {
        PageRequest page = PageRequest.of(0, 12, Sort.by("createdAt").descending());
        return idolRepository.findAllBy(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Idol> idolById(@PathVariable("id") Long id) {
        return idolRepository.findById(id)
                .map(idol -> new ResponseEntity<>(idol, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @PostMapping(consumes = "application/json")// client의 Content-Type이 application/json인 경우에만 처리
    @ResponseStatus(HttpStatus.CREATED)
    public Member postIdol(@RequestBody Member member) {
        return memberRepository.save(member);
    }

    @PatchMapping(path = "/{id}", consumes = "application/json")
    public Idol patchIdol(@PathVariable("id") Long id, @RequestBody Idol patchIdol) {
        Idol idol = idolRepository.findById(id).get();
        if (patchIdol.getName() != null) {
            idol.setName(patchIdol.getName());
        }
        if (patchIdol.getCntMember() != 0) {
            idol.setCntMember(patchIdol.getCntMember());
        }
        return idolRepository.save(idol);
    }

}
````

#### 한계

- null로 update 불가능
- collection의 element 추가 불가능

### 4. Deleting data from the server

```java
package com.example.inaction;

import com.example.inaction.entity.Idol;
import com.example.inaction.entity.Member;
import com.example.inaction.repo.IdolRepository;
import com.example.inaction.repo.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // REST API를 처리하는 컨트롤러
@RequestMapping(path = "/api/members" // request URI : /api/members/**
        , produces = "application/json") // JSON 형태로 응답
@CrossOrigin(origins = "http://taco-cloud.com") // CORS 설정
@AllArgsConstructor
public class IdolController {

    private IdolRepository idolRepository;
    private MemberRepository memberRepository;

    @GetMapping(params = "recent")
    public Iterable<Idol> recentIdols() {
        PageRequest page = PageRequest.of(0, 12, Sort.by("createdAt").descending());
        return idolRepository.findAllBy(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Idol> idolById(@PathVariable("id") Long id) {
        return idolRepository.findById(id)
                .map(idol -> new ResponseEntity<>(idol, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @PostMapping(consumes = "application/json")// client의 Content-Type이 application/json인 경우에만 처리
    @ResponseStatus(HttpStatus.CREATED)
    public Member postIdol(@RequestBody Member member) {
        return memberRepository.save(member);
    }

    @PatchMapping(path = "/{id}", consumes = "application/json")
    public Idol patchIdol(@PathVariable("id") Long id, @RequestBody Idol patchIdol) {
        Idol idol = idolRepository.findById(id).get();
        if (patchIdol.getName() != null) {
            idol.setName(patchIdol.getName());
        }
        if (patchIdol.getCntMember() != 0) {
            idol.setCntMember(patchIdol.getCntMember());
        }
        return idolRepository.save(idol);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteIdol(@PathVariable("id") Long id) {
        idolRepository.deleteById(id);
    }

}

```

## 2. Enabling data-backed services

## 3. Consuming REST services

## 4. Summary
