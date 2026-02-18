package com.coanalysis.server.prediction.application.port.in;

import com.coanalysis.server.prediction.application.domain.CryptoPrediction;

import java.util.List;

public interface GeneratePredictionUseCase {

    CryptoPrediction generatePrediction(String ticker);

    List<CryptoPrediction> generateAllPredictions();

    /**
     * 지정된 시간 이후에 수집된 뉴스 개수를 반환합니다.
     * @param hoursAgo 몇 시간 전부터의 뉴스를 카운트할지
     * @return 뉴스 개수
     */
    long countRecentNews(int hoursAgo);
}
