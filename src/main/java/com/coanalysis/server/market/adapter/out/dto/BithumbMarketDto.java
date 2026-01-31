package com.coanalysis.server.market.adapter.out.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BithumbMarketDto {

    @JsonProperty("market")
    private String market;

    @JsonProperty("korean_name")
    private String koreanName;

    @JsonProperty("english_name")
    private String englishName;

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
