package com.coanalysis.server.prediction.application.port.out;

public interface FetchCryptoPricePort {

    Double fetchCurrentPrice(String ticker);
}
