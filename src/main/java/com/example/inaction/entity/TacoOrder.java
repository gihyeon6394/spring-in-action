package com.example.inaction.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Taco_Order")
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true) // force=true: final 필드를 초기화하는 생성자를 생성
@RequiredArgsConstructor
@ToString
public class TacoOrder implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private final String deliveryName;
    private final String deliveryStreet;
    private final String deliveryCity;
    private final String deliveryState;
    private final String deliveryZip;
    private final String ccNumber;
    private final String ccExpiration;
    private final String ccCVV;
    private final LocalDateTime placedAt;


}
