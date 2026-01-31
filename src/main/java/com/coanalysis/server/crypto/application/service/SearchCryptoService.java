package com.coanalysis.server.crypto.application.service;

import com.coanalysis.server.crypto.application.domain.Crypto;
import com.coanalysis.server.crypto.application.port.in.SearchCryptoUsecase;
import com.coanalysis.server.crypto.application.port.out.SearchCryptoPort;
import com.coanalysis.server.market.adapter.out.BithumbClient;
import com.coanalysis.server.market.adapter.out.dto.BithumbTickerDto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SearchCryptoService implements SearchCryptoUsecase {
    private final SearchCryptoPort searchCryptoPort;
    private final BithumbClient bithumbClient;

    @Override
    public List<Crypto> searchByKeyword(String keyword) {
        return searchCryptoPort.searchByKeyword(keyword);
    }

    @Override
    public Optional<Crypto> findByTicker(String ticker) {
        return searchCryptoPort.findByTicker(ticker);
    }

    @Override
    public Optional<Crypto> findByTickerAndMarket(String ticker, String market) {
        Optional<Crypto> cryptoOpt = searchCryptoPort.findByTicker(ticker);
        if (cryptoOpt.isEmpty()) {
            return Optional.empty();
        }

        Crypto crypto = cryptoOpt.get();
        String marketCode = market.toUpperCase() + "-" + ticker.toUpperCase();

        List<BithumbTickerDto> tickers = bithumbClient.getTickers(List.of(marketCode));
        if (tickers == null || tickers.isEmpty()) {
            return cryptoOpt;
        }

        BithumbTickerDto tickerDto = tickers.get(0);
        return Optional.of(Crypto.builder()
                .ticker(crypto.getTicker())
                .name(crypto.getName())
                .logoUrl(crypto.getLogoUrl())
                .currentPrice(tickerDto.getTradePrice().doubleValue())
                .tradingVolume(tickerDto.getAccTradePrice24h().doubleValue())
                .build());
    }
}
