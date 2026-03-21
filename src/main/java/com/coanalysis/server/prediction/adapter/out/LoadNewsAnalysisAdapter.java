package com.coanalysis.server.prediction.adapter.out;

import com.coanalysis.server.crypto.application.domain.QCryptoNews;
import com.coanalysis.server.news.application.domain.QNews;
import com.coanalysis.server.news.application.domain.QNewsAnalysis;
import com.coanalysis.server.prediction.application.dto.NewsSignalItem;
import com.coanalysis.server.prediction.application.port.out.LoadNewsAnalysisPort;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoadNewsAnalysisAdapter implements LoadNewsAnalysisPort {

    private final JPAQueryFactory queryFactory;

    @Override
    public Map<String, Integer> countNewsBySentiment(String ticker, LocalDateTime from, LocalDateTime to) {
        QCryptoNews cryptoNews = QCryptoNews.cryptoNews;
        QNews news = QNews.news;
        QNewsAnalysis analysis = QNewsAnalysis.newsAnalysis;

        // 해당 코인의 뉴스와 감성분석 결과를 조인하여 감성 라벨별 개수 집계
        List<com.querydsl.core.Tuple> results = queryFactory
                .select(analysis.sentimentLabel, analysis.count())
                .from(cryptoNews)
                .join(news).on(cryptoNews.news.id.eq(news.id))
                .leftJoin(analysis).on(analysis.news.id.eq(news.id))
                .where(cryptoNews.crypto.ticker.eq(ticker.toUpperCase())
                        .and(news.publishedAt.between(from, to)))
                .groupBy(analysis.sentimentLabel)
                .fetch();

        Map<String, Integer> sentimentCounts = new HashMap<>();
        sentimentCounts.put("positive", 0);
        sentimentCounts.put("negative", 0);
        sentimentCounts.put("neutral", 0);

        for (com.querydsl.core.Tuple tuple : results) {
            String label = tuple.get(analysis.sentimentLabel);
            Long count = tuple.get(analysis.count());

            if (label != null && count != null) {
                String normalizedLabel = label.toLowerCase();
                if (normalizedLabel.contains("positive")) {
                    sentimentCounts.merge("positive", count.intValue(), Integer::sum);
                } else if (normalizedLabel.contains("negative")) {
                    sentimentCounts.merge("negative", count.intValue(), Integer::sum);
                } else {
                    sentimentCounts.merge("neutral", count.intValue(), Integer::sum);
                }
            } else {
                // 분석되지 않은 뉴스는 중립으로 처리
                if (count != null) {
                    sentimentCounts.merge("neutral", count.intValue(), Integer::sum);
                }
            }
        }

        log.debug("Sentiment counts for {} from {} to {}: positive={}, negative={}, neutral={}",
                ticker, from, to,
                sentimentCounts.get("positive"),
                sentimentCounts.get("negative"),
                sentimentCounts.get("neutral"));

        return sentimentCounts;
    }

    @Override
    public Map<String, Integer> countUnusedNewsBySentiment(String ticker, LocalDateTime lastPredictionTime, LocalDateTime to) {
        QCryptoNews cryptoNews = QCryptoNews.cryptoNews;
        QNews news = QNews.news;
        QNewsAnalysis analysis = QNewsAnalysis.newsAnalysis;

        // 마지막 예측 시간 이후에 발행된 뉴스만 조회 (이미 사용된 기사 제외)
        List<com.querydsl.core.Tuple> results = queryFactory
                .select(analysis.sentimentLabel, analysis.count())
                .from(cryptoNews)
                .join(news).on(cryptoNews.news.id.eq(news.id))
                .leftJoin(analysis).on(analysis.news.id.eq(news.id))
                .where(cryptoNews.crypto.ticker.eq(ticker.toUpperCase())
                        .and(news.publishedAt.gt(lastPredictionTime))
                        .and(news.publishedAt.loe(to)))
                .groupBy(analysis.sentimentLabel)
                .fetch();

        Map<String, Integer> sentimentCounts = new HashMap<>();
        sentimentCounts.put("positive", 0);
        sentimentCounts.put("negative", 0);
        sentimentCounts.put("neutral", 0);

        for (com.querydsl.core.Tuple tuple : results) {
            String label = tuple.get(analysis.sentimentLabel);
            Long count = tuple.get(analysis.count());

            if (label != null && count != null) {
                String normalizedLabel = label.toLowerCase();
                if (normalizedLabel.contains("positive")) {
                    sentimentCounts.merge("positive", count.intValue(), Integer::sum);
                } else if (normalizedLabel.contains("negative")) {
                    sentimentCounts.merge("negative", count.intValue(), Integer::sum);
                } else {
                    sentimentCounts.merge("neutral", count.intValue(), Integer::sum);
                }
            } else {
                if (count != null) {
                    sentimentCounts.merge("neutral", count.intValue(), Integer::sum);
                }
            }
        }

        log.debug("Unused sentiment counts for {} after {}: positive={}, negative={}, neutral={}",
                ticker, lastPredictionTime,
                sentimentCounts.get("positive"),
                sentimentCounts.get("negative"),
                sentimentCounts.get("neutral"));

        return sentimentCounts;
    }

    @Override
    public List<NewsSignalItem> loadNewsSignals(String ticker, LocalDateTime from, LocalDateTime to) {
        QCryptoNews cryptoNews = QCryptoNews.cryptoNews;
        QNews news = QNews.news;
        QNewsAnalysis analysis = QNewsAnalysis.newsAnalysis;

        return queryFactory
                .select(analysis.sentimentLabel, analysis.sentimentScore, news.publishedAt)
                .from(cryptoNews)
                .join(news).on(cryptoNews.news.id.eq(news.id))
                .join(analysis).on(analysis.news.id.eq(news.id))
                .where(cryptoNews.crypto.ticker.eq(ticker.toUpperCase())
                        .and(news.publishedAt.between(from, to))
                        .and(analysis.sentimentLabel.isNotNull())
                        .and(analysis.sentimentScore.isNotNull()))
                .fetch()
                .stream()
                .map(tuple -> NewsSignalItem.builder()
                        .sentimentLabel(tuple.get(analysis.sentimentLabel))
                        .sentimentScore(tuple.get(analysis.sentimentScore))
                        .publishedAt(tuple.get(news.publishedAt))
                        .build())
                .toList();
    }

    @Override
    public List<NewsSignalItem> loadUnusedNewsSignals(String ticker, LocalDateTime lastPredictionTime, LocalDateTime to) {
        QCryptoNews cryptoNews = QCryptoNews.cryptoNews;
        QNews news = QNews.news;
        QNewsAnalysis analysis = QNewsAnalysis.newsAnalysis;

        return queryFactory
                .select(analysis.sentimentLabel, analysis.sentimentScore, news.publishedAt)
                .from(cryptoNews)
                .join(news).on(cryptoNews.news.id.eq(news.id))
                .join(analysis).on(analysis.news.id.eq(news.id))
                .where(cryptoNews.crypto.ticker.eq(ticker.toUpperCase())
                        .and(news.publishedAt.gt(lastPredictionTime))
                        .and(news.publishedAt.loe(to))
                        .and(analysis.sentimentLabel.isNotNull())
                        .and(analysis.sentimentScore.isNotNull()))
                .fetch()
                .stream()
                .map(tuple -> NewsSignalItem.builder()
                        .sentimentLabel(tuple.get(analysis.sentimentLabel))
                        .sentimentScore(tuple.get(analysis.sentimentScore))
                        .publishedAt(tuple.get(news.publishedAt))
                        .build())
                .toList();
    }
}
