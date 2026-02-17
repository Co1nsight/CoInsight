package com.coanalysis.server.prediction.application.service;

import com.coanalysis.server.crypto.application.domain.Crypto;
import com.coanalysis.server.infrastructure.repository.CryptoRepository;
import com.coanalysis.server.prediction.application.domain.CryptoPrediction;
import com.coanalysis.server.prediction.application.enums.PredictionLabel;
import com.coanalysis.server.prediction.application.port.in.GeneratePredictionUseCase;
import com.coanalysis.server.prediction.application.port.out.FetchCryptoPricePort;
import com.coanalysis.server.prediction.application.port.out.LoadNewsAnalysisPort;
import com.coanalysis.server.prediction.application.port.out.SavePredictionPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GeneratePredictionService implements GeneratePredictionUseCase {

    private final CryptoRepository cryptoRepository;
    private final LoadNewsAnalysisPort loadNewsAnalysisPort;
    private final FetchCryptoPricePort fetchCryptoPricePort;
    private final SavePredictionPort savePredictionPort;

    private static final double POSITIVE_THRESHOLD = 0.6;
    private static final double NEGATIVE_THRESHOLD = 0.4;

    @Override
    public CryptoPrediction generatePrediction(String ticker) {
        log.info("Generating prediction for ticker: {}", ticker);

        // 코인 정보 조회
        Optional<Crypto> cryptoOpt = cryptoRepository.findById(ticker.toUpperCase());
        if (cryptoOpt.isEmpty()) {
            log.warn("Crypto not found for ticker: {}", ticker);
            return null;
        }
        Crypto crypto = cryptoOpt.get();

        // 최근 24시간 뉴스 감성 분석 결과 조회
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.minusHours(24);
        Map<String, Integer> sentimentCounts = loadNewsAnalysisPort.countNewsBySentiment(ticker, from, now);

        int positiveCount = sentimentCounts.getOrDefault("positive", 0);
        int negativeCount = sentimentCounts.getOrDefault("negative", 0);
        int neutralCount = sentimentCounts.getOrDefault("neutral", 0);
        int totalNonNeutral = positiveCount + negativeCount;

        // 긍정 비율 계산 (중립 제외)
        double positiveRatio = totalNonNeutral > 0
                ? (double) positiveCount / totalNonNeutral
                : 0.5; // 뉴스가 없으면 중립으로

        // 예측 라벨 결정
        PredictionLabel predictionLabel;
        if (positiveRatio > POSITIVE_THRESHOLD) {
            predictionLabel = PredictionLabel.UP;
        } else if (positiveRatio < NEGATIVE_THRESHOLD) {
            predictionLabel = PredictionLabel.DOWN;
        } else {
            predictionLabel = PredictionLabel.NEUTRAL;
        }

        // 현재 가격 조회
        Double currentPrice = fetchCryptoPricePort.fetchCurrentPrice(ticker);
        if (currentPrice == null) {
            log.warn("Failed to fetch current price for ticker: {}", ticker);
            currentPrice = 0.0;
        }

        // 예측 저장
        CryptoPrediction prediction = CryptoPrediction.builder()
                .crypto(crypto)
                .predictionDate(LocalDate.now())
                .predictionTime(now)
                .positiveCount(positiveCount)
                .negativeCount(negativeCount)
                .neutralCount(neutralCount)
                .positiveRatio(Math.round(positiveRatio * 100) / 100.0)
                .predictionLabel(predictionLabel)
                .priceAtPrediction(currentPrice)
                .build();

        CryptoPrediction savedPrediction = savePredictionPort.savePrediction(prediction);
        log.info("Prediction generated for {}: label={}, positiveRatio={}, newsCount={}",
                ticker, predictionLabel, positiveRatio, positiveCount + negativeCount + neutralCount);

        return savedPrediction;
    }

    @Override
    public List<CryptoPrediction> generateAllPredictions() {
        log.info("Generating predictions for all cryptos");
        List<Crypto> allCryptos = cryptoRepository.findAll();
        List<CryptoPrediction> predictions = new ArrayList<>();

        for (Crypto crypto : allCryptos) {
            try {
                CryptoPrediction prediction = generatePrediction(crypto.getTicker());
                if (prediction != null) {
                    predictions.add(prediction);
                }
            } catch (Exception e) {
                log.error("Failed to generate prediction for {}: {}", crypto.getTicker(), e.getMessage());
            }
        }

        log.info("Generated {} predictions for {} cryptos", predictions.size(), allCryptos.size());
        return predictions;
    }
}
