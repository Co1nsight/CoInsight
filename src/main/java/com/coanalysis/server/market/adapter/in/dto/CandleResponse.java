package com.coanalysis.server.market.adapter.in.dto;

import com.coanalysis.server.market.adapter.out.dto.BithumbCandleDto;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class CandleResponse {

    private Long timestamp;
    private BigDecimal openPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal closePrice;
    private BigDecimal volume;

    public static CandleResponse from(BithumbCandleDto dto) {
        return CandleResponse.builder()
                .timestamp(dto.getTimestamp())
                .openPrice(dto.getOpeningPrice())
                .highPrice(dto.getHighPrice())
                .lowPrice(dto.getLowPrice())
                .closePrice(dto.getTradePrice())
                .volume(dto.getCandleAccTradeVolume())
                .build();
    }

    public static List<CandleResponse> from(List<BithumbCandleDto> dtos) {
        return dtos.stream()
                .map(CandleResponse::from)
                .toList();
    }
}
