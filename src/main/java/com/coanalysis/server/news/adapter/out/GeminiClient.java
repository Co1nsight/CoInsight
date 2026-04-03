package com.coanalysis.server.news.adapter.out;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

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
            @Value("${gemini.api.model:gemini-1.5-flash}") String model) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.model = model;
    }

    public String summarizeNews(String title, String content) {
        String prompt = buildPrompt(title, content);
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

            return extractText(response.getBody());

        } catch (Exception e) {
            log.error("Gemini API 호출 실패: {}", e.getMessage());
            return null;
        }
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

    private String buildPrompt(String title, String content) {
        return String.format("""
                너는 암호화폐 전문 번역가이자 요약 AI야.
                아래 영어(또는 한국어) 코인 뉴스를 읽고, 반드시 다음 규칙에 따라 한국어로 분석 및 요약해줘.

                1. 제목: 20자 이내로 직관적이고 핵심을 찌르는 제목을 작성할 것.
                2. 3줄 요약: 반드시 3개의 글머리 기호(-)를 사용해 다음 내용을 작성할 것.
                   - 첫 번째 줄 (핵심 사실): 무슨 일이 일어났는가
                   - 두 번째 줄 (상세 배경): 왜 일어났는가 / 어떤 수치가 나왔는가
                   - 세 번째 줄 (시장 영향): 이 뉴스가 시장/가격에 미칠 영향
                3. AI 시나리오 분석: 3줄 요약 아래에 다음 두 가지 관점을 각각 1~2문장으로 추가할 것.
                   - [상승 시나리오]: 이 뉴스가 호재로 작용할 경우의 기대 효과
                   - [리스크 점검]: 반대 상황이거나 투자 시 주의해야 할 악재 요소
                4. 문체: 깔끔하고 전문적인 평어체(~음/함, ~임)를 사용할 것. 쓸데없는 인사말이나 서론은 절대 넣지 마.

                ---
                제목: %s
                내용: %s
                """, title, content != null ? content : "");
    }
}
