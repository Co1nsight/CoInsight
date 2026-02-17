package com.coanalysis.server.news.adapter.in.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "뉴스 상세 정보 및 AI 분석 결과")
public class NewsDetailResponse {

    @Schema(description = "뉴스 고유 ID", example = "1")
    private Long id;

    @Schema(description = "뉴스 제목", example = "비트코인 사상 최고가 경신, 10만 달러 돌파")
    private String title;

    @Schema(description = "뉴스 본문 내용", example = "비트코인이 10만 달러를 돌파하며 사상 최고가를 기록했다...")
    private String content;

    @Schema(description = "뉴스 출처 (발행사)", example = "코인데스크")
    private String publisher;

    @Schema(description = "뉴스 발행일시", example = "2025-01-15T09:30:00")
    private LocalDateTime publishedAt;

    @Schema(description = "원문 링크", example = "https://coindesk.com/news/123")
    private String originalLink;

    @Schema(description = "뉴스 언어 (EN: 영문, KO: 한국어)", example = "KO")
    private String language;

    @Schema(description = "AI 분석 결과")
    private AnalysisResult analysis;

    @Schema(description = "관련 코인 목록")
    private List<RelatedCrypto> relatedCryptos;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "AI 감성 분석 결과")
    public static class AnalysisResult {

        @Schema(description = "감성 레이블 (POSITIVE: 호재, NEGATIVE: 악재, NEUTRAL: 중립)", example = "POSITIVE")
        private String sentimentLabel;

        @Schema(description = "분석 신뢰도 (0.0 ~ 1.0)", example = "0.85")
        private Double sentimentScore;

        @Schema(description = "분석 요약", example = "감성분석 결과: 긍정 (신뢰도: 85.0%) | 긍정: 85.0%, 중립: 10.0%, 부정: 5.0%")
        private String summary;
    }

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
