package com.coanalysis.server.prediction.adapter.out;

import com.coanalysis.server.market.adapter.out.BithumbClient;
import com.coanalysis.server.market.adapter.out.dto.BithumbTickerDto;
import com.coanalysis.server.prediction.application.port.out.FetchCryptoPricePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

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
}
