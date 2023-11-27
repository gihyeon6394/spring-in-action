package com.example.inaction.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Taco")
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true) // force=true: final 필드를 초기화하는 생성자를 생성
@RequiredArgsConstructor
@ToString
public class Taco implements java.io.Serializable {

    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private final String name;
    private final int tacoOrder;
    private final int tacoOrderKey;
    private final LocalDateTime createdAt;


}
