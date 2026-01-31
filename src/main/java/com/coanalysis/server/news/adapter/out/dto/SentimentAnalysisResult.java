package com.coanalysis.server.news.adapter.out.dto;

import com.coanalysis.server.news.application.enums.Sentiment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SentimentAnalysisResult {

    private Sentiment sentiment;
    private Double score;
    private Double positiveScore;
    private Double neutralScore;
    private Double negativeScore;

    public static SentimentAnalysisResult empty() {
        return SentimentAnalysisResult.builder()
                .sentiment(Sentiment.NEUTRAL)
                .score(0.0)
                .positiveScore(0.0)
                .neutralScore(1.0)
                .negativeScore(0.0)
                .build();
    }
}