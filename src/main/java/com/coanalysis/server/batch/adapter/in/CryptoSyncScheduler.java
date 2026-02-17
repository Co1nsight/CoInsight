package com.coanalysis.server.batch.adapter.in;

import com.coanalysis.server.crypto.application.port.in.SyncCryptoUsecase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CryptoSyncScheduler {

    private final SyncCryptoUsecase syncCryptoUsecase;

    /**
     * 매일 새벽 3시에 코인 목록을 동기화합니다.
     * cron: 초 분 시 일 월 요일
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void syncCryptosJob() {
        log.info("=== 코인 동기화 배치 시작 ===");
        try {
            int addedCount = syncCryptoUsecase.syncCryptos();
            log.info("=== 코인 동기화 배치 완료 === 추가된 코인: {} 개", addedCount);
        } catch (Exception e) {
            log.error("=== 코인 동기화 배치 실패 ===", e);
        }
    }
}
