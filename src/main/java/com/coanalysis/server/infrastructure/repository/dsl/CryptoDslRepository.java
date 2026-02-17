package com.coanalysis.server.infrastructure.repository.dsl;

import com.coanalysis.server.crypto.application.domain.Crypto;
import com.coanalysis.server.main.adapter.in.dto.UnifiedSearchResponse;

import java.util.List;
import java.util.Set;

public interface CryptoDslRepository {

	List<Crypto> findAllByTickers(Set<String> coinTickers);

	Set<String> findAllTickers();

	List<Crypto> findAllCryptos();

	List<UnifiedSearchResponse.CryptoResult> searchCryptosForUnified(String keyword, int limit);
}
