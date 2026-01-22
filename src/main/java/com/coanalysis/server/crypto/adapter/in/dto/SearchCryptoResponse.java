package com.coanalysis.server.crypto.adapter.in.dto;

import com.coanalysis.server.crypto.application.domain.Crypto;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SearchCryptoResponse {

    private Long id;
    private String ticker;
    private String name;
    private String logoUrl;
    private double currentPrice; //현재가
    private double tradingVolume; //거래대금


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
//dto설정