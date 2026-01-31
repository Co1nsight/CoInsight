package com.coanalysis.server.news.adapter.out;

import com.coanalysis.server.news.adapter.out.dto.SentimentAnalysisResult;
import com.coanalysis.server.news.application.domain.NewsAnalysis;
import com.coanalysis.server.news.application.port.out.AnalyzingNewsPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AnalyzingNewsAdapter implements AnalyzingNewsPort {

    private final HuggingFaceClient huggingFaceClient;

    @Override
    public NewsAnalysis analyzeContent(String title, String content) {
        String textToAnalyze = buildTextForAnalysis(title, content);

        SentimentAnalysisResult result = huggingFaceClient.analyzeSentiment(textToAnalyze);

        log.info("Sentiment analysis completed - Label: {}, Score: {}",
                result.getSentiment().getLabel(),
                result.getScore());

        return NewsAnalysis.builder()
                .sentimentLabel(result.getSentiment().name())
                .sentimentScore(result.getScore())
                .summary(generateSummary(result))
                .build();
    }

    private String buildTextForAnalysis(String title, String content) {
        if (content == null || content.isBlank()) {
            return title;
        }
        return title + ". " + content;
    }

    private String generateSummary(SentimentAnalysisResult result) {
        return String.format("감성분석 결과: %s (신뢰도: %.1f%%) | 긍정: %.1f%%, 중립: %.1f%%, 부정: %.1f%%",
                result.getSentiment().getKoreanLabel(),
                result.getScore() * 100,
                result.getPositiveScore() * 100,
                result.getNeutralScore() * 100,
                result.getNegativeScore() * 100);
    }
}