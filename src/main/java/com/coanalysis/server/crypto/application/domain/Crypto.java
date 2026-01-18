package com.coanalysis.server.crypto.application.domain;

import com.coanalysis.server.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "CRYPTO")
@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Crypto extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

}
