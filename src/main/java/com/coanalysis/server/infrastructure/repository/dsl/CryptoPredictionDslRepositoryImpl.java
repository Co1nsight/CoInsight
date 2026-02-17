package com.coanalysis.server.infrastructure.repository.dsl;

import com.coanalysis.server.crypto.application.domain.QCrypto;
import com.coanalysis.server.crypto.application.domain.QCryptoNews;
import com.coanalysis.server.news.application.domain.QNews;
import com.coanalysis.server.news.application.domain.QNewsAnalysis;
import com.coanalysis.server.prediction.adapter.in.dto.PredictionHistoryResponse;
import com.coanalysis.server.prediction.adapter.in.dto.PredictionNewsResponse;
import com.coanalysis.server.prediction.adapter.in.dto.RecentNewsResponse;
import com.coanalysis.server.prediction.application.domain.QCryptoPrediction;
import com.coanalysis.server.prediction.application.domain.QPredictionVerification;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CryptoPredictionDslRepositoryImpl implements CryptoPredictionDslRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PredictionHistoryResponse> findPredictionHistory(String ticker, int page, int size) {
        QCryptoPrediction prediction = QCryptoPrediction.cryptoPrediction;
        QCrypto crypto = QCrypto.crypto;

        return queryFactory.select(Projections.constructor(PredictionHistoryResponse.class,
                        prediction.id,
                        prediction.predictionDate,
                        prediction.predictionTime,
                        prediction.predictionLabel.stringValue(),
                        prediction.positiveRatio,
                        prediction.positiveCount,
                        prediction.negativeCount,
                        prediction.neutralCount,
                        prediction.priceAtPrediction
                ))
                .from(prediction)
                .join(crypto).on(prediction.crypto.ticker.eq(crypto.ticker))
                .where(crypto.ticker.eq(ticker.toUpperCase()))
                .orderBy(prediction.predictionTime.desc())
                .offset((long) page * size)
                .limit(size)
                .fetch();
    }

    @Override
    public long countPredictionHistory(String ticker) {
        QCryptoPrediction prediction = QCryptoPrediction.cryptoPrediction;

        Long count = queryFactory.select(prediction.count())
                .from(prediction)
                .where(prediction.crypto.ticker.eq(ticker.toUpperCase()))
                .fetchOne();

        return count != null ? count : 0;
    }

    @Override
    public List<PredictionNewsResponse> findPredictionNews(Long predictionId) {
        QCryptoPrediction prediction = QCryptoPrediction.cryptoPrediction;
        QCryptoNews cryptoNews = QCryptoNews.cryptoNews;
        QNews news = QNews.news;
        QNewsAnalysis analysis = QNewsAnalysis.newsAnalysis;

        // 예측에 사용된 뉴스 조회 (예측 시점 기준 24시간 전 ~ 예측 시점)
        var predictionInfo = queryFactory.select(prediction.crypto.ticker, prediction.predictionTime)
                .from(prediction)
                .where(prediction.id.eq(predictionId))
                .fetchOne();

        if (predictionInfo == null) {
            return List.of();
        }

        String ticker = predictionInfo.get(prediction.crypto.ticker);
        LocalDateTime predictionTime = predictionInfo.get(prediction.predictionTime);
        LocalDateTime fromTime = predictionTime.minusHours(24);

        return queryFactory.select(Projections.constructor(PredictionNewsResponse.class,
                        news.id,
                        news.title,
                        news.publisher,
                        news.publishedAt,
                        analysis.sentimentLabel,
                        analysis.sentimentScore
                ))
                .from(cryptoNews)
                .join(news).on(cryptoNews.news.id.eq(news.id))
                .leftJoin(analysis).on(analysis.news.id.eq(news.id))
                .where(cryptoNews.crypto.ticker.eq(ticker)
                        .and(news.publishedAt.between(fromTime, predictionTime)))
                .orderBy(news.publishedAt.desc())
                .fetch();
    }

    @Override
    public List<RecentNewsResponse> findRecentNews(String ticker, LocalDateTime from, LocalDateTime to, int page, int size) {
        QCryptoNews cryptoNews = QCryptoNews.cryptoNews;
        QNews news = QNews.news;
        QNewsAnalysis analysis = QNewsAnalysis.newsAnalysis;

        return queryFactory.select(Projections.constructor(RecentNewsResponse.class,
                        news.id,
                        news.title,
                        news.publisher,
                        news.publishedAt,
                        analysis.sentimentLabel,
                        analysis.sentimentScore
                ))
                .from(cryptoNews)
                .join(news).on(cryptoNews.news.id.eq(news.id))
                .leftJoin(analysis).on(analysis.news.id.eq(news.id))
                .where(cryptoNews.crypto.ticker.eq(ticker.toUpperCase())
                        .and(news.publishedAt.between(from, to)))
                .orderBy(news.publishedAt.desc())
                .offset((long) page * size)
                .limit(size)
                .fetch();
    }

    @Override
    public long countRecentNews(String ticker, LocalDateTime from, LocalDateTime to) {
        QCryptoNews cryptoNews = QCryptoNews.cryptoNews;
        QNews news = QNews.news;

        Long count = queryFactory.select(news.count())
                .from(cryptoNews)
                .join(news).on(cryptoNews.news.id.eq(news.id))
                .where(cryptoNews.crypto.ticker.eq(ticker.toUpperCase())
                        .and(news.publishedAt.between(from, to)))
                .fetchOne();

        return count != null ? count : 0;
    }

    @Override
    public List<PredictionHistoryResponse.VerificationResult> findVerificationResults(Long predictionId) {
        QPredictionVerification verification = QPredictionVerification.predictionVerification;

        return queryFactory.select(Projections.constructor(PredictionHistoryResponse.VerificationResult.class,
                        verification.intervalType.stringValue(),
                        verification.priceAtVerification,
                        verification.priceChangePercent,
                        verification.isSuccess,
                        verification.verifiedAt
                ))
                .from(verification)
                .where(verification.prediction.id.eq(predictionId))
                .orderBy(verification.intervalType.asc())
                .fetch();
    }
}
