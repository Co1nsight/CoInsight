package com.coanalysis.server.market.application.port.in;

import com.coanalysis.server.market.adapter.in.dto.CandleResponse;
import com.coanalysis.server.market.adapter.in.dto.TickerResponse;
import com.coanalysis.server.market.domain.CandleType;

import java.time.LocalDateTime;
import java.util.List;

public interface MarketUseCase {

    List<CandleResponse> getCandles(String symbol, CandleType candleType, Integer unit, int count, LocalDateTime to);

    List<TickerResponse> getTickers(String marketType, String sortBy);
}
