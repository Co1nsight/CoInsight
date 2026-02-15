package com.coanalysis.server.infrastructure.repository.dsl;

import static com.coanalysis.server.crypto.application.domain.QCrypto.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.coanalysis.server.crypto.application.domain.Crypto;
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
}
