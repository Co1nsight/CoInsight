package com.coanalysis.server.market.application.port.in;

import com.coanalysis.server.market.adapter.in.dto.CandleResponse;
import com.coanalysis.server.market.adapter.in.dto.TickerResponse;

import java.util.List;

public interface MarketUseCase {

    List<CandleResponse> getCandles(String symbol, int unit, int count);

    List<TickerResponse> getTickers(String marketType, String sortBy);
}
