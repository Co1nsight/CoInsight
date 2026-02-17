package com.coanalysis.server.infrastructure.repository.dsl;

import com.coanalysis.server.crypto.application.domain.QCrypto;
import com.coanalysis.server.crypto.application.domain.QCryptoNews;
import com.coanalysis.server.main.adapter.in.dto.MainNewsResponse;
import com.coanalysis.server.main.adapter.in.dto.UnifiedSearchResponse;
import com.coanalysis.server.news.adapter.in.dto.NewsDetailResponse;
import com.coanalysis.server.news.adapter.in.dto.NewsWithAnalysisResponse;
import com.coanalysis.server.news.adapter.in.dto.SearchNewsResponse;
import com.coanalysis.server.news.application.domain.QNews;
import com.coanalysis.server.news.application.domain.QNewsAnalysis;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NewsDslRepositoryImpl implements NewsDslRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<SearchNewsResponse> searchAllNews(int page, int size) {
        QNews news = new QNews("news");
        QNewsAnalysis analysis = new QNewsAnalysis("analysis");

        return queryFactory.select(Projections.constructor(SearchNewsResponse.class,
                        news.id,
                        news.title,
                        news.publisher,
                        news.publishedAt,
                        analysis.sentimentLabel,
                        analysis.sentimentScore
                ))
                .from(news)
                .join(analysis).on(analysis.news.id.eq(news.id))
                .orderBy(news.publishedAt.desc())
                .offset((long) page * size)
                .limit(size)
                .fetch();
    }

    @Override
    public long countAllNews() {
        QNews news = new QNews("news");
        QNewsAnalysis analysis = new QNewsAnalysis("analysis");

        Long count = queryFactory.select(news.count())
                .from(news)
                .join(analysis).on(analysis.news.id.eq(news.id))
                .fetchOne();

        return count != null ? count : 0;
    }

    @Override
    public List<SearchNewsResponse> searchByKeyword(String keyword, int page, int size) {
        QNews news = new QNews("news");
        QNewsAnalysis analysis = new QNewsAnalysis("analysis");

        BooleanBuilder where = new BooleanBuilder();
        if (StringUtils.hasText(keyword)) {
            where.or(news.title.containsIgnoreCase(keyword));
            where.or(news.content.containsIgnoreCase(keyword));
        }

        return queryFactory.select(Projections.constructor(SearchNewsResponse.class,
                        news.id,
                        news.title,
                        news.publisher,
                        news.publishedAt,
                        analysis.sentimentLabel,
                        analysis.sentimentScore
                ))
                .from(news)
                .join(analysis).on(analysis.news.id.eq(news.id))
                .where(where)
                .orderBy(news.publishedAt.desc())
                .offset((long) page * size)
                .limit(size)
                .fetch();
    }

    @Override
    public long countByKeyword(String keyword) {
        QNews news = new QNews("news");
        QNewsAnalysis analysis = new QNewsAnalysis("analysis");

        BooleanBuilder where = new BooleanBuilder();
        if (StringUtils.hasText(keyword)) {
            where.or(news.title.containsIgnoreCase(keyword));
            where.or(news.content.containsIgnoreCase(keyword));
        }

        Long count = queryFactory.select(news.count())
                .from(news)
                .join(analysis).on(analysis.news.id.eq(news.id))
                .where(where)
                .fetchOne();

        return count != null ? count : 0;
    }

    @Override
    public List<NewsWithAnalysisResponse> findNewsWithAnalysis(int page, int size) {
        QNews news = new QNews("news");
        QNewsAnalysis analysis = new QNewsAnalysis("analysis");

        return queryFactory.select(Projections.fields(NewsWithAnalysisResponse.class,
                        news.id,
                        news.title,
                        news.publisher,
                        news.publishedAt,
                        news.originalLink,
                        analysis.sentimentLabel,
                        analysis.sentimentScore
                ))
                .from(news)
                .leftJoin(analysis).on(analysis.news.id.eq(news.id))
                .orderBy(news.publishedAt.desc())
                .offset((long) page * size)
                .limit(size)
                .fetch();
    }

    @Override
    public long countNewsWithAnalysis() {
        QNews news = new QNews("news");

        Long count = queryFactory.select(news.count())
                .from(news)
                .fetchOne();

        return count != null ? count : 0;
    }

    @Override
    public List<NewsWithAnalysisResponse.RelatedCrypto> findRelatedCryptosByNewsId(Long newsId) {
        QCryptoNews cryptoNews = new QCryptoNews("cryptoNews");
        QCrypto crypto = new QCrypto("crypto");

        return queryFactory.select(Projections.constructor(NewsWithAnalysisResponse.RelatedCrypto.class,
                        crypto.ticker,
                        crypto.name,
                        crypto.logoUrl
                ))
                .from(cryptoNews)
                .join(crypto).on(cryptoNews.crypto.ticker.eq(crypto.ticker))
                .where(cryptoNews.news.id.eq(newsId))
                .fetch();
    }

    @Override
    public NewsDetailResponse findNewsDetailById(Long newsId) {
        QNews news = new QNews("news");

        return queryFactory.select(Projections.fields(NewsDetailResponse.class,
                        news.id,
                        news.title,
                        news.content,
                        news.publisher,
                        news.publishedAt,
                        news.originalLink,
                        news.language.stringValue().as("language")
                ))
                .from(news)
                .where(news.id.eq(newsId))
                .fetchOne();
    }

    @Override
    public NewsDetailResponse.AnalysisResult findAnalysisByNewsId(Long newsId) {
        QNewsAnalysis analysis = new QNewsAnalysis("analysis");

        return queryFactory.select(Projections.constructor(NewsDetailResponse.AnalysisResult.class,
                        analysis.sentimentLabel,
                        analysis.sentimentScore,
                        analysis.summary
                ))
                .from(analysis)
                .where(analysis.news.id.eq(newsId))
                .fetchOne();
    }

    @Override
    public List<NewsDetailResponse.RelatedCrypto> findDetailRelatedCryptosByNewsId(Long newsId) {
        QCryptoNews cryptoNews = new QCryptoNews("cryptoNews");
        QCrypto crypto = new QCrypto("crypto");

        return queryFactory.select(Projections.constructor(NewsDetailResponse.RelatedCrypto.class,
                        crypto.ticker,
                        crypto.name,
                        crypto.logoUrl
                ))
                .from(cryptoNews)
                .join(crypto).on(cryptoNews.crypto.ticker.eq(crypto.ticker))
                .where(cryptoNews.news.id.eq(newsId))
                .fetch();
    }

    @Override
    public List<SearchNewsResponse> findNewsByTicker(String ticker, int page, int size) {
        QNews news = new QNews("news");
        QNewsAnalysis analysis = new QNewsAnalysis("analysis");
        QCryptoNews cryptoNews = new QCryptoNews("cryptoNews");

        return queryFactory.select(Projections.constructor(SearchNewsResponse.class,
                        news.id,
                        news.title,
                        news.publisher,
                        news.publishedAt,
                        analysis.sentimentLabel,
                        analysis.sentimentScore
                ))
                .from(news)
                .join(cryptoNews).on(cryptoNews.news.id.eq(news.id))
                .leftJoin(analysis).on(analysis.news.id.eq(news.id))
                .where(cryptoNews.crypto.ticker.eq(ticker.toUpperCase()))
                .orderBy(news.publishedAt.desc())
                .offset((long) page * size)
                .limit(size)
                .fetch();
    }

    @Override
    public long countNewsByTicker(String ticker) {
        QNews news = new QNews("news");
        QCryptoNews cryptoNews = new QCryptoNews("cryptoNews");

        Long count = queryFactory.select(news.count())
                .from(news)
                .join(cryptoNews).on(cryptoNews.news.id.eq(news.id))
                .where(cryptoNews.crypto.ticker.eq(ticker.toUpperCase()))
                .fetchOne();

        return count != null ? count : 0;
    }

    @Override
    public List<MainNewsResponse> findMainNews(int page, int size) {
        QNews news = new QNews("news");
        QNewsAnalysis analysis = new QNewsAnalysis("analysis");

        return queryFactory.select(Projections.fields(MainNewsResponse.class,
                        news.id,
                        news.title,
                        news.publisher,
                        Expressions.stringTemplate(
                                "CASE WHEN LENGTH({0}) > 100 THEN CONCAT(SUBSTRING({0}, 1, 100), '...') ELSE {0} END",
                                news.content
                        ).as("contentSnippet"),
                        news.publishedAt,
                        news.language.stringValue().as("language"),
                        analysis.sentimentLabel,
                        analysis.sentimentScore
                ))
                .from(news)
                .leftJoin(analysis).on(analysis.news.id.eq(news.id))
                .orderBy(news.publishedAt.desc())
                .offset((long) page * size)
                .limit(size)
                .fetch();
    }

    @Override
    public long countMainNews() {
        QNews news = new QNews("news");

        Long count = queryFactory.select(news.count())
                .from(news)
                .fetchOne();

        return count != null ? count : 0;
    }

    @Override
    public List<MainNewsResponse.RelatedCrypto> findRelatedCryptosForMainNews(Long newsId) {
        QCryptoNews cryptoNews = new QCryptoNews("cryptoNews");
        QCrypto crypto = new QCrypto("crypto");

        return queryFactory.select(Projections.constructor(MainNewsResponse.RelatedCrypto.class,
                        crypto.ticker,
                        crypto.name,
                        crypto.logoUrl
                ))
                .from(cryptoNews)
                .join(crypto).on(cryptoNews.crypto.ticker.eq(crypto.ticker))
                .where(cryptoNews.news.id.eq(newsId))
                .fetch();
    }

    @Override
    public List<UnifiedSearchResponse.NewsResult> searchNewsForUnified(String keyword, int limit) {
        QNews news = new QNews("news");
        QNewsAnalysis analysis = new QNewsAnalysis("analysis");

        BooleanBuilder where = new BooleanBuilder();
        if (StringUtils.hasText(keyword)) {
            where.or(news.title.containsIgnoreCase(keyword));
            where.or(news.content.containsIgnoreCase(keyword));
        }

        return queryFactory.select(Projections.constructor(UnifiedSearchResponse.NewsResult.class,
                        news.id,
                        news.title,
                        news.publisher,
                        news.publishedAt,
                        analysis.sentimentLabel
                ))
                .from(news)
                .leftJoin(analysis).on(analysis.news.id.eq(news.id))
                .where(where)
                .orderBy(news.publishedAt.desc())
                .limit(limit)
                .fetch();
    }

}
