package com.coanalysis.server.prediction.adapter.out;

import com.coanalysis.server.market.adapter.out.BithumbClient;
import com.coanalysis.server.market.adapter.out.dto.BithumbTickerDto;
import com.coanalysis.server.prediction.application.port.out.FetchCryptoPricePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class FetchCryptoPriceAdapter implements FetchCryptoPricePort {

    private final BithumbClient bithumbClient;

    @Override
    public Double fetchCurrentPrice(String ticker) {
        String market = "KRW-" + ticker.toUpperCase();
        log.debug("Fetching current price for {}", market);

        try {
            List<BithumbTickerDto> tickers = bithumbClient.getTickers(List.of(market));
            if (tickers != null && !tickers.isEmpty()) {
                BithumbTickerDto tickerDto = tickers.get(0);
                Double price = tickerDto.getTradePrice() != null ? tickerDto.getTradePrice().doubleValue() : null;
                log.debug("Current price for {}: {}", market, price);
                return price;
            }
        } catch (Exception e) {
            log.error("Failed to fetch price for {}: {}", market, e.getMessage());
        }

        return null;
    }

    @Override
    public Map<String, Double> fetchAllPrices(List<String> tickers) {
        Map<String, Double> priceMap = new HashMap<>();

        if (tickers == null || tickers.isEmpty()) {
            return priceMap;
        }

        // 마켓 코드로 변환 (빗썸 API는 쉼표로 구분된 여러 마켓을 한 번에 조회 가능)
        List<String> markets = tickers.stream()
                .map(ticker -> "KRW-" + ticker.toUpperCase())
                .toList();

        log.info("Fetching prices for {} tickers in a single request", tickers.size());

        try {
            List<BithumbTickerDto> tickerDtos = bithumbClient.getTickers(markets);

            if (tickerDtos != null) {
                for (BithumbTickerDto dto : tickerDtos) {
                    // "KRW-BTC" -> "BTC"
                    String ticker = dto.getMarket().replace("KRW-", "");
                    Double price = dto.getTradePrice() != null ? dto.getTradePrice().doubleValue() : null;
                    if (price != null) {
                        priceMap.put(ticker, price);
                    }
                }
            }

            log.info("Successfully fetched {} prices out of {} tickers", priceMap.size(), tickers.size());
        } catch (Exception e) {
            log.error("Failed to fetch prices: {}", e.getMessage());
        }

        return priceMap;
    }
}
