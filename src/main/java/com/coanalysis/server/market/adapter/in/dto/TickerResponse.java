package com.coanalysis.server.market.adapter.in.dto;

import com.coanalysis.server.market.adapter.out.dto.BithumbMarketDto;
import com.coanalysis.server.market.adapter.out.dto.BithumbTickerDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@Schema(description = "코인 시세 정보")
public class TickerResponse {

    @Schema(description = "코인 심볼 (티커)", example = "BTC")
    private String symbol;

    @Schema(description = "코인 한글명", example = "비트코인")
    private String name;

    @Schema(description = "현재가", example = "135000000")
    private BigDecimal price;

    @Schema(description = "24시간 변동률 (소수점, 0.05 = 5%)", example = "0.0523")
    private BigDecimal changeRate;

    @Schema(description = "24시간 변동 금액", example = "6700000")
    private BigDecimal changePrice;

    @Schema(description = "24시간 거래대금", example = "892000000000")
    private BigDecimal tradeValue;

    @Schema(description = "마켓 타입 (KRW: 원화마켓, BTC: 비트코인마켓)", example = "KRW")
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
