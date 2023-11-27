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

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-rest</artifactId>
</dependency>
```

```yaml
spring:
  data:
  rest:
  base-path: /data-api # spring data rest의 기본 endpoint를 /data-api로 변경
```

- Spring Data로 생성한 모든 Repository 기반의 REST API를 생성해줌
    - Spring Data JPA, Spring Data MongoDB, Spring Data Neo4j, Spring Data Cassandra, Spring Data Redis, Spring Data
      Elasticsearch, ...
- endpoint를 추가할 필요 없음
- hyperlink 반환 : HATEOAS (Hypermedia as the Engine of Application State) 등을 위해 사용 가능

#### HATEOAS (Hypermedia as the Engine of Application State)

- 사람이 웹사이트 탐색하듯 API를 탐색할 수 있도록 함
- API 응답에 여러 링크가 포함되어있어, 추가 탐색이 가능해짐

### 2.1 Adjusting resource paths and relation names

- 기본적으로 리소스 경로를 repository entity 이름으로 생성
    - e.g. `Idol` entity의 경우, `{base-path}/idols`로 생성
- `/{base-path}` : Spring Data REST의 기본 endpoint
    - 모든 repository entity에 대한 REST endpoint를 반환

```json
{
  "_links": {
    "idols": {
      "href": "http://localhost:8080/rest-api/idols"
    },
    "users": {
      "href": "http://localhost:8080/rest-api/users"
    },
    "members": {
      "href": "http://localhost:8080/rest-api/members"
    },
    "profile": {
      "href": "http://localhost:8080/rest-api/profile"
    }
  }
}
```

````
@RestResource(rel = "rest_members", path = "rest_members")
public class Member {
    ...
}
````

- `@TestResource` : Spring Data REST가 생성하는 REST endpoint의 이름을 변경
    - e.g. `@TestResource(path = "rest_members")` : `Member` entity의 REST endpoint를 `{base-path}/rest_members`로 생성

### 2.2 Paging and sorting

```Bash
GET http://localhost:8080/rest-api/members?size=3&page=1
````

## 3. Consuming REST services

- Microservice 에서 유용
- Spring application 이 API를 제공 + 다른 application API를 호출
- Spring에서 다른 REST API와 상호작용하는 방법
    - _RestTemplate_ : synchronous REST client
    - _Traverson_ : Spring RestTemplate 의 Wrapper
        - SPring HATEOAS가 제공
        - hyperlink-aware, synchronous REST client
    - _WEBClient_ : asynchronous REST client

#### RestTemplate

- REST API호출에 대한 중복 코드를 제거해줌
- 중복 코드 : client, request 객체 생성 -> reqeust 실행 -> response 처리 -> response 객체 매핑 -> 예외 처리
- JDBC Template과 비슷함

| Mehod                  | Description                                  |
|------------------------|----------------------------------------------|
| `delete(...)`          | HTTP DELETE 요청을 보냄                           |
| `exchange(...)`        | HTTP 요청을 보냄<br/>return `ResponseEntity`      |
| `execute(...)`         | HTTP 요청을 보냄<br/>return `Object`              |
| `getForEntity(...)`    | HTTP GET 요청을 보냄<br/>return `ResponseEntity`  |
| `getForObject(...)`    | HTTP GET 요청을 보냄<br/>return `Object`          |
| `headForHeaders(...)`  | HTTP HEAD 요청을 보냄<br/>return `HttpHeaders`    |
| `optionsForAllow(...)` | HTTP OPTIONS 요청을 보냄<br/>return `Allow`       |
| `patchForObject(...)`  | HTTP PATCH 요청을 보냄<br/>return `Object`        |
| `postForEntity(...)`   | HTTP POST 요청을 보냄<br/>return `ResponseEntity` |
| `postForLocation(...)` | HTTP POST 요청을 보냄<br/>return `생성된 리소스 URL`    |
| `postForObject(...)`   | HTTP POST 요청을 보냄<br/>return `Object`         |
| `put(...)`             | HTTP PUT 요청을 보냄                              |

````
RestTemplate restTemplate = new RestTemplate();

...

// Bean으로 등록해서 사용하기
@Bean
public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return new RestTemplate();
}
````

### 3.1 GETing resources

```
@GetMapping("/rest/{id}")
public ResponseEntity<Idol> idolByIdRest(@PathVariable("id") Long id) {
    return restTemplate.getForEntity("http://localhost:8080/api/members/{id}", Idol.class, id);
}
```

### 3.2 PUTing resources

````
public void updateIdol(Idol idol) {
    restTemplate.put("http://localhost:8080/api/members/{id}", idol, idol.getId());
}
````

### 3.3 DELETEing resources

```
public void deleteIdol(Idol idol) {
    restTemplate.delete("http://localhost:8080/api/members/{id}", idol.getId());
}
```

### 3.4 POSTing resources

````
public Idol createIdol(Idol idol) {
    return restTemplate.postForObject("http://localhost:8080/api/members", idol, Idol.class);
}

// 부가 정보 얻기
public java.net.URI createIdol(Idol idol) {
    return restTemplate.postForLocation("http://localhost:8080/api/members", idol);
}

// 생성 객체 + 부가정보 얻기
public Idol createIdol(Idol idol) {
    ResponseEntity<Idol> responseEntity = restTemplate.postForEntity("http://localhost:8080/api/members", idol, Idol.class);
    logger.info("New resource created at " + responseEntity.getHeaders().getLocation());
    return responseEntity.getBody();
}
````

## 4. Summary
