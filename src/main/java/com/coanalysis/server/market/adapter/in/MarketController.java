package com.coanalysis.server.market.adapter.in;

import com.coanalysis.server.infrastructure.response.ApiResponse;
import com.coanalysis.server.market.adapter.in.dto.CandleResponse;
import com.coanalysis.server.market.adapter.in.dto.TickerResponse;
import com.coanalysis.server.market.adapter.in.swagger.MarketControllerSwagger;
import com.coanalysis.server.market.application.port.in.MarketUseCase;
import com.coanalysis.server.market.domain.CandleType;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/market")
@RequiredArgsConstructor
public class MarketController implements MarketControllerSwagger {

    private final MarketUseCase marketUseCase;

    @Override
    @GetMapping("/candles/{symbol}")
    public ResponseEntity<ApiResponse<List<CandleResponse>>> getCandles(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "MINUTES") CandleType candleType,
            @RequestParam(required = false) Integer unit,
            @RequestParam(defaultValue = "200") Integer count,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        Integer effectiveUnit = (candleType == CandleType.MINUTES && unit == null) ? 1 : unit;
        List<CandleResponse> candles = marketUseCase.getCandles(symbol, candleType, effectiveUnit, count, to);
        return ResponseEntity.ok(ApiResponse.success(candles));
    }

    @Override
    @GetMapping("/tickers")
    public ResponseEntity<ApiResponse<List<TickerResponse>>> getTickers(
            @RequestParam(defaultValue = "KRW") String marketType,
            @RequestParam(defaultValue = "tradeValue") String sortBy
    ) {
        List<TickerResponse> tickers = marketUseCase.getTickers(marketType, sortBy);
        return ResponseEntity.ok(ApiResponse.success(tickers));
    }
}
