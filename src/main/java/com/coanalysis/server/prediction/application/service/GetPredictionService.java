package com.coanalysis.server.prediction.application.service;

import com.coanalysis.server.infrastructure.response.PageResponse;
import com.coanalysis.server.prediction.adapter.in.dto.PredictionHistoryResponse;
import com.coanalysis.server.prediction.adapter.in.dto.PredictionNewsResponse;
import com.coanalysis.server.prediction.adapter.in.dto.PredictionStatsResponse;
import com.coanalysis.server.prediction.adapter.in.dto.RecentNewsResponse;
import com.coanalysis.server.prediction.application.port.in.GetPredictionUseCase;
import com.coanalysis.server.prediction.application.port.out.LoadPredictionPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetPredictionService implements GetPredictionUseCase {

    private final LoadPredictionPort loadPredictionPort;

    @Override
    public PageResponse<PredictionHistoryResponse> getPredictionHistory(String ticker, int page, int size) {
        log.info("Getting prediction history for ticker: {}, page: {}, size: {}", ticker, page, size);
        return loadPredictionPort.loadPredictionHistory(ticker.toUpperCase(), page, size);
    }

    @Override
    public List<PredictionNewsResponse> getPredictionNews(String ticker, Long predictionId) {
        log.info("Getting prediction news for ticker: {}, predictionId: {}", ticker, predictionId);
        return loadPredictionPort.loadPredictionNews(predictionId);
    }

    @Override
    public PredictionStatsResponse getPredictionStats(String ticker) {
        log.info("Getting prediction stats for ticker: {}", ticker);
        return loadPredictionPort.loadPredictionStats(ticker.toUpperCase());
    }

    @Override
    public PageResponse<RecentNewsResponse> getRecentNews(String ticker, int page, int size) {
        log.info("Getting recent news for ticker: {}, page: {}, size: {}", ticker, page, size);
        LocalDateTime to = LocalDateTime.now();
        LocalDateTime from = to.minusHours(24);
        return loadPredictionPort.loadRecentNews(ticker.toUpperCase(), from, to, page, size);
    }
}
