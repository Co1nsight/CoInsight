package com.coanalysis.server.infrastructure.repository.dsl;

import com.coanalysis.server.crypto.application.domain.Crypto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CryptoDslRepositoryImpl implements CryptoDslRepository {

    private final JPAQueryFactory queryFactory;


}
