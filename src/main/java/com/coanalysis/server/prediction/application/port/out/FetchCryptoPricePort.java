package com.coanalysis.server.prediction.application.port.out;

import java.util.List;
import java.util.Map;

public interface FetchCryptoPricePort {

    Double fetchCurrentPrice(String ticker);

    /**
     * 여러 코인의 현재 가격을 한 번에 조회합니다.
     * Rate limiting을 고려하여 배치로 나눠서 호출합니다.
     *
     * @param tickers 조회할 코인 티커 목록
     * @return ticker -> price 맵
     */
    Map<String, Double> fetchAllPrices(List<String> tickers);
}
