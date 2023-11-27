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
package com.example.inaction;

import com.example.inaction.entity.Taco;
import com.example.inaction.repo.TacoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // REST API를 처리하는 컨트롤러
@RequestMapping(path = "/api/tacos" // request URI : /api/tacos/**
        , produces = "application/json") // JSON 형태로 응답
@CrossOrigin(origins = "http://taco-cloud.com") // CORS 설정
public class TacoController {

    private TacoRepository tacoRepo;

    public TacoController(TacoRepository tacoRepo) {
        this.tacoRepo = tacoRepo;
    }

    @GetMapping(params = "recent")
    public Iterable<Taco> recentTacos() {
        PageRequest page = PageRequest.of(0, 12, Sort.by("createdAt").descending());
        return tacoRepo.findAll(page);
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
    - 

### 2. Sending data to the server

### 3. Updating data on the server

### 4. Deleting data from the server

## 2. Enabling data-backed services

## 3. Consuming REST services

## 4. Summary
