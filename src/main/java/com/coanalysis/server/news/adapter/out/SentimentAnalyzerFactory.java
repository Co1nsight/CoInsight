package com.coanalysis.server.news.adapter.out;

import com.coanalysis.server.news.adapter.out.dto.SentimentAnalysisResult;
import com.coanalysis.server.news.application.enums.Language;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SentimentAnalyzerFactory {

    private final HuggingFaceClient koreanClient;
    private final EnglishSentimentClient englishClient;

    public SentimentAnalysisResult analyze(String text, Language language) {
        log.debug("Analyzing sentiment for {} text", language.getDisplayName());

        return switch (language) {
            case KO -> koreanClient.analyzeSentiment(text);
            case EN -> englishClient.analyzeSentiment(text);
        };
    }
}
