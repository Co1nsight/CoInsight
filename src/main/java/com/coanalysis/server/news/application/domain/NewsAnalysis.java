package com.coanalysis.server.news.application.domain;

import com.coanalysis.server.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "NEWS_ANALYSIS")
@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NewsAnalysis extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne
    @JoinColumn(name = "news_id", nullable = false)
    private News news;

    private String summary;

    private Double sentimentScore;

    private String sentimentLabel;

}
