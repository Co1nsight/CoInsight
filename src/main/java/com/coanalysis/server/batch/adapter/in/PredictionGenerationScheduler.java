package com.coanalysis.server.batch.adapter.in;

import com.coanalysis.server.prediction.application.domain.CryptoPrediction;
import com.coanalysis.server.prediction.application.port.in.GeneratePredictionUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PredictionGenerationScheduler {

    private final GeneratePredictionUseCase generatePredictionUseCase;

    private static final int MINIMUM_NEWS_COUNT = 20;
    private static final int PREDICTION_INTERVAL_HOURS = 12;

    /**
     * 12시간마다 모든 코인에 대한 예측 생성 (0시 30분, 12시 30분)
     * - 뉴스 수집과 겹치지 않도록 30분에 실행
     * - 최근 12시간 동안 수집된 뉴스가 20개 이하이면 스킵
     * cron: 초 분 시 일 월 요일
     */
    @Scheduled(cron = "0 30 0,12 * * *")
    public void generatePredictions() {
        log.info("=== Prediction generation started ({}h interval) ===", PREDICTION_INTERVAL_HOURS);
        long startTime = System.currentTimeMillis();

        try {
            // 최근 12시간 동안 수집된 뉴스 개수 확인
            long recentNewsCount = generatePredictionUseCase.countRecentNews(PREDICTION_INTERVAL_HOURS);

            if (recentNewsCount < MINIMUM_NEWS_COUNT) {
                log.info("=== Prediction generation skipped === Recent news count: {} (minimum: {}). Insufficient data.",
                        recentNewsCount, MINIMUM_NEWS_COUNT);
                return;
            }

            List<CryptoPrediction> predictions = generatePredictionUseCase.generateAllPredictions();
            long duration = System.currentTimeMillis() - startTime;

            log.info("=== Prediction generation completed === Generated: {} predictions, Recent news: {}, Duration: {}ms",
                    predictions.size(), recentNewsCount, duration);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("=== Prediction generation failed === Duration: {}ms, Error: {}",
                    duration, e.getMessage(), e);
        }
    }
}
