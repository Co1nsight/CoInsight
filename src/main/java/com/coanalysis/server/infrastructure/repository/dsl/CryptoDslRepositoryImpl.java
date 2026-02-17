package com.coanalysis.server.infrastructure.repository.dsl;

import static com.coanalysis.server.crypto.application.domain.QCrypto.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.coanalysis.server.crypto.application.domain.Crypto;
import com.coanalysis.server.main.adapter.in.dto.UnifiedSearchResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CryptoDslRepositoryImpl implements CryptoDslRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<Crypto> findAllByTickers(Set<String> coinTickers) {
		return queryFactory.selectFrom(crypto)
			.where(crypto.ticker.in(coinTickers))
			.fetch();
	}

	@Override
	public Set<String> findAllTickers() {
		return new HashSet<>(
			queryFactory.selectDistinct(crypto.ticker)
				.from(crypto)
				.fetch()
		);
	}

	@Override
	public List<Crypto> findAllCryptos() {
		return queryFactory.selectFrom(crypto)
			.fetch();
	}

	@Override
	public List<UnifiedSearchResponse.CryptoResult> searchCryptosForUnified(String keyword, int limit) {
		BooleanBuilder where = new BooleanBuilder();
		if (StringUtils.hasText(keyword)) {
			where.or(crypto.ticker.containsIgnoreCase(keyword));
			where.or(crypto.name.containsIgnoreCase(keyword));
		}

		return queryFactory.select(Projections.constructor(UnifiedSearchResponse.CryptoResult.class,
				crypto.ticker,
				crypto.name,
				crypto.logoUrl
			))
			.from(crypto)
			.where(where)
			.limit(limit)
			.fetch();
	}
}
