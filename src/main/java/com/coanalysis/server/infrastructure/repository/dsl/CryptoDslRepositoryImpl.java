package com.coanalysis.server.infrastructure.repository.dsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CryptoDslRepositoryImpl implements CryptoDslRepository {

    private final JPAQueryFactory queryFactory;


}
