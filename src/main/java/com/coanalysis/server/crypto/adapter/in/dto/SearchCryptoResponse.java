package com.coanalysis.server.crypto.adapter.in.dto;

import com.coanalysis.server.crypto.application.domain.Crypto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Schema(description = "암호화폐 검색 결과")
public class SearchCryptoResponse {

    @Schema(description = "코인 고유 ID", example = "1")
    private Long id;

    @Schema(description = "코인 티커 (심볼)", example = "BTC")
    private String ticker;

    @Schema(description = "코인 한글명", example = "비트코인")
    private String name;

    @Schema(description = "코인 로고 이미지 URL", example = "https://example.com/btc.png")
    private String logoUrl;

    @Schema(description = "현재가 (KRW)", example = "135000000.0")
    private double currentPrice;

    @Schema(description = "24시간 거래대금 (KRW)", example = "500000000000.0")
    private double tradingVolume;


    public static SearchCryptoResponse of(Crypto crypto){
        return SearchCryptoResponse.builder()
                .id(crypto.getId())
                .ticker(crypto.getTicker())
                .name(crypto.getName())
                .logoUrl(crypto.getLogoUrl())
                .currentPrice(crypto.getCurrentPrice())
                .tradingVolume(crypto.getTradingVolume())
                .build();
    }

    public static List<SearchCryptoResponse> from(List<Crypto> cryptoList) {
        return cryptoList.stream()
                .map(SearchCryptoResponse::of)
                .toList();
    }
}
