package com.coanalysis.server.crypto.application.domain;

import com.coanalysis.server.infrastructure.entity.BaseEntity;
import com.coanalysis.server.news.application.domain.News;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "CRYPTO_NEWS_MAPPING")
@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CryptoNews extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "crypto_id", nullable = false)
    private Crypto crypto;

    @ManyToOne
    @JoinColumn(name = "news_id", nullable = false)
    private News news;

}
