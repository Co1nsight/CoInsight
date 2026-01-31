package com.coanalysis.server.infrastructure.repository.dsl;

import com.coanalysis.server.news.adapter.in.dto.SearchNewsResponse;
import com.coanalysis.server.news.application.domain.QNews;
import com.coanalysis.server.news.application.domain.QNewsAnalysis;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
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
    public List<SearchNewsResponse> searchAllNews() {
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
                .limit(20)
                .fetch();
    }

    @Override
    public List<SearchNewsResponse> searchByKeyword(String keyword) {
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
                .limit(20)
                .fetch();
    }

}
