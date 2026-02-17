package com.coanalysis.server.main.adapter.in.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "메인화면 뉴스 정보")
public class MainNewsResponse {

    @Schema(description = "뉴스 고유 ID", example = "1")
    private Long id;

    @Schema(description = "뉴스 제목", example = "비트코인 사상 최고가 경신, 10만 달러 돌파")
    private String title;

    @Schema(description = "뉴스 출처 (발행사)", example = "TokenPost")
    private String publisher;

    @Schema(description = "뉴스 간략 내용 (최대 100자)", example = "비트코인이 10만 달러를 돌파하며 사상 최고가를...")
    private String contentSnippet;

    @Schema(description = "뉴스 발행일시", example = "2025-01-15T09:30:00")
    private LocalDateTime publishedAt;

    @Schema(description = "뉴스 언어 (EN/KO)", example = "KO")
    private String language;

    @Schema(description = "감성 분석 결과 (POSITIVE: 호재, NEGATIVE: 악재, NEUTRAL: 중립)", example = "POSITIVE")
    private String sentimentLabel;

    @Schema(description = "분석 신뢰도 (0.0 ~ 1.0)", example = "0.85")
    private Double sentimentScore;

    @Schema(description = "관련 코인 목록")
    private List<RelatedCrypto> relatedCryptos;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "관련 코인 정보")
    public static class RelatedCrypto {

        @Schema(description = "코인 티커 (심볼)", example = "BTC")
        private String ticker;

        @Schema(description = "코인 한글명", example = "비트코인")
        private String name;

        @Schema(description = "코인 로고 URL", example = "https://example.com/btc.png")
        private String logoUrl;
    }
}
