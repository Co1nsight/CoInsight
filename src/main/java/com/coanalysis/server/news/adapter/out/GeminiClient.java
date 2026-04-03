package com.coanalysis.server.news.adapter.out;

import com.coanalysis.server.news.adapter.out.dto.GeminiSummaryResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class GeminiClient {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String baseUrl;
    private final String model;

    public GeminiClient(
            RestTemplate restTemplate,
            @Value("${gemini.api.key}") String apiKey,
            @Value("${gemini.api.base-url:https://generativelanguage.googleapis.com}") String baseUrl,
            @Value("${gemini.api.model:gemini-2.0-flash}") String model) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.model = model;
    }

    public GeminiSummaryResult summarizeNews(String title, String content, List<String> availableTickers) {
        String prompt = buildPrompt(title, content, availableTickers);
        String url = baseUrl + "/v1beta/models/" + model + ":generateContent?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                )
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            if (response.getBody() == null) {
                log.warn("Gemini API 응답이 비어있습니다.");
                return null;
            }

            String rawText = extractText(response.getBody());
            if (rawText == null) return null;

            return parseResult(rawText, availableTickers);

        } catch (Exception e) {
            log.error("Gemini API 호출 실패: {}", e.getMessage());
            return null;
        } finally {
            // 분당 15건 제한 대응: 요청 후 무조건 4초 대기
            try {
                Thread.sleep(4000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private GeminiSummaryResult parseResult(String rawText, List<String> availableTickers) {
        String summary = rawText;
        Set<String> tickers = new HashSet<>();

        // [관련코인]: BTC, ETH 형태의 마지막 줄 파싱
        int idx = rawText.lastIndexOf("[관련코인]:");
        if (idx != -1) {
            String tickerLine = rawText.substring(idx + "[관련코인]:".length()).trim();
            summary = rawText.substring(0, idx).trim();

            Set<String> validTickers = new HashSet<>(availableTickers);
            Arrays.stream(tickerLine.split(","))
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .filter(validTickers::contains)
                    .forEach(tickers::add);
        }

        return new GeminiSummaryResult(summary, tickers);
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map responseBody) {
        try {
            List<Map> candidates = (List<Map>) responseBody.get("candidates");
            if (candidates == null || candidates.isEmpty()) return null;

            Map content = (Map) candidates.get(0).get("content");
            if (content == null) return null;

            List<Map> parts = (List<Map>) content.get("parts");
            if (parts == null || parts.isEmpty()) return null;

            return (String) parts.get(0).get("text");
        } catch (Exception e) {
            log.error("Gemini 응답 파싱 실패: {}", e.getMessage());
            return null;
        }
    }

    private String buildPrompt(String title, String content, List<String> availableTickers) {
        String tickerList = availableTickers.stream()
                .limit(50)
                .collect(Collectors.joining(", "));

        return String.format("""
                너는 암호화폐 전문 번역가이자 요약 AI야.
                아래 영어(또는 한국어) 코인 뉴스를 읽고, 반드시 아래 출력 형식을 그대로 따라 작성해줘.
                형식 외의 내용(인사말, 설명, 추가 코멘트)은 절대 포함하지 마.
                각 섹션 사이에는 반드시 빈 줄을 하나 넣을 것.

                출력 형식:
                **[제목: 20자 이내]**

                - [핵심 사실: 무슨 일이 일어났는가]
                - [상세 배경: 왜 일어났는가 / 어떤 수치가 나왔는가]
                - [시장 영향: 이 뉴스가 시장/가격에 미칠 영향]

                **AI 시나리오 분석**

                - [상승 시나리오]: [호재로 작용할 경우의 기대 효과 1~2문장]

                - [리스크 점검]: [투자 시 주의해야 할 악재 요소 1~2문장]

                [관련코인]: BTC, ETH (관련 코인 없으면 [관련코인]: 없음)

                규칙:
                - 문체는 깔끔하고 전문적인 평어체(~음/함, ~임)를 사용할 것
                - 사용 가능한 코인 목록에서만 관련 코인을 선택할 것

                사용 가능한 코인 목록: %s

                ---
                제목: %s
                내용: %s
                """, tickerList, title, content != null ? content : "");
    }
}