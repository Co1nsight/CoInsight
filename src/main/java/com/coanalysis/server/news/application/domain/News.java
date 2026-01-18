package com.coanalysis.server.news.application.domain;

import com.coanalysis.server.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "NEWS")
@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class News extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

}
