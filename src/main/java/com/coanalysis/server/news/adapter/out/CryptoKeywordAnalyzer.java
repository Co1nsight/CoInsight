package com.coanalysis.server.news.adapter.out;

import com.coanalysis.server.news.adapter.out.dto.SentimentAnalysisResult;
import com.coanalysis.server.news.application.enums.Sentiment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 한국어 암호화폐 뉴스 키워드 기반 감성 분석기.
 * BERT 모델 분석 결과(70%)와 키워드 점수(30%)를 혼합하여 최종 감성 점수를 산출한다.
 */
@Slf4j
@Component
public class CryptoKeywordAnalyzer {

    private static final double BERT_WEIGHT = 0.7;
    private static final double KEYWORD_WEIGHT = 0.3;

    /** 호재(Bullish) 키워드 맵 — 가중치: 0.5 ~ 2.0 */
    private static final Map<String, Double> BULLISH_KEYWORDS = Map.ofEntries(
            // 가격 상승 직접 표현
            Map.entry("급등",       2.0),
            Map.entry("폭등",       2.0),
            Map.entry("신고가",     2.0),
            Map.entry("최고가",     1.8),
            Map.entry("최고치",     1.8),
            Map.entry("상승",       1.2),
            Map.entry("급상승",     1.8),
            Map.entry("반등",       1.5),
            Map.entry("회복",       1.3),
            Map.entry("돌파",       1.5),
            // ETF / 기관 투자
            Map.entry("현물ETF",    2.0),
            Map.entry("ETF승인",    2.0),
            Map.entry("ETF유입",    1.8),
            Map.entry("기관투자",   1.8),
            Map.entry("기관매수",   1.8),
            Map.entry("자금유입",   1.5),
            Map.entry("순유입",     1.5),
            Map.entry("블랙록",     1.5),
            Map.entry("기관채택",   1.8),
            // 규제 완화 / 제도화
            Map.entry("규제완화",   1.8),
            Map.entry("제도화",     1.5),
            Map.entry("합법화",     1.8),
            Map.entry("승인",       1.3),
            Map.entry("허용",       1.2),
            Map.entry("친암호화폐", 1.8),
            Map.entry("규제명확화", 1.5),
            // 시장 긍정 심리 / 기술적 신호
            Map.entry("강세장",     1.5),
            Map.entry("불장",       1.8),
            Map.entry("매수세",     1.3),
            Map.entry("랠리",       1.5),
            Map.entry("상승세",     1.3),
            Map.entry("호재",       1.5),
            Map.entry("호조",       1.2),
            Map.entry("긍정적",     0.8),
            Map.entry("강세",       1.2),
            // 반감기 / 이벤트
            Map.entry("반감기",     1.8),
            Map.entry("고래매수",   1.5),
            Map.entry("누적",       0.8),
            // 거래소 상장 / 파트너십
            Map.entry("상장",       1.2),
            Map.entry("파트너십",   1.0),
            Map.entry("협력",       0.8),
            Map.entry("업무협약",   1.0),
            Map.entry("채택",       1.3),
            // 기술 발전
            Map.entry("업그레이드", 1.0),
            Map.entry("메인넷",     1.0),
            Map.entry("개발완료",   1.0),
            Map.entry("확장성",     0.8),
            // 국가 채택
            Map.entry("전략비축",   2.0),
            Map.entry("국가채택",   2.0),
            Map.entry("법정화폐",   1.8),
            Map.entry("성장",       0.8),
            Map.entry("흑자",       1.0)
    );

