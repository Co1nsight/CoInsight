package com.coanalysis.server.prediction.adapter.out;

import com.coanalysis.server.infrastructure.repository.CryptoPredictionRepository;
import com.coanalysis.server.infrastructure.repository.PredictionVerificationRepository;
import com.coanalysis.server.infrastructure.repository.dsl.CryptoPredictionDslRepository;
import com.coanalysis.server.infrastructure.response.PageResponse;
import com.coanalysis.server.prediction.adapter.in.dto.PredictionHistoryResponse;
import com.coanalysis.server.prediction.adapter.in.dto.PredictionNewsResponse;
import com.coanalysis.server.prediction.adapter.in.dto.PredictionStatsResponse;
import com.coanalysis.server.prediction.adapter.in.dto.RecentNewsResponse;
import com.coanalysis.server.prediction.application.domain.CryptoPrediction;
import com.coanalysis.server.prediction.application.enums.IntervalType;
import com.coanalysis.server.prediction.application.port.out.LoadPredictionPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PredictionQueryAdapter implements LoadPredictionPort {

    private final CryptoPredictionRepository predictionRepository;
    private final PredictionVerificationRepository verificationRepository;
    private final CryptoPredictionDslRepository predictionDslRepository;

    @Override
    public PageResponse<PredictionHistoryResponse> loadPredictionHistory(String ticker, int page, int size) {
        List<PredictionHistoryResponse> predictions = predictionDslRepository.findPredictionHistory(ticker, page, size);

        // 각 예측에 대한 검증 결과 로드
        for (PredictionHistoryResponse prediction : predictions) {
            List<PredictionHistoryResponse.VerificationResult> verifications =
                    predictionDslRepository.findVerificationResults(prediction.getPredictionId());
            prediction = PredictionHistoryResponse.builder()
                    .predictionId(prediction.getPredictionId())
                    .predictionDate(prediction.getPredictionDate())
                    .predictionTime(prediction.getPredictionTime())
                    .predictionLabel(prediction.getPredictionLabel())
                    .positiveRatio(prediction.getPositiveRatio())
                    .positiveCount(prediction.getPositiveCount())
                    .negativeCount(prediction.getNegativeCount())
                    .neutralCount(prediction.getNeutralCount())
                    .priceAtPrediction(prediction.getPriceAtPrediction())
                    .verifications(verifications)
                    .build();
        }

        // 검증 결과를 포함한 새로운 리스트 생성
        List<PredictionHistoryResponse> enrichedPredictions = new ArrayList<>();
        for (PredictionHistoryResponse prediction : predictions) {
            List<PredictionHistoryResponse.VerificationResult> verifications =
                    predictionDslRepository.findVerificationResults(prediction.getPredictionId());
            enrichedPredictions.add(PredictionHistoryResponse.builder()
                    .predictionId(prediction.getPredictionId())
                    .predictionDate(prediction.getPredictionDate())
                    .predictionTime(prediction.getPredictionTime())
                    .predictionLabel(prediction.getPredictionLabel())
                    .positiveRatio(prediction.getPositiveRatio())
                    .positiveCount(prediction.getPositiveCount())
                    .negativeCount(prediction.getNegativeCount())
                    .neutralCount(prediction.getNeutralCount())
                    .priceAtPrediction(prediction.getPriceAtPrediction())
                    .verifications(verifications)
                    .build());
        }

        long totalElements = predictionDslRepository.countPredictionHistory(ticker);
        return PageResponse.of(enrichedPredictions, page, size, totalElements);
    }

    @Override
    public List<PredictionNewsResponse> loadPredictionNews(Long predictionId) {
        return predictionDslRepository.findPredictionNews(predictionId);
    }

    @Override
    public PredictionStatsResponse loadPredictionStats(String ticker) {
        long totalPredictions = predictionRepository.countByTicker(ticker.toUpperCase());
        long totalVerifications = verificationRepository.countByTicker(ticker.toUpperCase());
        long totalSuccesses = verificationRepository.countSuccessByTicker(ticker.toUpperCase());

        double overallSuccessRate = totalVerifications > 0
                ? (double) totalSuccesses / totalVerifications * 100
                : 0.0;

        List<PredictionStatsResponse.IntervalStats> intervalStats = new ArrayList<>();
        for (IntervalType intervalType : IntervalType.values()) {
            long verificationCount = verificationRepository.countByTickerAndIntervalType(
                    ticker.toUpperCase(), intervalType);
            long successCount = verificationRepository.countSuccessByTickerAndIntervalType(
                    ticker.toUpperCase(), intervalType);
            double successRate = verificationCount > 0
                    ? (double) successCount / verificationCount * 100
                    : 0.0;

            intervalStats.add(PredictionStatsResponse.IntervalStats.builder()
                    .intervalType(intervalType.name())
                    .description(intervalType.getDescription())
                    .verificationCount(verificationCount)
                    .successCount(successCount)
                    .successRate(Math.round(successRate * 10) / 10.0)
                    .build());
        }

        return PredictionStatsResponse.builder()
                .ticker(ticker.toUpperCase())
                .totalPredictions(totalPredictions)
                .totalVerifications(totalVerifications)
                .totalSuccesses(totalSuccesses)
                .overallSuccessRate(Math.round(overallSuccessRate * 10) / 10.0)
                .intervalStats(intervalStats)
                .build();
    }

    @Override
    public PageResponse<RecentNewsResponse> loadRecentNews(String ticker, LocalDateTime from, LocalDateTime to,
                                                            int page, int size) {
        List<RecentNewsResponse> news = predictionDslRepository.findRecentNews(ticker, from, to, page, size);
        long totalElements = predictionDslRepository.countRecentNews(ticker, from, to);
        return PageResponse.of(news, page, size, totalElements);
    }

    @Override
    public List<CryptoPrediction> loadUnverifiedPredictions(LocalDateTime targetTime, IntervalType intervalType) {
        return predictionRepository.findUnverifiedPredictions(targetTime, intervalType);
    }

    @Override
    public Optional<CryptoPrediction> loadPredictionById(Long id) {
        return predictionRepository.findById(id);
    }

    @Override
    public boolean isVerificationExists(Long predictionId, IntervalType intervalType) {
        return verificationRepository.existsByPredictionIdAndIntervalType(predictionId, intervalType);
    }
}
