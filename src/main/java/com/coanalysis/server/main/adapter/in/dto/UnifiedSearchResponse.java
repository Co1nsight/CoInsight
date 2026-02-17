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
@Schema(description = "통합 검색 결과 (코인 + 뉴스)")
public class UnifiedSearchResponse {

    @Schema(description = "검색된 코인 목록")
    private List<CryptoResult> cryptos;

    @Schema(description = "검색된 뉴스 목록")
    private List<NewsResult> news;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "코인 검색 결과")
    public static class CryptoResult {

        @Schema(description = "코인 티커 (심볼)", example = "BTC")
        private String ticker;

        @Schema(description = "코인 한글명", example = "비트코인")
        private String name;

        @Schema(description = "코인 로고 URL", example = "https://example.com/btc.png")
        private String logoUrl;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "뉴스 검색 결과")
    public static class NewsResult {

        @Schema(description = "뉴스 고유 ID", example = "1")
        private Long id;

        @Schema(description = "뉴스 제목", example = "비트코인 사상 최고가 경신")
        private String title;

        @Schema(description = "뉴스 출처", example = "TokenPost")
        private String publisher;

        @Schema(description = "발행일시", example = "2025-01-15T09:30:00")
        private LocalDateTime publishedAt;

        @Schema(description = "감성 분석 결과", example = "POSITIVE")
        private String sentimentLabel;
    }
}
