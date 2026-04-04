package com.coanalysis.server.news.adapter.out;

import com.coanalysis.server.news.adapter.out.dto.SentimentAnalysisResult;
import com.coanalysis.server.news.application.enums.Sentiment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 한국어/영어 암호화폐 뉴스 키워드 기반 감성 분석기.
 *
 * <p>두 가지 용도로 사용된다:
 * <ul>
 *   <li>{@link #blendWithKeywords}: BERT 결과(70%)와 키워드 점수(30%)를 혼합</li>
 *   <li>{@link #analyzeKeywordsOnly}: BERT 서버 장애/토큰 소진 시 키워드만으로 감성 판단 (폴백)</li>
 * </ul>
 */
@Slf4j
@Component
public class CryptoKeywordAnalyzer {

    private static final double BERT_WEIGHT    = 0.7;
    private static final double KEYWORD_WEIGHT = 0.3;

    // ── 한국어 호재(Bullish) ──────────────────────────────────────────────────
    private static final Map<String, Double> KO_BULLISH = Map.ofEntries(
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
            Map.entry("현물ETF",    2.0),
            Map.entry("ETF승인",    2.0),
            Map.entry("ETF유입",    1.8),
            Map.entry("기관투자",   1.8),
            Map.entry("기관매수",   1.8),
            Map.entry("자금유입",   1.5),
            Map.entry("순유입",     1.5),
            Map.entry("블랙록",     1.5),
            Map.entry("기관채택",   1.8),
            Map.entry("규제완화",   1.8),
            Map.entry("제도화",     1.5),
            Map.entry("합법화",     1.8),
            Map.entry("승인",       1.3),
            Map.entry("허용",       1.2),
            Map.entry("친암호화폐", 1.8),
            Map.entry("규제명확화", 1.5),
            Map.entry("강세장",     1.5),
            Map.entry("불장",       1.8),
            Map.entry("매수세",     1.3),
            Map.entry("랠리",       1.5),
            Map.entry("상승세",     1.3),
            Map.entry("호재",       1.5),
            Map.entry("호조",       1.2),
            Map.entry("긍정적",     0.8),
            Map.entry("강세",       1.2),
            Map.entry("반감기",     1.8),
            Map.entry("고래매수",   1.5),
            Map.entry("누적",       0.8),
            Map.entry("상장",       1.2),
            Map.entry("파트너십",   1.0),
            Map.entry("협력",       0.8),
            Map.entry("업무협약",   1.0),
            Map.entry("채택",       1.3),
            Map.entry("업그레이드", 1.0),
            Map.entry("메인넷",     1.0),
            Map.entry("개발완료",   1.0),
            Map.entry("확장성",     0.8),
            Map.entry("전략비축",   2.0),
            Map.entry("국가채택",   2.0),
            Map.entry("법정화폐",   1.8),
            Map.entry("성장",       0.8)
    );

    // ── 한국어 악재(Bearish) ──────────────────────────────────────────────────
    private static final Map<String, Double> KO_BEARISH = Map.ofEntries(
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
            Map.entry("규제강화",   1.8),
            Map.entry("금지",       2.0),
            Map.entry("거래금지",   2.0),
            Map.entry("불법",       1.8),
            Map.entry("제재",       1.5),
            Map.entry("단속",       1.5),
            Map.entry("과세강화",   1.5),
            Map.entry("규제압박",   1.8),
            Map.entry("해킹",       2.0),
            Map.entry("탈취",       1.8),
            Map.entry("도난",       1.8),
            Map.entry("취약점",     1.3),
            Map.entry("보안사고",   1.8),
            Map.entry("북한해커",   2.0),
            Map.entry("스캠",       2.0),
            Map.entry("사기",       1.8),
            Map.entry("러그풀",     2.0),
            Map.entry("폰지",       1.8),
            Map.entry("먹튀",       2.0),
            Map.entry("투자사기",   1.8),
            Map.entry("파산",       1.8),
            Map.entry("파산신청",   2.0),
            Map.entry("유동성위기", 1.8),
            Map.entry("인출중단",   1.8),
            Map.entry("출금정지",   1.8),
            Map.entry("상장폐지",   1.5),
            Map.entry("청산",       1.5),
            Map.entry("대규모청산", 1.8),
            Map.entry("매도세",     1.3),
            Map.entry("자금유출",   1.5),
            Map.entry("순유출",     1.5),
            Map.entry("이탈",       1.2),
            Map.entry("악재",       1.5),
            Map.entry("우려",       0.8),
            Map.entry("불확실성",   1.0),
            Map.entry("변동성확대", 1.0),
            Map.entry("부정적",     0.8),
            Map.entry("위험",       0.8),
            Map.entry("손실",       1.0),
            Map.entry("적자",       1.0)
    );

    // ── 영어 호재(Bullish) ────────────────────────────────────────────────────
    private static final Map<String, Double> EN_BULLISH = Map.ofEntries(
            Map.entry("rally",             1.5),
            Map.entry("surge",             1.8),
            Map.entry("all-time high",     2.0),
            Map.entry("all time high",     2.0),
            Map.entry("ath",               2.0),
            Map.entry("breakout",          1.5),
            Map.entry("rebound",           1.5),
            Map.entry("recovery",          1.3),
            Map.entry("bull run",          1.8),
            Map.entry("bullish",           1.5),
            Map.entry("moon",              1.5),
            Map.entry("pump",              1.5),
            Map.entry("spot etf",          2.0),
            Map.entry("etf approval",      2.0),
            Map.entry("etf inflow",        1.8),
            Map.entry("institutional",     1.5),
            Map.entry("blackrock",         1.5),
            Map.entry("fidelity",          1.5),
            Map.entry("inflow",            1.3),
            Map.entry("net inflow",        1.5),
            Map.entry("approved",          1.5),
            Map.entry("legalized",         1.8),
            Map.entry("pro-crypto",        1.8),
            Map.entry("adoption",          1.5),
            Map.entry("halving",           1.8),
            Map.entry("upgrade",           1.0),
            Map.entry("mainnet",           1.0),
            Map.entry("launch",            1.0),
            Map.entry("partnership",       1.0),
            Map.entry("listing",           1.2),
            Map.entry("strategic reserve", 2.0),
            Map.entry("legal tender",      1.8),
            Map.entry("accumulate",        1.3),
            Map.entry("oversold",          1.2),
            Map.entry("positive",          0.8),
            Map.entry("growth",            0.8),
            Map.entry("profit",            1.0),
            Map.entry("gain",              1.0),
            Map.entry("soar",              1.8),
            Map.entry("milestone",         1.0)
    );

    // ── 영어 악재(Bearish) ────────────────────────────────────────────────────
    private static final Map<String, Double> EN_BEARISH = Map.ofEntries(
            Map.entry("crash",             2.0),
            Map.entry("dump",              1.8),
            Map.entry("plunge",            1.8),
            Map.entry("tumble",            1.5),
            Map.entry("collapse",          1.8),
            Map.entry("correction",        1.0),
            Map.entry("bear market",       1.5),
            Map.entry("bearish",           1.5),
            Map.entry("crypto winter",     2.0),
            Map.entry("fud",               1.5),
            Map.entry("ban",               2.0),
            Map.entry("banned",            2.0),
            Map.entry("crackdown",         1.8),
            Map.entry("illegal",           1.8),
            Map.entry("sanction",          1.5),
            Map.entry("seized",            1.5),
            Map.entry("hack",              2.0),
            Map.entry("hacked",            2.0),
            Map.entry("exploit",           1.8),
            Map.entry("vulnerability",     1.3),
            Map.entry("stolen",            1.8),
            Map.entry("theft",             1.8),
            Map.entry("breach",            1.5),
            Map.entry("scam",              2.0),
            Map.entry("fraud",             1.8),
            Map.entry("rug pull",          2.0),
            Map.entry("ponzi",             1.8),
            Map.entry("bankrupt",          1.8),
            Map.entry("bankruptcy",        2.0),
            Map.entry("insolvent",         1.8),
            Map.entry("withdrawal halt",   1.8),
            Map.entry("delisted",          1.5),
            Map.entry("liquidation",       1.5),
            Map.entry("sell-off",          1.3),
            Map.entry("selloff",           1.3),
            Map.entry("outflow",           1.3),
            Map.entry("net outflow",       1.5),
            Map.entry("panic",             1.5),
            Map.entry("overbought",        1.2),
            Map.entry("loss",              1.0),
            Map.entry("lawsuit",           1.5),
            Map.entry("investigation",     1.2)
    );

    /**
     * BERT 결과(70%)와 키워드 점수(30%)를 혼합하여 반환.
     * 키워드 미매칭 시 BERT 결과 그대로 반환.
     */
    public SentimentAnalysisResult blendWithKeywords(SentimentAnalysisResult bertResult, String text) {
        if (text == null || text.isBlank()) {
            return bertResult;
        }

        KeywordScore ks = computeKeywordScore(text);
        if (ks.total() == 0) {
            log.debug("No crypto keywords matched — using BERT score as-is");
            return bertResult;
        }

        double keywordPositive = ks.bullish() / ks.total();
        double keywordNegative = ks.bearish() / ks.total();

        double blendedPositive = bertResult.getPositiveScore() * BERT_WEIGHT + keywordPositive * KEYWORD_WEIGHT;
        double blendedNegative = bertResult.getNegativeScore() * BERT_WEIGHT + keywordNegative * KEYWORD_WEIGHT;
        double blendedNeutral  = bertResult.getNeutralScore()  * BERT_WEIGHT;

        Sentiment sentiment = dominantSentiment(blendedPositive, blendedNegative, blendedNeutral);
        double topScore = pickTopScore(sentiment, blendedPositive, blendedNegative, blendedNeutral);

        log.info("Keyword blend — BERT: {}({}) → Blended: {}({}) | bullish={}{}, bearish={}{}",
                bertResult.getSentiment(), String.format("%.2f", bertResult.getScore()),
                sentiment, String.format("%.2f", topScore),
                String.format("%.2f", ks.bullish()), ks.bullishMatched(),
                String.format("%.2f", ks.bearish()), ks.bearishMatched());

        return SentimentAnalysisResult.builder()
                .sentiment(sentiment)
                .score(topScore)
                .positiveScore(blendedPositive)
                .neutralScore(blendedNeutral)
                .negativeScore(blendedNegative)
                .build();
    }

    /**
     * BERT 없이 키워드만으로 감성 분석 (폴백).
     * BERT 서버 장애 / 토큰 소진 시 호출된다.
     * 키워드 미매칭 시 NEUTRAL 반환.
     */
    public SentimentAnalysisResult analyzeKeywordsOnly(String text) {
        if (text == null || text.isBlank()) {
            return SentimentAnalysisResult.empty();
        }

        KeywordScore ks = computeKeywordScore(text);
        if (ks.total() == 0) {
            log.debug("Keyword-only fallback: no keywords matched, returning NEUTRAL");
            return SentimentAnalysisResult.empty();
        }

        double positiveScore = ks.bullish() / ks.total();
        double negativeScore = ks.bearish() / ks.total();
        double neutralScore  = 0.0;

        Sentiment sentiment = dominantSentiment(positiveScore, negativeScore, neutralScore);
        double topScore = pickTopScore(sentiment, positiveScore, negativeScore, neutralScore);

        log.info("Keyword-only fallback — {}: {} | bullish={}{}, bearish={}{}",
                sentiment, String.format("%.2f", topScore),
                String.format("%.2f", ks.bullish()), ks.bullishMatched(),
                String.format("%.2f", ks.bearish()), ks.bearishMatched());

        return SentimentAnalysisResult.builder()
                .sentiment(sentiment)
                .score(topScore)
                .positiveScore(positiveScore)
                .neutralScore(neutralScore)
                .negativeScore(negativeScore)
                .build();
    }

    private KeywordScore computeKeywordScore(String text) {
        double bullish = 0.0;
        double bearish = 0.0;
        List<String> bullishMatched = new ArrayList<>();
        List<String> bearishMatched = new ArrayList<>();

        // 한국어 — 원문 그대로 비교
        for (Map.Entry<String, Double> e : KO_BULLISH.entrySet()) {
            if (text.contains(e.getKey())) {
                bullish += e.getValue();
                bullishMatched.add(e.getKey());
            }
        }
        for (Map.Entry<String, Double> e : KO_BEARISH.entrySet()) {
            if (text.contains(e.getKey())) {
                bearish += e.getValue();
                bearishMatched.add(e.getKey());
            }
        }

        // 영어 — 대소문자 무시
        String lower = text.toLowerCase();
        for (Map.Entry<String, Double> e : EN_BULLISH.entrySet()) {
            if (lower.contains(e.getKey())) {
                bullish += e.getValue();
                bullishMatched.add(e.getKey());
            }
        }
        for (Map.Entry<String, Double> e : EN_BEARISH.entrySet()) {
            if (lower.contains(e.getKey())) {
                bearish += e.getValue();
                bearishMatched.add(e.getKey());
            }
        }

        return new KeywordScore(bullish, bearish, bullishMatched, bearishMatched);
    }

    private Sentiment dominantSentiment(double positive, double negative, double neutral) {
        if (positive >= negative && positive >= neutral) return Sentiment.POSITIVE;
        if (negative >= positive && negative >= neutral) return Sentiment.NEGATIVE;
        return Sentiment.NEUTRAL;
    }

    private double pickTopScore(Sentiment sentiment, double positive, double negative, double neutral) {
        return switch (sentiment) {
            case POSITIVE -> positive;
            case NEGATIVE -> negative;
            default       -> neutral;
        };
    }

    private record KeywordScore(
            double bullish,
            double bearish,
            List<String> bullishMatched,
            List<String> bearishMatched
    ) {
        double total() { return bullish + bearish; }
    }
}
