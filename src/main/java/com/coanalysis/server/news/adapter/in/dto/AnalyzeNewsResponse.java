package com.coanalysis.server.news.adapter.in.dto;

import com.coanalysis.server.news.application.domain.NewsAnalysis;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "뉴스 감성 분석 결과")
public class AnalyzeNewsResponse {

    @Schema(description = "감성 레이블", example = "POSITIVE")
    private String sentimentLabel;

    @Schema(description = "감성 점수 (0.0 ~ 1.0)", example = "0.85")
    private Double sentimentScore;

    @Schema(description = "분석 요약", example = "감성분석 결과: 긍정 (신뢰도: 85.0%) | 긍정: 85.0%, 중립: 10.0%, 부정: 5.0%")
    private String summary;

    public static AnalyzeNewsResponse of(NewsAnalysis newsAnalysis) {
        return AnalyzeNewsResponse.builder()
                .sentimentLabel(newsAnalysis.getSentimentLabel())
                .sentimentScore(newsAnalysis.getSentimentScore())
                .summary(newsAnalysis.getSummary())
                .build();
    }
}