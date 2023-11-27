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
