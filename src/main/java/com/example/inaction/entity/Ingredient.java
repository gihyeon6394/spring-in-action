package com.example.inaction.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Ingredient")
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true) // force=true: final 필드를 초기화하는 생성자를 생성
@RequiredArgsConstructor
@ToString
public class Ingredient implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private final String name;
    private final String type;
}
