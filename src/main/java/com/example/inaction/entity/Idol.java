package com.example.inaction.entity;


import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Idol")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Idol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private LocalDateTime createdAt;
    private int cntMember;

    @OneToMany(mappedBy = "idol", cascade = CascadeType.ALL)
    private List<Member> members = new ArrayList<>();

    public void addIdol(Member member) {
        member.setIdol(this);
        this.members.add(member);
        this.cntMember = this.members.size();
    }

    @Override
    public String toString() {
        return ToStringBuilder
                .reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }


}
