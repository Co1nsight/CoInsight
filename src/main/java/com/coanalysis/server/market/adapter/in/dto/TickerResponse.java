package com.coanalysis.server.market.adapter.in.dto;

import com.coanalysis.server.market.adapter.out.dto.BithumbMarketDto;
import com.coanalysis.server.market.adapter.out.dto.BithumbTickerDto;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class TickerResponse {

    private String symbol;
    private String name;
    private BigDecimal price;
    private BigDecimal changeRate;
    private BigDecimal changePrice;
    private BigDecimal tradeValue;
    private String market;

    public static TickerResponse from(BithumbTickerDto ticker, BithumbMarketDto marketInfo) {
        return TickerResponse.builder()
                .symbol(ticker.getSymbol())
                .name(marketInfo != null ? marketInfo.getKoreanName() : ticker.getSymbol())
                .price(ticker.getTradePrice())
                .changeRate(ticker.getSignedChangeRate())
                .changePrice(ticker.getSignedChangePrice())
                .tradeValue(ticker.getAccTradePrice24h())
                .market(ticker.getMarketType())
                .build();
    }
}
