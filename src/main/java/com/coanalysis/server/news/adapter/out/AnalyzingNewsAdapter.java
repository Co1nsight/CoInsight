package com.coanalysis.server.news.adapter.out;

import com.coanalysis.server.infrastructure.exception.CustomException;
import com.coanalysis.server.infrastructure.exception.ErrorCode;
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

        SentimentAnalysisResult finalResult;
        boolean usedFallback = false;

        try {
            SentimentAnalysisResult bertResult = huggingFaceClient.analyzeSentiment(textToAnalyze);
            finalResult = cryptoKeywordAnalyzer.blendWithKeywords(bertResult, textToAnalyze);
            log.info("Sentiment analysis completed - BERT: {} ({}), Blended: {} ({})",
                    bertResult.getSentiment().getLabel(), String.format("%.2f", bertResult.getScore()),
                    finalResult.getSentiment().getLabel(), String.format("%.2f", finalResult.getScore()));
        } catch (CustomException e) {
            if (e.getErrorCode() == ErrorCode.HUGGINGFACE_QUOTA_EXCEEDED) {
                log.warn("HuggingFace quota exceeded — falling back to keyword-only analysis");
            } else {
                log.warn("BERT analysis failed ({}): {} — falling back to keyword-only analysis",
                        e.getErrorCode(), e.getMessage());
            }
            finalResult = cryptoKeywordAnalyzer.analyzeKeywordsOnly(textToAnalyze);
            usedFallback = true;
        } catch (Exception e) {
            log.warn("BERT analysis unavailable: {} — falling back to keyword-only analysis", e.getMessage());
            finalResult = cryptoKeywordAnalyzer.analyzeKeywordsOnly(textToAnalyze);
            usedFallback = true;
        }

        return NewsAnalysis.builder()
                .sentimentLabel(finalResult.getSentiment().name())
                .sentimentScore(finalResult.getScore())
                .summary(generateSummary(finalResult, usedFallback))
                .build();
    }

    private String buildTextForAnalysis(String title, String content) {
        if (content == null || content.isBlank()) {
            return title;
        }
        return title + ". " + content;
    }

    private String generateSummary(SentimentAnalysisResult result, boolean usedFallback) {
        String method = usedFallback ? "키워드 전용(BERT 폴백)" : "BERT+키워드 혼합";
        return String.format(
                "감성분석 결과: %s (신뢰도: %.1f%%) | 긍정: %.1f%%, 중립: %.1f%%, 부정: %.1f%% [%s]",
                result.getSentiment().getKoreanLabel(),
                result.getScore() * 100,
                result.getPositiveScore() * 100,
                result.getNeutralScore() * 100,
                result.getNegativeScore() * 100,
                method);
    }
}