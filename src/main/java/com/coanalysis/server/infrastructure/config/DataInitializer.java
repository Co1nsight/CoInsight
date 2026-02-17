package com.coanalysis.server.infrastructure.config;

import com.coanalysis.server.crypto.application.port.in.SyncCryptoUsecase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SyncCryptoUsecase syncCryptoUsecase;

    @Override
    public void run(String... args) {
        log.info("애플리케이션 시작 - 코인 목록 동기화 실행");

        try {
            int addedCount = syncCryptoUsecase.syncCryptos();
            log.info("코인 동기화 완료 - 추가된 코인: {} 개", addedCount);
        } catch (Exception e) {
            log.error("코인 동기화 실패", e);
        }

        log.info("초기화 완료!");
    }
}
