package com.coanalysis.server.market.adapter.in.swagger;

import com.coanalysis.server.infrastructure.response.ApiResponse;
import com.coanalysis.server.market.adapter.in.dto.CandleResponse;
import com.coanalysis.server.market.adapter.in.dto.TickerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Market", description = "시세 조회 API")
public interface MarketControllerSwagger {

    @Operation(
            summary = "코인 분봉 차트 조회",
            description = "특정 코인의 분봉 캔들 데이터를 조회합니다. 1분 단위로 캐싱됩니다."
    )
    ResponseEntity<ApiResponse<List<CandleResponse>>> getCandles(
            @Parameter(name = "symbol", description = "코인 심볼 (예: BTC, ETH)", required = true, example = "BTC")
            String symbol,
            @Parameter(name = "unit", description = "분봉 단위 (1, 3, 5, 10, 15, 30, 60, 240)", example = "1")
            Integer unit,
            @Parameter(name = "count", description = "조회할 캔들 개수 (최대 200)", example = "200")
            Integer count
    );

    @Operation(
            summary = "전체 코인 목록 조회",
            description = "홈화면용 전체 코인 시세 목록을 조회합니다. 1분 단위로 캐싱됩니다."
    )
    ResponseEntity<ApiResponse<List<TickerResponse>>> getTickers(
            @Parameter(name = "marketType", description = "마켓 타입 (KRW: 원화마켓, BTC: 비트코인마켓)", example = "KRW")
            String marketType,
            @Parameter(name = "sortBy", description = "정렬 기준 (tradeValue: 거래대금순)", example = "tradeValue")
            String sortBy
    );
}
