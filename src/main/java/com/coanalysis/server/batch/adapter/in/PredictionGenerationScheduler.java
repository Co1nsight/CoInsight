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

    /**
     * 매일 09:00에 모든 코인에 대한 예측 생성
     * cron: 초 분 시 일 월 요일
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void generateDailyPredictions() {
        log.info("=== Daily prediction generation started ===");
        long startTime = System.currentTimeMillis();

        try {
            List<CryptoPrediction> predictions = generatePredictionUseCase.generateAllPredictions();
            long duration = System.currentTimeMillis() - startTime;

            log.info("=== Daily prediction generation completed === Generated: {} predictions, Duration: {}ms",
                    predictions.size(), duration);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("=== Daily prediction generation failed === Duration: {}ms, Error: {}",
                    duration, e.getMessage(), e);
        }
    }
}
