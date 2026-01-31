package com.coanalysis.server.market.application.service;

import com.coanalysis.server.infrastructure.config.CacheConfig;
import com.coanalysis.server.market.adapter.in.dto.CandleResponse;
import com.coanalysis.server.market.adapter.in.dto.TickerResponse;
import com.coanalysis.server.market.adapter.out.BithumbClient;
import com.coanalysis.server.market.adapter.out.dto.BithumbCandleDto;
import com.coanalysis.server.market.adapter.out.dto.BithumbMarketDto;
import com.coanalysis.server.market.adapter.out.dto.BithumbTickerDto;
import com.coanalysis.server.market.application.port.in.MarketUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketService implements MarketUseCase {

    private final BithumbClient bithumbClient;

    @Override
    @Cacheable(value = CacheConfig.MARKET_CACHE, key = "'candles:' + #symbol + ':' + #unit + ':' + #count")
    public List<CandleResponse> getCandles(String symbol, int unit, int count) {
        log.info("Fetching candles for symbol: {}, unit: {}, count: {}", symbol, unit, count);

        String market = "KRW-" + symbol.toUpperCase();
        List<BithumbCandleDto> candles = bithumbClient.getCandles(market, unit, count);

        return CandleResponse.from(candles);
    }

    @Override
    @Cacheable(value = CacheConfig.MARKET_CACHE, key = "'tickers:' + #marketType + ':' + #sortBy")
    public List<TickerResponse> getTickers(String marketType, String sortBy) {
        log.info("Fetching tickers for marketType: {}, sortBy: {}", marketType, sortBy);

        List<BithumbMarketDto> allMarkets = bithumbClient.getMarkets();

        List<BithumbMarketDto> filteredMarkets = allMarkets.stream()
                .filter(m -> m.getMarketType().equalsIgnoreCase(marketType))
                .toList();

        if (filteredMarkets.isEmpty()) {
            return List.of();
        }

        List<String> marketCodes = filteredMarkets.stream()
                .map(BithumbMarketDto::getMarket)
                .toList();

        List<BithumbTickerDto> tickers = bithumbClient.getTickers(marketCodes);

        Map<String, BithumbMarketDto> marketInfoMap = filteredMarkets.stream()
                .collect(Collectors.toMap(BithumbMarketDto::getMarket, Function.identity()));

        List<TickerResponse> responses = tickers.stream()
                .map(ticker -> TickerResponse.from(ticker, marketInfoMap.get(ticker.getMarket())))
                .toList();

        if ("tradeValue".equalsIgnoreCase(sortBy)) {
            return responses.stream()
                    .sorted(Comparator.comparing(TickerResponse::getTradeValue, Comparator.nullsLast(Comparator.reverseOrder())))
                    .toList();
        }

        return responses;
    }
}