    /** 악재(Bearish) 키워드 맵 — 가중치: 0.5 ~ 2.0 */
    private static final Map<String, Double> BEARISH_KEYWORDS = Map.ofEntries(
            // 가격 하락 직접 표현
            Map.entry("급락",       2.0),
            Map.entry("폭락",       2.0),
            Map.entry("하락",       1.2),
            Map.entry("급하락",     1.8),
            Map.entry("붕괴",       1.8),
            Map.entry("추락",       1.5),
            Map.entry("하락세",     1.3),
            Map.entry("조정",       1.0),
            Map.entry("약세장",     1.5),
            Map.entry("침체",       1.3),
            Map.entry("크립토윈터", 2.0),
            Map.entry("공포",       1.5),
            // 규제 강화
            Map.entry("규제강화",   1.8),
            Map.entry("금지",       2.0),
            Map.entry("거래금지",   2.0),
            Map.entry("불법",       1.8),
            Map.entry("제재",       1.5),
            Map.entry("단속",       1.5),
            Map.entry("과세강화",   1.5),
            Map.entry("규제압박",   1.8),
            // 해킹 / 보안 사고
            Map.entry("해킹",       2.0),
            Map.entry("탈취",       1.8),
            Map.entry("도난",       1.8),
            Map.entry("취약점",     1.3),
            Map.entry("보안사고",   1.8),
            Map.entry("북한해커",   2.0),
            // 스캠 / 사기
            Map.entry("스캠",       2.0),
            Map.entry("사기",       1.8),
            Map.entry("러그풀",     2.0),
            Map.entry("폰지",       1.8),
            Map.entry("먹튀",       2.0),
            Map.entry("투자사기",   1.8),
            // 거래소 위기
            Map.entry("파산",       1.8),
            Map.entry("파산신청",   2.0),
            Map.entry("유동성위기", 1.8),
            Map.entry("인출중단",   1.8),
            Map.entry("출금정지",   1.8),
            Map.entry("상장폐지",   1.5),
            // 매도 / 청산 압력
            Map.entry("청산",       1.5),
            Map.entry("대규모청산", 1.8),
            Map.entry("매도세",     1.3),
            Map.entry("자금유출",   1.5),
            Map.entry("순유출",     1.5),
            Map.entry("이탈",       1.2),
            // 부정 심리
            Map.entry("악재",       1.5),
            Map.entry("우려",       0.8),
            Map.entry("불확실성",   1.0),
            Map.entry("변동성확대", 1.0),
            Map.entry("부정적",     0.8),
            Map.entry("위험",       0.8),
            Map.entry("손실",       1.0),
            Map.entry("적자",       1.0)
    );

    /**
     * BERT 분석 결과와 키워드 점수를 혼합하여 최종 감성 점수를 반환한다.
     * 키워드가 하나도 매칭되지 않으면 BERT 결과를 그대로 사용한다.
     *
     * @param bertResult BERT 모델 분석 결과
     * @param text       분석 대상 텍스트 (제목 + 본문)
     * @return BERT 70% + 키워드 30% 혼합 결과
     */
    public SentimentAnalysisResult blendWithKeywords(SentimentAnalysisResult bertResult, String text) {
        if (text == null || text.isBlank()) {
            return bertResult;
        }

        double bullishScore = 0.0;
        double bearishScore = 0.0;
        List<String> matchedBullish = new ArrayList<>();
        List<String> matchedBearish = new ArrayList<>();

        for (Map.Entry<String, Double> entry : BULLISH_KEYWORDS.entrySet()) {
            if (text.contains(entry.getKey())) {
                bullishScore += entry.getValue();
                matchedBullish.add(entry.getKey() + "(" + entry.getValue() + ")");
            }
        }
        for (Map.Entry<String, Double> entry : BEARISH_KEYWORDS.entrySet()) {
            if (text.contains(entry.getKey())) {
                bearishScore += entry.getValue();
                matchedBearish.add(entry.getKey() + "(" + entry.getValue() + ")");
            }
        }

        double total = bullishScore + bearishScore;
        if (total == 0) {
            log.debug("No crypto keywords matched — using BERT score as-is");
            return bertResult;
        }

        double keywordPositive = bullishScore / total;
        double keywordNegative = bearishScore / total;

        log.debug("Keyword analysis — bullish: {} ({}), bearish: {} ({})",
                bullishScore, matchedBullish, bearishScore, matchedBearish);

        double blendedPositive = bertResult.getPositiveScore() * BERT_WEIGHT + keywordPositive * KEYWORD_WEIGHT;
        double blendedNegative = bertResult.getNegativeScore() * BERT_WEIGHT + keywordNegative * KEYWORD_WEIGHT;
        double blendedNeutral  = bertResult.getNeutralScore()  * BERT_WEIGHT;

        Sentiment blendedSentiment;
        double topScore;
        if (blendedPositive >= blendedNegative && blendedPositive >= blendedNeutral) {
            blendedSentiment = Sentiment.POSITIVE;
            topScore = blendedPositive;
        } else if (blendedNegative >= blendedPositive && blendedNegative >= blendedNeutral) {
            blendedSentiment = Sentiment.NEGATIVE;
            topScore = blendedNegative;
        } else {
            blendedSentiment = Sentiment.NEUTRAL;
            topScore = blendedNeutral;
        }

        log.info("Keyword blend — BERT: {}({:.2f}) → Blended: {}({:.2f}) | bullish={:.2f}, bearish={:.2f}",
                bertResult.getSentiment(), bertResult.getScore(),
                blendedSentiment, topScore, bullishScore, bearishScore);

        return SentimentAnalysisResult.builder()
                .sentiment(blendedSentiment)
                .score(topScore)
                .positiveScore(blendedPositive)
                .neutralScore(blendedNeutral)
                .negativeScore(blendedNegative)
                .build();
    }
}
