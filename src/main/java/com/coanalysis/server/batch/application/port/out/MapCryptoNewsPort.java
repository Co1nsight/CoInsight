package com.coanalysis.server.batch.application.port.out;

import com.coanalysis.server.crypto.application.domain.Crypto;
import com.coanalysis.server.news.application.domain.News;

import java.util.List;
import java.util.Set;

public interface MapCryptoNewsPort {
    void mapNewsToCoins(News news, Set<String> coinTickers);
    Set<String> getAllKnownTickers();

    /**
     * 모든 코인 목록을 조회합니다.
     * ticker, name(한글명), englishName(영문명)을 포함합니다.
     */
    List<Crypto> getAllCryptos();
}
