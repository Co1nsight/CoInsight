package com.coanalysis.server.prediction.application.service;

import com.coanalysis.server.crypto.application.domain.Crypto;
import com.coanalysis.server.infrastructure.repository.CryptoRepository;
import com.coanalysis.server.infrastructure.repository.CryptoPredictionRepository;
import com.coanalysis.server.infrastructure.repository.NewsRepository;
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
    private final NewsRepository newsRepository;
    private final CryptoPredictionRepository cryptoPredictionRepository;

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
        LocalDateTime now = LocalDateTime.now();

        // 해당 코인의 마지막 예측 시간 조회
        LocalDateTime lastPredictionTime = cryptoPredictionRepository.findLastPredictionTimeByTicker(ticker.toUpperCase());

        // 마지막 예측 이후의 뉴스만 조회 (기사 중복 사용 방지)
        Map<String, Integer> sentimentCounts;
        if (lastPredictionTime != null) {
            sentimentCounts = loadNewsAnalysisPort.countUnusedNewsBySentiment(ticker, lastPredictionTime, now);
            log.debug("Using news after last prediction at {} for {}", lastPredictionTime, ticker);
        } else {
            LocalDateTime from = now.minusHours(24);
            sentimentCounts = loadNewsAnalysisPort.countNewsBySentiment(ticker, from, now);
            log.debug("First prediction for {}, using last 24h news", ticker);
        }

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

        // 1. 모든 코인의 가격을 한 번에 조회 (rate limit 방지)
        List<String> tickers = allCryptos.stream()
                .map(Crypto::getTicker)
                .toList();
        Map<String, Double> priceMap = fetchCryptoPricePort.fetchAllPrices(tickers);
        log.info("Fetched prices for {} cryptos", priceMap.size());

        // 2. 각 코인에 대해 예측 생성 (이미 조회한 가격 사용)
        List<CryptoPrediction> predictions = new ArrayList<>();
        for (Crypto crypto : allCryptos) {
            try {
                Double price = priceMap.get(crypto.getTicker());
                CryptoPrediction prediction = generatePredictionWithPrice(crypto, price);
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

    private CryptoPrediction generatePredictionWithPrice(Crypto crypto, Double currentPrice) {
        String ticker = crypto.getTicker();
        LocalDateTime now = LocalDateTime.now();

        // 해당 코인의 마지막 예측 시간 조회
        LocalDateTime lastPredictionTime = cryptoPredictionRepository.findLastPredictionTimeByTicker(ticker);

        // 마지막 예측 이후의 뉴스만 조회 (기사 중복 사용 방지)
        // 첫 예측인 경우 최근 24시간 뉴스 사용
        Map<String, Integer> sentimentCounts;
        if (lastPredictionTime != null) {
            sentimentCounts = loadNewsAnalysisPort.countUnusedNewsBySentiment(ticker, lastPredictionTime, now);
            log.debug("Using news after last prediction at {} for {}", lastPredictionTime, ticker);
        } else {
            LocalDateTime from = now.minusHours(24);
            sentimentCounts = loadNewsAnalysisPort.countNewsBySentiment(ticker, from, now);
            log.debug("First prediction for {}, using last 24h news", ticker);
        }

        int positiveCount = sentimentCounts.getOrDefault("positive", 0);
        int negativeCount = sentimentCounts.getOrDefault("negative", 0);
        int neutralCount = sentimentCounts.getOrDefault("neutral", 0);
        int totalNonNeutral = positiveCount + negativeCount;

        // 긍정 비율 계산 (중립 제외)
        double positiveRatio = totalNonNeutral > 0
                ? (double) positiveCount / totalNonNeutral
                : 0.5;

        // 예측 라벨 결정
        PredictionLabel predictionLabel;
        if (positiveRatio > POSITIVE_THRESHOLD) {
            predictionLabel = PredictionLabel.UP;
        } else if (positiveRatio < NEGATIVE_THRESHOLD) {
            predictionLabel = PredictionLabel.DOWN;
        } else {
            predictionLabel = PredictionLabel.NEUTRAL;
        }

        // 가격이 없으면 0으로 설정
        if (currentPrice == null) {
            log.warn("Price not available for ticker: {}", ticker);
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
        log.debug("Prediction generated for {}: label={}, positiveRatio={}, newsCount={}",
                ticker, predictionLabel, positiveRatio, positiveCount + negativeCount + neutralCount);

        return savedPrediction;
    }

    @Override
    @Transactional(readOnly = true)
    public long countRecentNews(int hoursAgo) {
        LocalDateTime from = LocalDateTime.now().minusHours(hoursAgo);
        return newsRepository.countNewsPublishedSince(from);
    }
}
