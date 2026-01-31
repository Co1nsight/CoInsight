package com.coanalysis.server.news.adapter.in.dto;

import com.coanalysis.server.news.application.domain.News;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SearchNewsResponse {

    private Long id;
    private String title;
    private String publisher;
    private LocalDateTime publishedAt;
    private String sentimentLabel;
    private Double sentimentScore;

    public static SearchNewsResponse of(News news) {
        return SearchNewsResponse.builder()
                .id(news.getId())
                .title(news.getTitle())
                .publisher(news.getPublisher())
                .publishedAt(news.getPublishedAt())
                .build();
    }

}
