package com.coanalysis.server.infrastructure.repository.dsl;

import com.coanalysis.server.crypto.application.domain.Crypto;
import com.coanalysis.server.news.adapter.in.dto.SearchNewsResponse;
import com.coanalysis.server.news.application.domain.QNews;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.expression.spel.ast.Projection;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NewsDslRepositoryImpl implements NewsDslRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<SearchNewsResponse> searchAllNews() {
        QNews news = new QNews("news");

        return queryFactory.select(Projections.constructor(SearchNewsResponse.class,
                        news.id
                ))
                .from(news)
                .fetch();
    }

}
