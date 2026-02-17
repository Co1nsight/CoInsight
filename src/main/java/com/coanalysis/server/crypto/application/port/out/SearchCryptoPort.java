package com.coanalysis.server.crypto.application.port.out;

import com.coanalysis.server.crypto.application.domain.Crypto;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SearchCryptoPort {
    List<Crypto> searchByKeyword(String keyword);
    Optional<Crypto> findByTicker(String ticker);
	Set<String> findAllTickers();
	List<Crypto> findAllByTickers(Set<String> coinTickers);

}
