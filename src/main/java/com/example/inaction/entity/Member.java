package com.example.inaction.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.rest.core.annotation.RestResource;

import java.time.LocalDateTime;

@Entity
@Table(name = "Member")
@Data
@NoArgsConstructor
@AllArgsConstructor
//@RestResource(rel = "rest_members", path = "rest_members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private LocalDateTime createdAt;
    private int age;
    private String userName;
    private String password;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "id_idol")
    private Idol idol;

    @Override
    public String toString() {
        return ToStringBuilder
                .reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
