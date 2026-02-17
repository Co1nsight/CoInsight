package com.coanalysis.server.batch.adapter.in;

import com.coanalysis.server.prediction.application.port.in.VerifyPredictionUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PredictionVerificationScheduler {

    private final VerifyPredictionUseCase verifyPredictionUseCase;

    /**
     * 매 시간 정각에 미검증 예측 결과 검증
     * cron: 초 분 시 일 월 요일
     */
    @Scheduled(cron = "0 0 * * * *")
    public void verifyPredictions() {
        log.info("=== Prediction verification started ===");
        long startTime = System.currentTimeMillis();

        try {
            verifyPredictionUseCase.verifyAllPendingPredictions();
            long duration = System.currentTimeMillis() - startTime;

            log.info("=== Prediction verification completed === Duration: {}ms", duration);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("=== Prediction verification failed === Duration: {}ms, Error: {}",
                    duration, e.getMessage(), e);
        }
    }
}
