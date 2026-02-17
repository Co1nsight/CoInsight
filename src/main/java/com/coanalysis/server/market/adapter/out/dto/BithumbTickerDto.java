package com.coanalysis.server.market.adapter.out.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BithumbTickerDto {

    @JsonProperty("market")
    private String market;

    @JsonProperty("trade_price")
    private BigDecimal tradePrice;

    @JsonProperty("signed_change_rate")
    private BigDecimal signedChangeRate;

    @JsonProperty("signed_change_price")
    private BigDecimal signedChangePrice;

    @JsonProperty("acc_trade_price_24h")
    private BigDecimal accTradePrice24h;

    @JsonProperty("opening_price")
    private BigDecimal openingPrice;

    @JsonProperty("high_price")
    private BigDecimal highPrice;

    @JsonProperty("low_price")
    private BigDecimal lowPrice;

    @JsonProperty("prev_closing_price")
    private BigDecimal prevClosingPrice;

    @JsonProperty("acc_trade_volume_24h")
    private BigDecimal accTradeVolume24h;

    public String getSymbol() {
        if (market != null && market.contains("-")) {
            return market.split("-")[1];
        }
        return market;
    }

    public String getMarketType() {
        if (market != null && market.contains("-")) {
            return market.split("-")[0];
        }
        return "KRW";
    }
}
