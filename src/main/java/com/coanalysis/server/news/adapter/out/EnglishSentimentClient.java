package com.coanalysis.server.news.adapter.out;

import com.coanalysis.server.infrastructure.exception.CustomException;
import com.coanalysis.server.infrastructure.exception.ErrorCode;
import com.coanalysis.server.news.adapter.out.dto.HuggingFaceRequest;
import com.coanalysis.server.news.adapter.out.dto.HuggingFaceSentimentResult;
import com.coanalysis.server.news.adapter.out.dto.SentimentAnalysisResult;
import com.coanalysis.server.news.application.enums.Sentiment;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class EnglishSentimentClient {

    // BERT 모델은 512 토큰 제한 (영어 약 1500자로 안전하게 설정)
    private static final int CHUNK_SIZE = 1500;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ApiTokenHolder apiTokenHolder;
    private final String defaultApiToken;
    private final String modelId;
    private final String baseUrl;

    public EnglishSentimentClient(
            RestTemplate restTemplate,
            ObjectMapper objectMapper,
            ApiTokenHolder apiTokenHolder,
            @Value("${huggingface.api.token}") String defaultApiToken,
            @Value("${huggingface.api.model.english:ProsusAI/finbert}") String modelId,
            @Value("${huggingface.api.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.apiTokenHolder = apiTokenHolder;
        this.defaultApiToken = defaultApiToken;
        this.modelId = modelId;
        this.baseUrl = baseUrl;
    }

    public SentimentAnalysisResult analyzeSentiment(String text) {
        if (text == null || text.isBlank()) {
            throw new CustomException(ErrorCode.SENTIMENT_ANALYSIS_EMPTY_TEXT);
        }

        // 텍스트를 청크로 분할
        List<String> chunks = splitIntoChunks(text);
        log.debug("Split text into {} chunks for English sentiment analysis", chunks.size());

        // 각 청크별 분석 결과 수집 (청크 길이도 함께 저장)
        List<SentimentAnalysisResult> chunkResults = new ArrayList<>();
        List<Integer> chunkLengths = new ArrayList<>();

        for (int i = 0; i < chunks.size(); i++) {
            try {
                String chunk = chunks.get(i);
                SentimentAnalysisResult result = analyzeChunk(chunk);
                chunkResults.add(result);
                chunkLengths.add(chunk.length());
                log.debug("Chunk {}/{} (length={}) analyzed: {}", i + 1, chunks.size(), chunk.length(), result.getSentiment());
            } catch (Exception e) {
                log.warn("Failed to analyze chunk {}/{}: {}", i + 1, chunks.size(), e.getMessage());
            }
        }

        if (chunkResults.isEmpty()) {
            throw new CustomException(ErrorCode.SENTIMENT_ANALYSIS_FAILED, "All chunk analyses failed");
        }

        // 청크 길이 기반 가중 평균 계산
        return weightedAverageResults(chunkResults, chunkLengths);
    }

    private SentimentAnalysisResult analyzeChunk(String chunk) {
        String url = baseUrl + "/" + modelId;

        String customToken = apiTokenHolder.getToken();
        String tokenToUse = (customToken != null && !customToken.isBlank()) ? customToken : defaultApiToken;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (tokenToUse != null && !tokenToUse.isBlank()) {
            headers.setBearerAuth(tokenToUse);
        }

        HuggingFaceRequest request = HuggingFaceRequest.of(chunk);
        HttpEntity<HuggingFaceRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (response.getBody() == null || response.getBody().isBlank()) {
                throw new CustomException(ErrorCode.SENTIMENT_ANALYSIS_FAILED, "API response is empty.");
            }

            log.debug("English sentiment API raw response: {}", response.getBody());

            List<List<HuggingFaceSentimentResult>> parsedResponse = objectMapper.readValue(
                    response.getBody(),
                    new TypeReference<>() {}
            );

            if (parsedResponse.isEmpty()) {
                throw new CustomException(ErrorCode.SENTIMENT_ANALYSIS_FAILED, "API response parsing result is empty.");
            }

            List<HuggingFaceSentimentResult> results = parsedResponse.get(0);
            return parseResults(results);

        } catch (CustomException e) {
            throw e;
        } catch (RestClientException e) {
            log.error("Failed to call English sentiment API: {}", e.getMessage());
            throw new CustomException(ErrorCode.SENTIMENT_ANALYSIS_API_ERROR, e.getMessage());
        } catch (Exception e) {
            log.error("Failed to parse English sentiment API response: {}", e.getMessage());
            throw new CustomException(ErrorCode.SENTIMENT_ANALYSIS_PARSE_ERROR, e.getMessage());
        }
    }

    private List<String> splitIntoChunks(String text) {
        List<String> chunks = new ArrayList<>();
        if (text.length() <= CHUNK_SIZE) {
            chunks.add(text);
            return chunks;
        }

        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + CHUNK_SIZE, text.length());

            // 문장 경계에서 자르기 시도 (마지막 청크가 아닌 경우)
            if (end < text.length()) {
                int lastPeriod = text.lastIndexOf(". ", end);
                int lastNewline = text.lastIndexOf("\n", end);
                int breakPoint = Math.max(lastPeriod, lastNewline);

                if (breakPoint > start + CHUNK_SIZE / 2) {
                    end = breakPoint + 1;
                }
            }

            chunks.add(text.substring(start, end).trim());
            start = end;
        }

        return chunks;
    }

    /**
     * 청크 길이 기반 가중 평균 계산
     * 글자 수가 많은 청크에 더 큰 가중치 부여
     */
    private SentimentAnalysisResult weightedAverageResults(List<SentimentAnalysisResult> results, List<Integer> lengths) {
        double totalLength = lengths.stream().mapToInt(Integer::intValue).sum();

        double weightedPositive = 0;
        double weightedNeutral = 0;
        double weightedNegative = 0;

        for (int i = 0; i < results.size(); i++) {
            double weight = lengths.get(i) / totalLength;
            weightedPositive += results.get(i).getPositiveScore() * weight;
            weightedNeutral += results.get(i).getNeutralScore() * weight;
            weightedNegative += results.get(i).getNegativeScore() * weight;
        }

        Sentiment sentiment;
        double topScore;

        if (weightedPositive >= weightedNegative && weightedPositive >= weightedNeutral) {
            sentiment = Sentiment.POSITIVE;
            topScore = weightedPositive;
        } else if (weightedNegative >= weightedPositive && weightedNegative >= weightedNeutral) {
            sentiment = Sentiment.NEGATIVE;
            topScore = weightedNegative;
        } else {
            sentiment = Sentiment.NEUTRAL;
            topScore = weightedNeutral;
        }

        log.info("Weighted average of {} chunks: positive={}, neutral={}, negative={}, final={}",
                results.size(), weightedPositive, weightedNeutral, weightedNegative, sentiment);

        return SentimentAnalysisResult.builder()
                .sentiment(sentiment)
                .score(topScore)
                .positiveScore(weightedPositive)
                .neutralScore(weightedNeutral)
                .negativeScore(weightedNegative)
                .build();
    }

    private SentimentAnalysisResult parseResults(List<HuggingFaceSentimentResult> results) {
        double rawPositive = 0.0;
        double rawNeutral = 0.0;
        double rawNegative = 0.0;

        for (HuggingFaceSentimentResult result : results) {
            String label = result.getLabel().toLowerCase();
            Double score = result.getScore();

            switch (label) {
                case "positive" -> rawPositive = score;
                case "neutral" -> rawNeutral = score;
                case "negative" -> rawNegative = score;
            }
        }

        double polarSum = rawPositive + rawNegative;
        double adjustedNeutral = 0.01 * rawNeutral;
        double adjustedPositive;
        double adjustedNegative;

        if (polarSum > 0) {
            adjustedPositive = 0.99 * (rawPositive / polarSum);
            adjustedNegative = 0.99 * (rawNegative / polarSum);
        } else {
            adjustedPositive = 0.495;
            adjustedNegative = 0.495;
        }

        Sentiment sentiment;
        double topScore;

        if (adjustedPositive >= adjustedNegative && adjustedPositive >= adjustedNeutral) {
            sentiment = Sentiment.POSITIVE;
            topScore = adjustedPositive;
        } else if (adjustedNegative >= adjustedPositive && adjustedNegative >= adjustedNeutral) {
            sentiment = Sentiment.NEGATIVE;
            topScore = adjustedNegative;
        } else {
            sentiment = Sentiment.NEUTRAL;
            topScore = adjustedNeutral;
        }

        return SentimentAnalysisResult.builder()
                .sentiment(sentiment)
                .score(topScore)
                .positiveScore(adjustedPositive)
                .neutralScore(adjustedNeutral)
                .negativeScore(adjustedNegative)
                .build();
    }

}
