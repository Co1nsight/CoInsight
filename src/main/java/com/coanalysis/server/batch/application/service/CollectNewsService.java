package com.coanalysis.server.batch.application.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.coanalysis.server.batch.adapter.out.ArticleContentScraper;
import com.coanalysis.server.batch.adapter.out.CryptoCompareNewsClient;
import com.coanalysis.server.batch.adapter.out.TokenPostNewsClient;
import com.coanalysis.server.batch.application.domain.CollectedNews;
import com.coanalysis.server.batch.application.port.in.CollectNewsUseCase;
import com.coanalysis.server.batch.application.port.out.FindDuplicateNewsPort;
import com.coanalysis.server.batch.application.port.out.MapCryptoNewsPort;
import com.coanalysis.server.batch.application.port.out.SaveCollectedNewsPort;
import com.coanalysis.server.crypto.application.domain.Crypto;
import com.coanalysis.server.infrastructure.repository.NewsAnalysisRepository;
import com.coanalysis.server.infrastructure.exception.CustomException;
import com.coanalysis.server.infrastructure.exception.ErrorCode;
import com.coanalysis.server.news.adapter.out.GeminiClient;
import com.coanalysis.server.news.adapter.out.SentimentAnalyzerFactory;
import com.coanalysis.server.news.adapter.out.dto.GeminiSummaryResult;
import com.coanalysis.server.news.adapter.out.dto.SentimentAnalysisResult;
import com.coanalysis.server.news.application.domain.News;
import com.coanalysis.server.news.application.domain.NewsAnalysis;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CollectNewsService implements CollectNewsUseCase {

    private final ArticleContentScraper articleContentScraper;
    private final CryptoCompareNewsClient englishNewsClient;
    private final TokenPostNewsClient tokenPostNewsClient;
    private final FindDuplicateNewsPort findDuplicateNewsPort;
    private final SaveCollectedNewsPort saveCollectedNewsPort;
    private final MapCryptoNewsPort mapCryptoNewsPort;
    private final SentimentAnalyzerFactory sentimentAnalyzerFactory;
    private final GeminiClient geminiClient;
    private final NewsAnalysisRepository newsAnalysisRepository;
    private final EntityManager em;
    private final CryptoKeywordMatcher cryptoKeywordMatcher;

    @Override
    @Transactional
    public int collectAndProcessNews() {
        log.info("Starting news collection batch for EN and KO languages");

        // 1. 영문 뉴스 수집
        List<CollectedNews> englishNews = englishNewsClient.fetchLatestNews();
        log.info("Collected {} English news from CryptoCompare", englishNews.size());

        // 2. 한국어 뉴스 수집 (TokenPost)
        List<CollectedNews> tokenPostNews = tokenPostNewsClient.fetchLatestNews();
        log.info("Collected {} Korean news from TokenPost", tokenPostNews.size());

        // 3. 전체 뉴스 합치기
        List<CollectedNews> allNews = Stream.concat(
                englishNews.stream(), tokenPostNews.stream()
        ).collect(Collectors.toList());

        if (allNews.isEmpty()) {
            log.info("No news collected from any source");
            return 0;
        }

        // 4. 중복 링크 추출 및 확인
        Set<String> links = allNews.stream()
                .map(CollectedNews::originalLink)
                .collect(Collectors.toSet());

        Set<String> existingLinks = findDuplicateNewsPort.findExistingLinks(links);
        log.info("Found {} duplicate news to exclude", existingLinks.size());

        // 5. 중복되지 않은 뉴스 필터링
        List<CollectedNews> uniqueNews = allNews.stream()
                .filter(news -> !existingLinks.contains(news.originalLink()))
                .collect(Collectors.toList());

        if (uniqueNews.isEmpty()) {
            log.info("No new unique news to process");
            return 0;
        }

        // 6. RSS 기사 전문 스크래핑 (중복 필터링 후 새 기사만)
        uniqueNews = scrapeRssArticles(uniqueNews);

        // 7. 모든 코인 정보 조회 (ticker, 한글명, 영문명)
        List<Crypto> allCryptos = mapCryptoNewsPort.getAllCryptos();
        Map<String, String> keywordToTicker = buildKeywordToTickerMap(allCryptos);
        Set<String> knownTickers = allCryptos.stream()
                .map(Crypto::getTicker)
                .collect(Collectors.toSet());

        // 거래대금 상위 50개 티커 (Gemini 프롬프트용)
        List<String> topTickers = allCryptos.stream()
                .sorted((a, b) -> Double.compare(b.getTradingVolume(), a.getTradingVolume()))
                .limit(50)
                .map(Crypto::getTicker)
                .collect(Collectors.toList());

        // 8. 뉴스 저장
        List<News> savedNewsList = saveCollectedNewsPort.saveAll(uniqueNews);
        log.info("Saved {} new news articles", savedNewsList.size());
        em.flush();

        // 9. 감성 분석 및 코인 매핑 처리
        for (int i = 0; i < savedNewsList.size(); i++) {
            final News savedNews = savedNewsList.get(i);
            final CollectedNews collected = uniqueNews.get(i);

            try {
                processNewsItem(savedNews, collected, knownTickers, keywordToTicker, topTickers);
            } catch (CustomException e) {
                if (e.getErrorCode() == ErrorCode.HUGGINGFACE_QUOTA_EXCEEDED) {
                    log.warn("HuggingFace API 크레딧 소진. 남은 {} 건의 감성 분석을 중단합니다.",
                            savedNewsList.size() - i);
                    break;
                }
                log.warn("Failed to process news ID {}: {}", savedNews.getId(), e.getMessage());
            }
        }

        log.info("News collection batch completed. Processed {} news articles (EN: {}, KO: {})",
                savedNewsList.size(),
                savedNewsList.stream().filter(n -> n.getLanguage().name().equals("EN")).count(),
                savedNewsList.stream().filter(n -> n.getLanguage().name().equals("KO")).count());

        return savedNewsList.size();
    }

    private void processNewsItem(News savedNews, CollectedNews collected, Set<String> knownTickers,
                                  Map<String, String> keywordToTicker, List<String> topTickers) {
        try {
            // 8-1. 감성 분석 수행 - 전문(full content) 기준으로 분석
            String textToAnalyze = buildTextForAnalysis(savedNews.getTitle(), savedNews.getContent());
            SentimentAnalysisResult result = sentimentAnalyzerFactory.analyze(textToAnalyze, savedNews.getLanguage());

            // 8-2. NewsAnalysis 저장
            NewsAnalysis analysis = NewsAnalysis.builder()
                    .news(savedNews)
                    .sentimentLabel(result.getSentiment().name())
                    .sentimentScore(result.getScore())
                    .summary(generateSummary(result, savedNews.getLanguage()))
                    .build();

            newsAnalysisRepository.save(analysis);
            log.debug("News ID {} ({}) sentiment analysis completed: {}",
                    savedNews.getId(), savedNews.getLanguage(), result.getSentiment());

            // 8-3. Gemini 요약 생성 + 관련 코인 추출
            GeminiSummaryResult geminiResult = geminiClient.summarizeNews(
                    savedNews.getTitle(), savedNews.getContent(), topTickers);

            Set<String> matchedTickers;
            if (geminiResult != null && !geminiResult.summary().isBlank()) {
                savedNews.updateContent(geminiResult.summary());
                log.debug("News ID {} content replaced with Gemini summary", savedNews.getId());

                // Gemini가 추출한 코인 사용, 없으면 키워드 매처로 fallback
                if (!geminiResult.tickers().isEmpty()) {
                    matchedTickers = geminiResult.tickers();
                    log.debug("News ID {} coins from Gemini: {}", savedNews.getId(), matchedTickers);
                } else {
                    matchedTickers = extractMatchedTickers(savedNews, collected, knownTickers, keywordToTicker);
                    log.debug("News ID {} coins from keyword matcher (Gemini empty): {}", savedNews.getId(), matchedTickers);
                }
            } else {
                log.warn("News ID {} Gemini 요약 실패, 원문 유지 및 키워드 매처 사용", savedNews.getId());
                matchedTickers = extractMatchedTickers(savedNews, collected, knownTickers, keywordToTicker);
            }

            // 8-4. 코인 매핑 저장
            if (!matchedTickers.isEmpty()) {
                mapCryptoNewsPort.mapNewsToCoins(savedNews, matchedTickers);
                log.debug("News ID {} mapped to coins: {}", savedNews.getId(), matchedTickers);
            }

        } catch (CustomException e) {
            if (e.getErrorCode() == ErrorCode.HUGGINGFACE_QUOTA_EXCEEDED) {
                throw e;
            }
            log.warn("Failed to process news ID {}: {}", savedNews.getId(), e.getMessage());
        } catch (Exception e) {
            log.warn("Failed to process news ID {}: {}", savedNews.getId(), e.getMessage());
        }
    }

    /**
     * 코인 이름(한글명, 영문명, ticker)을 키워드로 매핑하는 Map을 생성합니다.
     * 키는 소문자로 변환되며, 값은 ticker입니다.
     */
    private Map<String, String> buildKeywordToTickerMap(List<Crypto> cryptos) {
        Map<String, String> map = new HashMap<>();
        for (Crypto crypto : cryptos) {
            String ticker = crypto.getTicker();

            // ticker 자체 (대소문자 무시)
            map.put(ticker.toLowerCase(), ticker);

            // 한글명
            if (crypto.getName() != null && !crypto.getName().isBlank()) {
                map.put(crypto.getName().toLowerCase(), ticker);
            }

            // 영문명
            if (crypto.getEnglishName() != null && !crypto.getEnglishName().isBlank()) {
                map.put(crypto.getEnglishName().toLowerCase(), ticker);
            }
        }
        return map;
    }

    /**
     * 뉴스 텍스트에서 코인 키워드를 찾아 매칭된 ticker를 반환합니다.
     * CryptoKeywordMatcher를 사용하여 다양한 패턴으로 매칭합니다.
     */
    private Set<String> extractMatchedTickers(News savedNews, CollectedNews collected,
                                               Set<String> knownTickers, Map<String, String> keywordToTicker) {
        return cryptoKeywordMatcher.extractTickers(
                savedNews.getTitle(),
                savedNews.getContent(),
                collected.categories(),
                knownTickers,
                keywordToTicker
        );
    }

    private List<CollectedNews> scrapeRssArticles(List<CollectedNews> newsList) {
        List<CollectedNews> result = new ArrayList<>(newsList.size());
        int scrapedCount = 0;

        for (CollectedNews news : newsList) {
            if (!articleContentScraper.isRssSource(news.source())) {
                result.add(news);
                continue;
            }

            String fullContent = articleContentScraper.scrapeContent(news.originalLink(), news.source());
            if (fullContent != null) {
                result.add(news.withBody(fullContent));
                scrapedCount++;
            } else {
                result.add(news);
            }
        }

        log.info("Scraped full content for {}/{} RSS articles", scrapedCount,
                newsList.stream().filter(n -> articleContentScraper.isRssSource(n.source())).count());
        return result;
    }

    private String buildTextForAnalysis(String title, String content) {
        if (content == null || content.isBlank()) {
            return title;
        }
        return title + ". " + content;
    }

    private String generateSummary(SentimentAnalysisResult result, com.coanalysis.server.news.application.enums.Language language) {
        if (language == com.coanalysis.server.news.application.enums.Language.KO) {
            return String.format("감성분석 결과: %s (신뢰도: %.1f%%) | 긍정: %.1f%%, 중립: %.1f%%, 부정: %.1f%%",
                    result.getSentiment().getKoreanLabel(),
                    result.getScore() * 100,
                    result.getPositiveScore() * 100,
                    result.getNeutralScore() * 100,
                    result.getNegativeScore() * 100);
        } else {
            return String.format("감성분석 결과: %s (신뢰도: %.1f%%) | 긍정: %.1f%%, 중립: %.1f%%, 부정: %.1f%%",
                    result.getSentiment().getLabel(),
                    result.getScore() * 100,
                    result.getPositiveScore() * 100,
                    result.getNeutralScore() * 100,
                    result.getNegativeScore() * 100);
        }
    }
}
