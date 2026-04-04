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
    private final CryptoKeywordAnalyzer cryptoKeywordAnalyzer;

    @Override
    public NewsAnalysis analyzeContent(String title, String content) {
        String textToAnalyze = buildTextForAnalysis(title, content);

        SentimentAnalysisResult bertResult = huggingFaceClient.analyzeSentiment(textToAnalyze);
        SentimentAnalysisResult blendedResult = cryptoKeywordAnalyzer.blendWithKeywords(bertResult, textToAnalyze);

        log.info("Sentiment analysis completed - BERT: {} ({:.2f}), Blended: {} ({:.2f})",
                bertResult.getSentiment().getLabel(), bertResult.getScore(),
                blendedResult.getSentiment().getLabel(), blendedResult.getScore());

        return NewsAnalysis.builder()
                .sentimentLabel(blendedResult.getSentiment().name())
                .sentimentScore(blendedResult.getScore())
                .summary(generateSummary(bertResult, blendedResult))
                .build();
    }

    private String buildTextForAnalysis(String title, String content) {
        if (content == null || content.isBlank()) {
            return title;
        }
        return title + ". " + content;
    }

    private String generateSummary(SentimentAnalysisResult bertResult, SentimentAnalysisResult blendedResult) {
        return String.format(
                "감성분석 결과: %s (신뢰도: %.1f%%) | 긍정: %.1f%%, 중립: %.1f%%, 부정: %.1f%% [BERT: %s %.1f%% / 키워드 혼합 적용]",
                blendedResult.getSentiment().getKoreanLabel(),
                blendedResult.getScore() * 100,
                blendedResult.getPositiveScore() * 100,
                blendedResult.getNeutralScore() * 100,
                blendedResult.getNegativeScore() * 100,
                bertResult.getSentiment().getKoreanLabel(),
                bertResult.getScore() * 100);
    }
}