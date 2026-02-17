package com.coanalysis.server.prediction.adapter.in.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "최근 뉴스 응답")
public class RecentNewsResponse {

    @Schema(description = "뉴스 ID", example = "1")
    private Long newsId;

    @Schema(description = "뉴스 제목", example = "Bitcoin surges to new highs")
    private String title;

    @Schema(description = "출처", example = "CoinDesk")
    private String publisher;

    @Schema(description = "발행일", example = "2024-01-15T08:30:00")
    private LocalDateTime publishedAt;

    @Schema(description = "감성 라벨", example = "positive")
    private String sentimentLabel;

    @Schema(description = "감성 점수 (-1 ~ 1)", example = "0.85")
    private Double sentimentScore;

    public String getSentimentDisplayLabel() {
        if (sentimentLabel == null) {
            return "분석중";
        }
        return switch (sentimentLabel.toLowerCase()) {
            case "positive" -> "호재";
            case "negative" -> "악재";
            default -> "중립";
        };
    }
}
