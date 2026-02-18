package com.coanalysis.server.prediction.application.port.out;

import java.time.LocalDateTime;
import java.util.Map;

public interface LoadNewsAnalysisPort {

    Map<String, Integer> countNewsBySentiment(String ticker, LocalDateTime from, LocalDateTime to);

    /**
     * 마지막 예측 시간 이후에 발행된 뉴스만 집계합니다.
     * 이미 다른 예측에서 사용된 기사를 제외하기 위함입니다.
     *
     * @param ticker 코인 티커
     * @param lastPredictionTime 마지막 예측 시간 (이 시간 이후의 뉴스만 조회)
     * @param to 현재 시간
     * @return 감성 라벨별 뉴스 개수
     */
    Map<String, Integer> countUnusedNewsBySentiment(String ticker, LocalDateTime lastPredictionTime, LocalDateTime to);
}
