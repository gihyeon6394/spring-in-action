package com.example.inaction.repo;

import com.example.inaction.entity.Member;
import org.springframework.data.repository.CrudRepository;

public interface MemberRepository extends CrudRepository<Member, Long> {


}
