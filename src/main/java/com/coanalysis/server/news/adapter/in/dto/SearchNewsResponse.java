package com.coanalysis.server.news.adapter.in.dto;

import com.coanalysis.server.news.application.domain.News;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Schema(description = "뉴스 검색 결과")
public class SearchNewsResponse {

    @Schema(description = "뉴스 고유 ID", example = "1")
    private Long id;

    @Schema(description = "뉴스 제목", example = "비트코인 사상 최고가 경신, 10만 달러 돌파")
    private String title;

    @Schema(description = "뉴스 발행사", example = "코인데스크")
    private String publisher;

    @Schema(description = "뉴스 발행일시", example = "2025-01-15T09:30:00")
    private LocalDateTime publishedAt;

    @Schema(description = "감성 분석 레이블 (POSITIVE/NEUTRAL/NEGATIVE)", example = "POSITIVE")
    private String sentimentLabel;

    @Schema(description = "감성 분석 신뢰도 점수 (0.0 ~ 1.0)", example = "0.85")
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
