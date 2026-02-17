package com.coanalysis.server.infrastructure.repository.dsl;

import com.coanalysis.server.prediction.adapter.in.dto.PredictionHistoryResponse;
import com.coanalysis.server.prediction.adapter.in.dto.PredictionNewsResponse;
import com.coanalysis.server.prediction.adapter.in.dto.RecentNewsResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface CryptoPredictionDslRepository {

    List<PredictionHistoryResponse> findPredictionHistory(String ticker, int page, int size);

    long countPredictionHistory(String ticker);

    List<PredictionNewsResponse> findPredictionNews(Long predictionId);

    List<RecentNewsResponse> findRecentNews(String ticker, LocalDateTime from, LocalDateTime to, int page, int size);

    long countRecentNews(String ticker, LocalDateTime from, LocalDateTime to);

    List<PredictionHistoryResponse.VerificationResult> findVerificationResults(Long predictionId);
}
