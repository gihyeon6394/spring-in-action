package com.example.inaction.repo;

import com.example.inaction.entity.Idol;
import com.example.inaction.entity.Member;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;

public interface IdolRepository extends CrudRepository<Idol, Long> {


    Iterable<Idol> findAllBy(PageRequest page);
}
