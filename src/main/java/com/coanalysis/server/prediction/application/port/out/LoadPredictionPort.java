package com.coanalysis.server.prediction.application.port.out;

import com.coanalysis.server.infrastructure.response.PageResponse;
import com.coanalysis.server.prediction.adapter.in.dto.PredictionHistoryResponse;
import com.coanalysis.server.prediction.adapter.in.dto.PredictionNewsResponse;
import com.coanalysis.server.prediction.adapter.in.dto.PredictionStatsResponse;
import com.coanalysis.server.prediction.adapter.in.dto.RecentNewsResponse;
import com.coanalysis.server.prediction.application.domain.CryptoPrediction;
import com.coanalysis.server.prediction.application.enums.IntervalType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LoadPredictionPort {

    PageResponse<PredictionHistoryResponse> loadPredictionHistory(String ticker, int page, int size);

    List<PredictionNewsResponse> loadPredictionNews(Long predictionId);

    PredictionStatsResponse loadPredictionStats(String ticker);

    PageResponse<RecentNewsResponse> loadRecentNews(String ticker, LocalDateTime from, LocalDateTime to, int page, int size);

    List<CryptoPrediction> loadUnverifiedPredictions(LocalDateTime targetTime, IntervalType intervalType);

    Optional<CryptoPrediction> loadPredictionById(Long id);

    boolean isVerificationExists(Long predictionId, IntervalType intervalType);
}
