package com.coanalysis.server.market.adapter.in.dto;

import com.coanalysis.server.market.adapter.out.dto.BithumbCandleDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@Schema(description = "캔들 차트 데이터")
public class CandleResponse {

    @Schema(description = "캔들 시작 시간 (Unix timestamp, milliseconds)", example = "1705312800000")
    private Long timestamp;

    @Schema(description = "시가 - 해당 기간 시작 가격", example = "135000000")
    private BigDecimal openPrice;

    @Schema(description = "고가 - 해당 기간 최고 가격", example = "135500000")
    private BigDecimal highPrice;

    @Schema(description = "저가 - 해당 기간 최저 가격", example = "134800000")
    private BigDecimal lowPrice;

    @Schema(description = "종가 - 해당 기간 마지막 가격", example = "135200000")
    private BigDecimal closePrice;

    @Schema(description = "거래량 - 해당 기간 거래된 코인 수량", example = "12.5")
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
