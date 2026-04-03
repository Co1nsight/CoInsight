package com.coanalysis.server.news.application.domain;

import com.coanalysis.server.infrastructure.entity.BaseEntity;
import com.coanalysis.server.news.application.enums.Language;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @Column(name = "TITLE", length = 1000, nullable = false)
    private String title;

    @Column(name = "ORIGINAL_LINK", length = 2500, nullable = false)
    private String originalLink;

    @Column(name = "PUBLISHER", length = 1000)
    private String publisher;

    private LocalDateTime publishedAt;

    @Column(name = "CONTENT", columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(length = 2)
    @Builder.Default
    private Language language = Language.EN;

    public void updateContent(String content) {
        this.content = content;
    }

}
