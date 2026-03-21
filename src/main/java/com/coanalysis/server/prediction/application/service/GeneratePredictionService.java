package com.coanalysis.server.prediction.application.service;

import com.coanalysis.server.crypto.application.domain.Crypto;
import com.coanalysis.server.infrastructure.repository.CryptoRepository;
import com.coanalysis.server.infrastructure.repository.CryptoPredictionRepository;
import com.coanalysis.server.infrastructure.repository.NewsRepository;
import com.coanalysis.server.prediction.application.domain.CryptoPrediction;
import com.coanalysis.server.prediction.application.dto.NewsSignalItem;
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
import java.time.temporal.ChronoUnit;
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
    private static final int MIN_POLAR_NEWS_COUNT = 3;
    private static final double TIME_DECAY_LAMBDA = 0.1;
    private static final double SENTIMENT_WEIGHT = 0.7;
    private static final double MOMENTUM_WEIGHT = 0.3;

    @Override
    public CryptoPrediction generatePrediction(String ticker) {
        log.info("Generating prediction for ticker: {}", ticker);

        Optional<Crypto> cryptoOpt = cryptoRepository.findById(ticker.toUpperCase());
        if (cryptoOpt.isEmpty()) {
            log.warn("Crypto not found for ticker: {}", ticker);
            return null;
        }

        Double currentPrice = fetchCryptoPricePort.fetchCurrentPrice(ticker);
        return buildPrediction(cryptoOpt.get(), currentPrice);
    }

    @Override
    public List<CryptoPrediction> generateAllPredictions() {
        log.info("Generating predictions for all cryptos");
        List<Crypto> allCryptos = cryptoRepository.findAll();

        List<String> tickers = allCryptos.stream().map(Crypto::getTicker).toList();
        Map<String, Double> priceMap = fetchCryptoPricePort.fetchAllPrices(tickers);
        log.info("Fetched prices for {} cryptos", priceMap.size());

        List<CryptoPrediction> predictions = new ArrayList<>();
        for (Crypto crypto : allCryptos) {
            try {
                Double price = priceMap.get(crypto.getTicker());
                CryptoPrediction prediction = buildPrediction(crypto, price);
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

    @Override
    @Transactional(readOnly = true)
    public long countRecentNews(int hoursAgo) {
        LocalDateTime from = LocalDateTime.now().minusHours(hoursAgo);
        return newsRepository.countNewsPublishedSince(from);
    }

    /**
     * 예측 생성의 핵심 로직.
     * 1) 뉴스 신호 로드 (점수 가중합 + 시간 가중치)
     * 2) 가격 모멘텀 결합
     * 3) 임계값 기반 라벨 결정 후 저장
     */
    private CryptoPrediction buildPrediction(Crypto crypto, Double currentPrice) {
        String ticker = crypto.getTicker();
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime lastPredictionTime = cryptoPredictionRepository.findLastPredictionTimeByTicker(ticker);

        List<NewsSignalItem> signals;
        if (lastPredictionTime != null) {
            signals = loadNewsAnalysisPort.loadUnusedNewsSignals(ticker, lastPredictionTime, now);
        } else {
            signals = loadNewsAnalysisPort.loadNewsSignals(ticker, now.minusHours(24), now);
        }

        int positiveCount = (int) signals.stream()
                .filter(s -> s.getSentimentLabel() != null && s.getSentimentLabel().toLowerCase().contains("positive"))
                .count();
        int negativeCount = (int) signals.stream()
                .filter(s -> s.getSentimentLabel() != null && s.getSentimentLabel().toLowerCase().contains("negative"))
                .count();
        int neutralCount = (int) signals.stream()
                .filter(s -> s.getSentimentLabel() == null || s.getSentimentLabel().toLowerCase().contains("neutral"))
                .count();
        int totalPolarCount = positiveCount + negativeCount;

        if (totalPolarCount < MIN_POLAR_NEWS_COUNT) {
            log.info("Insufficient polar news for {}: polarCount={} (min={})", ticker, totalPolarCount, MIN_POLAR_NEWS_COUNT);
            return null;
        }

        double positiveWeightedScore = computeWeightedScore(signals, "positive", now);
        double negativeWeightedScore = computeWeightedScore(signals, "negative", now);
        double totalWeightedScore = positiveWeightedScore + negativeWeightedScore;

        double sentimentRatio = totalWeightedScore > 0
                ? positiveWeightedScore / totalWeightedScore
                : 0.5;

        double combinedRatio = combineWithPriceMomentum(sentimentRatio, ticker, currentPrice);

        PredictionLabel predictionLabel;
        if (combinedRatio > POSITIVE_THRESHOLD) {
            predictionLabel = PredictionLabel.UP;
        } else if (combinedRatio < NEGATIVE_THRESHOLD) {
            predictionLabel = PredictionLabel.DOWN;
        } else {
            predictionLabel = PredictionLabel.NEUTRAL;
        }

        if (predictionLabel == PredictionLabel.NEUTRAL) {
            log.info("Skipping neutral prediction for {}: combinedRatio={}, polarCount={}", ticker, combinedRatio, totalPolarCount);
            return null;
        }

        if (currentPrice == null) {
            log.warn("Price not available for ticker: {}", ticker);
            currentPrice = 0.0;
        }

        CryptoPrediction prediction = CryptoPrediction.builder()
                .crypto(crypto)
                .predictionDate(LocalDate.now())
                .predictionTime(now)
                .positiveCount(positiveCount)
                .negativeCount(negativeCount)
                .neutralCount(neutralCount)
                .positiveRatio(Math.round(combinedRatio * 1000) / 1000.0)
                .predictionLabel(predictionLabel)
                .priceAtPrediction(currentPrice)
                .build();

        CryptoPrediction saved = savePredictionPort.savePrediction(prediction);
        log.info("Prediction generated for {}: label={}, combinedRatio={}, sentimentRatio={}, polarCount={}",
                ticker, predictionLabel, combinedRatio, sentimentRatio, totalPolarCount);
        return saved;
    }

    /**
     * 지수 감쇠 시간 가중치를 적용한 감성 점수 합산.
     * 최신 기사일수록 더 큰 가중치를 받는다.
     */
    private double computeWeightedScore(List<NewsSignalItem> signals, String labelType, LocalDateTime now) {
        return signals.stream()
                .filter(s -> s.getSentimentLabel() != null
                        && s.getSentimentLabel().toLowerCase().contains(labelType)
                        && s.getSentimentScore() != null
                        && s.getPublishedAt() != null)
                .mapToDouble(s -> {
                    double hoursAgo = ChronoUnit.MINUTES.between(s.getPublishedAt(), now) / 60.0;
                    double timeWeight = Math.exp(-TIME_DECAY_LAMBDA * hoursAgo);
                    return s.getSentimentScore() * timeWeight;
                })
                .sum();
    }

    /**
     * 센티먼트 비율(70%)과 가격 모멘텀(30%)을 결합.
     * 이전 예측 이후 가격 변동을 tanh로 정규화하여 [0,1] 범위로 변환.
     */
    private double combineWithPriceMomentum(double sentimentRatio, String ticker, Double currentPrice) {
        Optional<Double> lastPriceOpt = cryptoPredictionRepository.findLastPriceAtPredictionByTicker(ticker);

        if (lastPriceOpt.isEmpty() || lastPriceOpt.get() == null || lastPriceOpt.get() == 0
                || currentPrice == null || currentPrice == 0) {
            log.debug("No price momentum data for {}, using sentiment only", ticker);
            return sentimentRatio;
        }

        double lastPrice = lastPriceOpt.get();
        double rawMomentum = (currentPrice - lastPrice) / lastPrice;
        // tanh로 정규화: ±10% 변동 → tanh(±1) ≈ ±0.76 → [0,1] 범위로 변환
        double normalizedMomentum = (Math.tanh(rawMomentum * 10) + 1) / 2.0;

        double combined = sentimentRatio * SENTIMENT_WEIGHT + normalizedMomentum * MOMENTUM_WEIGHT;
        log.debug("Price momentum for {}: rawMomentum={}%, normalizedMomentum={}, combined={}",
                ticker, rawMomentum * 100, normalizedMomentum, combined);
        return combined;
    }
}
