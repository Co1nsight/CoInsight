package com.coanalysis.server.crypto.application.port.in;

import com.coanalysis.server.crypto.application.domain.Crypto;

import java.util.List;
import java.util.Optional;

public interface SearchCryptoUsecase {
    List<Crypto> searchByKeyword(String keyword);

    Optional<Crypto> findByTicker(String ticker);

    Optional<Crypto> findByTickerAndMarket(String ticker, String market);
}
