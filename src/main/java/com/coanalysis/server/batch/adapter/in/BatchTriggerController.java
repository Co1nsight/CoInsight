package com.coanalysis.server.batch.adapter.in;

import com.coanalysis.server.batch.adapter.in.swagger.BatchTriggerControllerSwagger;
import com.coanalysis.server.batch.application.port.in.CollectNewsUseCase;
import com.coanalysis.server.crypto.application.port.in.SyncCryptoUsecase;
import com.coanalysis.server.infrastructure.response.ApiResponse;
import com.coanalysis.server.prediction.application.domain.CryptoPrediction;
import com.coanalysis.server.prediction.application.port.in.GeneratePredictionUseCase;
import com.coanalysis.server.prediction.application.port.in.VerifyPredictionUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/batch")
@RequiredArgsConstructor
public class BatchTriggerController implements BatchTriggerControllerSwagger {

    private final CollectNewsUseCase collectNewsUseCase;
    private final SyncCryptoUsecase syncCryptoUsecase;
    private final GeneratePredictionUseCase generatePredictionUseCase;
    private final VerifyPredictionUseCase verifyPredictionUseCase;

    @Override
    @PostMapping("/trigger/news-collection")
    public ResponseEntity<ApiResponse<String>> triggerNewsCollection() {
        log.info("[Manual Trigger] News collection batch started");
        long startTime = System.currentTimeMillis();

        int processedCount = collectNewsUseCase.collectAndProcessNews();
        long duration = System.currentTimeMillis() - startTime;

        log.info("[Manual Trigger] News collection batch completed. Processed: {}, Duration: {}ms",
                processedCount, duration);

        return ResponseEntity.ok(ApiResponse.success(
                String.format("News collection completed. Processed: %d articles", processedCount)));
    }

    @Override
    @PostMapping("/trigger/crypto-sync")
    public ResponseEntity<ApiResponse<String>> triggerCryptoSync() {
        log.info("[Manual Trigger] Crypto sync batch started");
        long startTime = System.currentTimeMillis();

        int addedCount = syncCryptoUsecase.syncCryptos();
        long duration = System.currentTimeMillis() - startTime;

        log.info("[Manual Trigger] Crypto sync batch completed. Added: {}, Duration: {}ms",
                addedCount, duration);

        return ResponseEntity.ok(ApiResponse.success(
                String.format("Crypto sync completed. Added: %d cryptos", addedCount)));
    }

    @Override
    @PostMapping("/trigger/prediction-generation")
    public ResponseEntity<ApiResponse<String>> triggerPredictionGeneration() {
        log.info("[Manual Trigger] Prediction generation batch started");
        long startTime = System.currentTimeMillis();

        List<CryptoPrediction> predictions = generatePredictionUseCase.generateAllPredictions();
        long duration = System.currentTimeMillis() - startTime;

        log.info("[Manual Trigger] Prediction generation batch completed. Generated: {}, Duration: {}ms",
                predictions.size(), duration);

        return ResponseEntity.ok(ApiResponse.success(
                String.format("Prediction generation completed. Generated: %d predictions", predictions.size())));
    }

    @Override
    @PostMapping("/trigger/prediction-verification")
    public ResponseEntity<ApiResponse<String>> triggerPredictionVerification() {
        log.info("[Manual Trigger] Prediction verification batch started");
        long startTime = System.currentTimeMillis();

        verifyPredictionUseCase.verifyAllPendingPredictions();
        long duration = System.currentTimeMillis() - startTime;

        log.info("[Manual Trigger] Prediction verification batch completed. Duration: {}ms", duration);

        return ResponseEntity.ok(ApiResponse.success("Prediction verification completed"));
    }

    @Override
    @PostMapping("/trigger/logo-url-update")
    public ResponseEntity<ApiResponse<String>> triggerLogoUrlUpdate() {
        log.info("[Manual Trigger] Logo URL update started");
        long startTime = System.currentTimeMillis();

        int updatedCount = syncCryptoUsecase.updateAllLogoUrls();
        long duration = System.currentTimeMillis() - startTime;

        log.info("[Manual Trigger] Logo URL update completed. Updated: {}, Duration: {}ms",
                updatedCount, duration);

        return ResponseEntity.ok(ApiResponse.success(
                String.format("Logo URL update completed. Updated: %d cryptos", updatedCount)));
    }
}
