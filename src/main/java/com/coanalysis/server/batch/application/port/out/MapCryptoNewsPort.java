package com.coanalysis.server.batch.application.port.out;

import com.coanalysis.server.news.application.domain.News;
import java.util.Map;
import java.util.Set;

public interface MapCryptoNewsPort {
    void mapNewsToCoins(News news, Set<String> coinTickers);
    Set<String> getAllKnownTickers();
}
