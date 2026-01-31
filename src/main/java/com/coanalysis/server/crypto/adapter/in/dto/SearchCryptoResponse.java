package com.coanalysis.server.crypto.adapter.in.dto;

import com.coanalysis.server.crypto.application.domain.Crypto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Schema(description = "암호화폐 검색 결과")
public class SearchCryptoResponse {

    @Schema(description = "코인 티커 (심볼)", example = "BTC")
    private String ticker;

    @Schema(description = "코인 한글명", example = "비트코인")
    private String name;

    @Schema(description = "코인 로고 이미지 URL", example = "https://example.com/btc.png")
    private String logoUrl;

    @Schema(description = "마켓 (결제 통화)", example = "KRW")
    private String market;

    @Schema(description = "현재가", example = "135000000.0")
    private String currentPrice;

    @Schema(description = "24시간 거래대금", example = "500000000000.0")
    private String tradingVolume;


    public static SearchCryptoResponse of(Crypto crypto){
        return of(crypto, "KRW");
    }

    public static SearchCryptoResponse of(Crypto crypto, String market){
        return SearchCryptoResponse.builder()
                .ticker(crypto.getTicker())
                .name(crypto.getName())
                .logoUrl(crypto.getLogoUrl())
                .market(market.toUpperCase())
                .currentPrice(BigDecimal.valueOf(crypto.getCurrentPrice()).toPlainString())
                .tradingVolume(BigDecimal.valueOf(crypto.getTradingVolume()).toPlainString())
                .build();
    }

    public static List<SearchCryptoResponse> from(List<Crypto> cryptoList) {
        return cryptoList.stream()
                .map(SearchCryptoResponse::of)
                .toList();
    }
}
