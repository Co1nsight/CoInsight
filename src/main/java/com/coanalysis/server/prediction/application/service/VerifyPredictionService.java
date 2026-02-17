package com.coanalysis.server.prediction.application.service;

import com.coanalysis.server.prediction.application.domain.CryptoPrediction;
import com.coanalysis.server.prediction.application.domain.PredictionVerification;
import com.coanalysis.server.prediction.application.enums.IntervalType;
import com.coanalysis.server.prediction.application.enums.PredictionLabel;
import com.coanalysis.server.prediction.application.port.in.VerifyPredictionUseCase;
import com.coanalysis.server.prediction.application.port.out.FetchCryptoPricePort;
import com.coanalysis.server.prediction.application.port.out.LoadPredictionPort;
import com.coanalysis.server.prediction.application.port.out.SavePredictionPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class VerifyPredictionService implements VerifyPredictionUseCase {

    private final LoadPredictionPort loadPredictionPort;
    private final FetchCryptoPricePort fetchCryptoPricePort;
    private final SavePredictionPort savePredictionPort;

    private static final double NEUTRAL_THRESHOLD = 1.0; // 1% 미만 변동 시 중립 성공

    @Override
    public List<PredictionVerification> verifyPendingPredictions(IntervalType intervalType) {
        log.info("Verifying pending predictions for interval: {}", intervalType);

        // 검증 대상 시간 계산 (현재 시간 - 간격)
        LocalDateTime targetTime = LocalDateTime.now().minusHours(intervalType.getHours());

        // 미검증 예측 조회
        List<CryptoPrediction> unverifiedPredictions =
                loadPredictionPort.loadUnverifiedPredictions(targetTime, intervalType);

        log.info("Found {} unverified predictions for interval {}", unverifiedPredictions.size(), intervalType);

        if (unverifiedPredictions.isEmpty()) {
            return new ArrayList<>();
        }

        // 1. 모든 티커를 수집하여 한 번에 가격 조회 (rate limit 방지)
        List<String> tickers = unverifiedPredictions.stream()
                .map(p -> p.getCrypto().getTicker())
                .distinct()
                .toList();
        Map<String, Double> priceMap = fetchCryptoPricePort.fetchAllPrices(tickers);
        log.info("Fetched prices for {} tickers in a single request", priceMap.size());

        // 2. 조회한 가격을 사용하여 각 예측 검증
        List<PredictionVerification> verifications = new ArrayList<>();

        for (CryptoPrediction prediction : unverifiedPredictions) {
            try {
                // 이미 검증된 경우 스킵
                if (loadPredictionPort.isVerificationExists(prediction.getId(), intervalType)) {
                    continue;
                }

                String ticker = prediction.getCrypto().getTicker();
                Double currentPrice = priceMap.get(ticker);

                PredictionVerification verification = verifyPredictionWithPrice(prediction, intervalType, currentPrice);
                if (verification != null) {
                    verifications.add(verification);
                }
            } catch (Exception e) {
                log.error("Failed to verify prediction {}: {}", prediction.getId(), e.getMessage());
            }
        }

        log.info("Verified {} predictions for interval {}", verifications.size(), intervalType);
        return verifications;
    }

    @Override
    public void verifyAllPendingPredictions() {
        log.info("Verifying all pending predictions");

        for (IntervalType intervalType : IntervalType.values()) {
            try {
                verifyPendingPredictions(intervalType);
            } catch (Exception e) {
                log.error("Failed to verify predictions for interval {}: {}", intervalType, e.getMessage());
            }
        }
    }

    private PredictionVerification verifyPredictionWithPrice(CryptoPrediction prediction, IntervalType intervalType, Double currentPrice) {
        String ticker = prediction.getCrypto().getTicker();

        if (currentPrice == null || currentPrice == 0) {
            log.warn("Price not available for {}", ticker);
            return null;
        }

        // 가격 변동률 계산
        Double priceAtPrediction = prediction.getPriceAtPrediction();
        if (priceAtPrediction == null || priceAtPrediction == 0) {
            log.warn("Invalid price at prediction for prediction {}", prediction.getId());
            return null;
        }

        double priceChangePercent = ((currentPrice - priceAtPrediction) / priceAtPrediction) * 100;

        // 성공 여부 판정
        boolean isSuccess = determineSuccess(prediction.getPredictionLabel(), priceChangePercent);

        PredictionVerification verification = PredictionVerification.builder()
                .prediction(prediction)
                .intervalType(intervalType)
                .verifiedAt(LocalDateTime.now())
                .priceAtVerification(currentPrice)
                .priceChangePercent(Math.round(priceChangePercent * 100) / 100.0)
                .isSuccess(isSuccess)
                .build();

        PredictionVerification savedVerification = savePredictionPort.saveVerification(verification);

        log.debug("Verified prediction {} for {} with interval {}: change={}%, success={}",
                prediction.getId(), ticker, intervalType, priceChangePercent, isSuccess);

        return savedVerification;
    }

    private boolean determineSuccess(PredictionLabel predictionLabel, double priceChangePercent) {
        return switch (predictionLabel) {
            case UP -> priceChangePercent > 0;
            case DOWN -> priceChangePercent < 0;
            case NEUTRAL -> Math.abs(priceChangePercent) < NEUTRAL_THRESHOLD;
        };
    }
}
