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

    private static final int MINIMUM_NEWS_COUNT = 10;

    /**
     * 매시간 30분에 모든 코인에 대한 예측 생성
     * - 뉴스 수집(0분, 15분, 30분, 45분)과 겹치지 않도록 30분에 실행
     * - 최근 1시간 동안 수집된 뉴스가 10개 이하이면 다음 배치로 스킵
     * cron: 초 분 시 일 월 요일
     */
    @Scheduled(cron = "0 30 * * * *")
    public void generateHourlyPredictions() {
        log.info("=== Hourly prediction generation started ===");
        long startTime = System.currentTimeMillis();

        try {
            // 최근 1시간 동안 수집된 뉴스 개수 확인
            long recentNewsCount = generatePredictionUseCase.countRecentNews(1);

            if (recentNewsCount <= MINIMUM_NEWS_COUNT) {
                log.info("=== Hourly prediction generation skipped === Recent news count: {} (minimum: {}). Will accumulate for next batch.",
                        recentNewsCount, MINIMUM_NEWS_COUNT);
                return;
            }

            List<CryptoPrediction> predictions = generatePredictionUseCase.generateAllPredictions();
            long duration = System.currentTimeMillis() - startTime;

            log.info("=== Hourly prediction generation completed === Generated: {} predictions, Recent news: {}, Duration: {}ms",
                    predictions.size(), recentNewsCount, duration);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("=== Hourly prediction generation failed === Duration: {}ms, Error: {}",
                    duration, e.getMessage(), e);
        }
    }
}
