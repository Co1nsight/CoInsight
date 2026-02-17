package com.coanalysis.server.crypto.application.port.out;

import com.coanalysis.server.crypto.application.domain.Crypto;

import java.util.List;
import java.util.Set;

public interface SaveCryptoPort {

    /**
     * 여러 Crypto를 저장합니다.
     */
    List<Crypto> saveAll(List<Crypto> cryptos);

    /**
     * DB에 존재하는 모든 ticker를 조회합니다.
     */
    Set<String> findAllTickers();
}
