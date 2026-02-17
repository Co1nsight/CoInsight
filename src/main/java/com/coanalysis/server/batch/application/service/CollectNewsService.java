package com.coanalysis.server.batch.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import com.coanalysis.server.batch.adapter.out.CryptoCompareNewsClient;
import com.coanalysis.server.batch.adapter.out.TokenPostNewsClient;
import com.coanalysis.server.batch.application.domain.CollectedNews;
import com.coanalysis.server.batch.application.port.in.CollectNewsUseCase;
import com.coanalysis.server.batch.application.port.out.FindDuplicateNewsPort;
import com.coanalysis.server.batch.application.port.out.MapCryptoNewsPort;
import com.coanalysis.server.batch.application.port.out.SaveCollectedNewsPort;
import com.coanalysis.server.infrastructure.repository.NewsAnalysisRepository;
import com.coanalysis.server.news.adapter.out.SentimentAnalyzerFactory;
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

    private final CryptoCompareNewsClient englishNewsClient;
    private final TokenPostNewsClient koreanNewsClient;
    private final FindDuplicateNewsPort findDuplicateNewsPort;
    private final SaveCollectedNewsPort saveCollectedNewsPort;
    private final MapCryptoNewsPort mapCryptoNewsPort;
    private final SentimentAnalyzerFactory sentimentAnalyzerFactory;
    private final NewsAnalysisRepository newsAnalysisRepository;
    private final EntityManager em;

    @Override
    @Transactional
    public int collectAndProcessNews() {
        log.info("Starting news collection batch for EN and KO languages");

        // 1. 영문 뉴스 수집
        List<CollectedNews> englishNews = englishNewsClient.fetchLatestNews();
        log.info("Collected {} English news from CryptoCompare", englishNews.size());

        // 2. 한국어 뉴스 수집
        List<CollectedNews> koreanNews = koreanNewsClient.fetchLatestNews();
        log.info("Collected {} Korean news from TokenPost", koreanNews.size());

        // 3. 전체 뉴스 합치기
        List<CollectedNews> allNews = Stream.concat(englishNews.stream(), koreanNews.stream())
                .collect(Collectors.toList());

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

        // 6. 알려진 코인 티커 목록 조회
        Set<String> knownTickers = mapCryptoNewsPort.getAllKnownTickers();

        // 7. 뉴스 저장
        List<News> savedNewsList = saveCollectedNewsPort.saveAll(uniqueNews);
        log.info("Saved {} new news articles", savedNewsList.size());
        em.flush();

        // 8. Virtual Thread로 감성 분석 및 코인 매핑 처리;
        for (int i = 0; i < savedNewsList.size(); i++) {
            final News savedNews = savedNewsList.get(i);
            final CollectedNews collected = uniqueNews.get(i);

            processNewsItem(savedNews, collected, knownTickers);
        }

        log.info("News collection batch completed. Processed {} news articles (EN: {}, KO: {})",
                savedNewsList.size(),
                savedNewsList.stream().filter(n -> n.getLanguage().name().equals("EN")).count(),
                savedNewsList.stream().filter(n -> n.getLanguage().name().equals("KO")).count());

        return savedNewsList.size();
    }

    private void processNewsItem(News savedNews, CollectedNews collected, Set<String> knownTickers) {
        try {
            // 8-1. 감성 분석 수행 (언어에 맞는 모델 사용)
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

            // 8-3. 코인 매핑 추출 및 저장
            Set<String> matchedTickers = collected.extractCoinTickers(knownTickers);
            if (!matchedTickers.isEmpty()) {
                mapCryptoNewsPort.mapNewsToCoins(savedNews, matchedTickers);
                log.debug("News ID {} mapped to coins: {}", savedNews.getId(), matchedTickers);
            }

        } catch (Exception e) {
            log.warn("Failed to process news ID {}: {}", savedNews.getId(), e.getMessage());
        }
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
