package com.coanalysis.server.batch.adapter.in;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.coanalysis.server.batch.application.port.in.CollectNewsUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsCollectionScheduler {

    private final CollectNewsUseCase collectNewsUseCase;

    /**
     * 1시간마다 뉴스 수집 배치 실행
     * Virtual Thread에서 자동 실행됨 (spring.threads.virtual.enabled=true)
     */
    @Scheduled(fixedRate = 3600000) // 1시간 = 3,600,000ms
    public void collectNewsJob() {
        log.info("=== News collection batch started ===");
        long startTime = System.currentTimeMillis();

        try {
            int processedCount = collectNewsUseCase.collectAndProcessNews();
            long duration = System.currentTimeMillis() - startTime;

            log.info("=== News collection batch completed === Processed: {} articles, Duration: {}ms",
                    processedCount, duration);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("=== News collection batch failed === Duration: {}ms, Error: {}",
                    duration, e.getMessage(), e);
        }
    }
}
