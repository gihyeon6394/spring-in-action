package com.example.inaction;

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
@RequestMapping(path = "/api/tacos" // request URI : /api/tacos/**
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
