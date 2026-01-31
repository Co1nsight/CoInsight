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

import java.util.List;

@Slf4j
@Component
public class HuggingFaceClient {

    private static final int MAX_INPUT_LENGTH = 512;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ApiTokenHolder apiTokenHolder;
    private final String defaultApiToken;
    private final String modelId;
    private final String baseUrl;

    public HuggingFaceClient(
            RestTemplate restTemplate,
            ObjectMapper objectMapper,
            ApiTokenHolder apiTokenHolder,
            @Value("${huggingface.api.token}") String defaultApiToken,
            @Value("${huggingface.api.model}") String modelId,
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

        String truncatedText = truncateText(text);
        String url = baseUrl + "/" + modelId;

        // ThreadLocal에서 토큰을 가져오고, 없으면 서버 기본 토큰 사용
        String customToken = apiTokenHolder.getToken();
        String tokenToUse = (customToken != null && !customToken.isBlank()) ? customToken : defaultApiToken;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (tokenToUse != null && !tokenToUse.isBlank()) {
            headers.setBearerAuth(tokenToUse);
        }

        HuggingFaceRequest request = HuggingFaceRequest.of(truncatedText);
        HttpEntity<HuggingFaceRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (response.getBody() == null || response.getBody().isBlank()) {
                throw new CustomException(ErrorCode.SENTIMENT_ANALYSIS_FAILED, "API 응답이 비어있습니다.");
            }

            log.info("HuggingFace API raw response: {}", response.getBody());

            List<List<HuggingFaceSentimentResult>> parsedResponse = objectMapper.readValue(
                    response.getBody(),
                    new TypeReference<>() {}
            );

            if (parsedResponse.isEmpty()) {
                throw new CustomException(ErrorCode.SENTIMENT_ANALYSIS_FAILED, "API 응답 파싱 결과가 비어있습니다.");
            }

            List<HuggingFaceSentimentResult> results = parsedResponse.get(0);
            log.info("HuggingFace API response labels: {}", results.stream()
                    .map(r -> r.getLabel() + "=" + r.getScore())
                    .toList());
            return parseResults(results);

        } catch (CustomException e) {
            throw e;
        } catch (RestClientException e) {
            log.error("Failed to call HuggingFace API: {}", e.getMessage());
            throw new CustomException(ErrorCode.SENTIMENT_ANALYSIS_API_ERROR, e.getMessage());
        } catch (Exception e) {
            log.error("Failed to parse HuggingFace API response: {}", e.getMessage());
            throw new CustomException(ErrorCode.SENTIMENT_ANALYSIS_PARSE_ERROR, e.getMessage());
        }
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

        // neutral 비중을 1%로 고정하고, 나머지 99%를 positive/negative 비율대로 재분배
        double adjustedNeutral = 0.01 * rawNeutral;
        double polarSum = rawPositive + rawNegative;

        double adjustedPositive;
        double adjustedNegative;

        if (polarSum > 0) {
            adjustedPositive = 0.99 * (rawPositive / polarSum);
            adjustedNegative = 0.99 * (rawNegative / polarSum);
        } else {
            adjustedPositive = 0.495;
            adjustedNegative = 0.495;
        }

        // 조정된 점수 중 가장 높은 것을 sentiment로 결정
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

    private String truncateText(String text) {
        if (text.length() <= MAX_INPUT_LENGTH) {
            return text;
        }
        return text.substring(0, MAX_INPUT_LENGTH);
    }
}
