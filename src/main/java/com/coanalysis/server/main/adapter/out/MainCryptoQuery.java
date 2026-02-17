package com.coanalysis.server.main.adapter.out;

import com.coanalysis.server.crypto.application.domain.Crypto;
import com.coanalysis.server.infrastructure.repository.CryptoRepository;
import com.coanalysis.server.infrastructure.response.PageResponse;
import com.coanalysis.server.main.adapter.in.dto.MainCryptoResponse;
import com.coanalysis.server.market.adapter.out.BithumbClient;
import com.coanalysis.server.market.adapter.out.dto.BithumbTickerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MainCryptoQuery {

    private final CryptoRepository cryptoRepository;
    private final BithumbClient bithumbClient;

    public PageResponse<MainCryptoResponse> findCryptosByTradingVolume(int page, int size) {
        // 1. DB에서 모든 코인 정보 조회
        List<Crypto> allCryptos = cryptoRepository.findAllCryptos();

        if (allCryptos.isEmpty()) {
            return PageResponse.of(List.of(), page, size, 0);
        }

        // 2. Bithumb API에서 실시간 시세 조회 (KRW 마켓만)
        List<String> marketCodes = allCryptos.stream()
                .map(c -> "KRW-" + c.getTicker())
                .collect(Collectors.toList());

        List<BithumbTickerDto> tickers;
        try {
            tickers = bithumbClient.getTickers(marketCodes);
        } catch (Exception e) {
            log.warn("Failed to fetch tickers from Bithumb API: {}", e.getMessage());
            tickers = List.of();
        }

        // 3. 티커 정보를 Map으로 변환
        Map<String, BithumbTickerDto> tickerMap = tickers.stream()
                .collect(Collectors.toMap(BithumbTickerDto::getSymbol, t -> t, (a, b) -> a));

        // 4. 코인 정보와 시세 정보 합치기
        List<MainCryptoResponse> allResponses = allCryptos.stream()
                .map(crypto -> {
                    BithumbTickerDto ticker = tickerMap.get(crypto.getTicker());
                    return MainCryptoResponse.builder()
                            .ticker(crypto.getTicker())
                            .name(crypto.getName())
                            .logoUrl(crypto.getLogoUrl())
                            .currentPrice(ticker != null ? ticker.getTradePrice() : null)
                            .tradingVolume24h(ticker != null ? ticker.getAccTradePrice24h() : null)
                            .changeRate(ticker != null ? ticker.getSignedChangeRate() : null)
                            .changePrice(ticker != null ? ticker.getSignedChangePrice() : null)
                            .highPrice(ticker != null ? ticker.getHighPrice() : null)
                            .lowPrice(ticker != null ? ticker.getLowPrice() : null)
                            .build();
                })
                .sorted(Comparator.comparing(
                        MainCryptoResponse::getTradingVolume24h,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .collect(Collectors.toList());

        // 5. 페이징 처리
        int totalElements = allResponses.size();
        int fromIndex = Math.min(page * size, totalElements);
        int toIndex = Math.min(fromIndex + size, totalElements);

        List<MainCryptoResponse> pagedContent = allResponses.subList(fromIndex, toIndex);

        return PageResponse.of(pagedContent, page, size, totalElements);
    }
}
