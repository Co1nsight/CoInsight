package com.coanalysis.server.prediction.application.port.in;

import com.coanalysis.server.infrastructure.response.PageResponse;
import com.coanalysis.server.prediction.adapter.in.dto.PredictionHistoryResponse;
import com.coanalysis.server.prediction.adapter.in.dto.PredictionNewsResponse;
import com.coanalysis.server.prediction.adapter.in.dto.PredictionStatsResponse;
import com.coanalysis.server.prediction.adapter.in.dto.RecentNewsResponse;

import java.util.List;

public interface GetPredictionUseCase {

    PageResponse<PredictionHistoryResponse> getPredictionHistory(String ticker, int page, int size);

    List<PredictionNewsResponse> getPredictionNews(String ticker, Long predictionId);

    PredictionStatsResponse getPredictionStats(String ticker);

    PageResponse<RecentNewsResponse> getRecentNews(String ticker, int page, int size);
}
