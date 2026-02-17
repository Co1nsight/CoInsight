package com.coanalysis.server.main.adapter.in.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "메인화면 코인 시세 정보")
public class MainCryptoResponse {

    @Schema(description = "코인 티커 (심볼)", example = "BTC")
    private String ticker;

    @Schema(description = "코인 한글명", example = "비트코인")
    private String name;

    @Schema(description = "코인 로고 URL", example = "https://example.com/btc.png")
    private String logoUrl;

    @Schema(description = "현재가 (KRW)", example = "135000000")
    private BigDecimal currentPrice;

    @Schema(description = "24시간 거래대금 (KRW)", example = "500000000000")
    private BigDecimal tradingVolume24h;

    @Schema(description = "등락률 (-1.0 ~ 1.0, 예: 0.05 = +5%)", example = "0.0523")
    private BigDecimal changeRate;

    @Schema(description = "등락가 (KRW)", example = "6500000")
    private BigDecimal changePrice;

    @Schema(description = "고가 (24시간)", example = "138000000")
    private BigDecimal highPrice;

    @Schema(description = "저가 (24시간)", example = "132000000")
    private BigDecimal lowPrice;
}
