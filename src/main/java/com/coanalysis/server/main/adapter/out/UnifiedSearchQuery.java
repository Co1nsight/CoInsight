package com.coanalysis.server.main.adapter.out;

import com.coanalysis.server.infrastructure.repository.CryptoRepository;
import com.coanalysis.server.infrastructure.repository.NewsRepository;
import com.coanalysis.server.main.adapter.in.dto.UnifiedSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UnifiedSearchQuery {

    private final CryptoRepository cryptoRepository;
    private final NewsRepository newsRepository;

    public UnifiedSearchResponse search(String keyword, int cryptoLimit, int newsLimit) {
        // 코인 검색
        List<UnifiedSearchResponse.CryptoResult> cryptos =
                cryptoRepository.searchCryptosForUnified(keyword, cryptoLimit);

        // 뉴스 검색
        List<UnifiedSearchResponse.NewsResult> news =
                newsRepository.searchNewsForUnified(keyword, newsLimit);

        return UnifiedSearchResponse.builder()
                .cryptos(cryptos)
                .news(news)
                .build();
    }
}
