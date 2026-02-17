package com.coanalysis.server.prediction.adapter.in;

import com.coanalysis.server.infrastructure.response.ApiResponse;
import com.coanalysis.server.infrastructure.response.PageResponse;
import com.coanalysis.server.prediction.adapter.in.dto.PredictionHistoryResponse;
import com.coanalysis.server.prediction.adapter.in.dto.PredictionNewsResponse;
import com.coanalysis.server.prediction.adapter.in.dto.PredictionStatsResponse;
import com.coanalysis.server.prediction.adapter.in.dto.RecentNewsResponse;
import com.coanalysis.server.prediction.adapter.in.swagger.PredictionControllerSwagger;
import com.coanalysis.server.prediction.application.domain.CryptoPrediction;
import com.coanalysis.server.prediction.application.port.in.GeneratePredictionUseCase;
import com.coanalysis.server.prediction.application.port.in.GetPredictionUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/crypto")
public class PredictionController implements PredictionControllerSwagger {

    private final GetPredictionUseCase getPredictionUseCase;
    private final GeneratePredictionUseCase generatePredictionUseCase;

    @Override
    @GetMapping("/{ticker}/predictions")
    public ApiResponse<PageResponse<PredictionHistoryResponse>> getPredictionHistory(
            @PathVariable String ticker,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/v1/crypto/{}/predictions - page: {}, size: {}", ticker, page, size);
        PageResponse<PredictionHistoryResponse> response = getPredictionUseCase.getPredictionHistory(ticker, page, size);
        return ApiResponse.success(response);
    }

    @Override
    @GetMapping("/{ticker}/predictions/{predictionId}/news")
    public ApiResponse<List<PredictionNewsResponse>> getPredictionNews(
            @PathVariable String ticker,
            @PathVariable Long predictionId) {
        log.info("GET /api/v1/crypto/{}/predictions/{}/news", ticker, predictionId);
        List<PredictionNewsResponse> response = getPredictionUseCase.getPredictionNews(ticker, predictionId);
        return ApiResponse.success(response);
    }

    @Override
    @GetMapping("/{ticker}/predictions/stats")
    public ApiResponse<PredictionStatsResponse> getPredictionStats(@PathVariable String ticker) {
        log.info("GET /api/v1/crypto/{}/predictions/stats", ticker);
        PredictionStatsResponse response = getPredictionUseCase.getPredictionStats(ticker);
        return ApiResponse.success(response);
    }

    @Override
    @GetMapping("/{ticker}/news/recent")
    public ApiResponse<PageResponse<RecentNewsResponse>> getRecentNews(
            @PathVariable String ticker,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/v1/crypto/{}/news/recent - page: {}, size: {}", ticker, page, size);
        PageResponse<RecentNewsResponse> response = getPredictionUseCase.getRecentNews(ticker, page, size);
        return ApiResponse.success(response);
    }

    // 테스트용 수동 예측 생성 엔드포인트
    @PostMapping("/{ticker}/predictions")
    public ApiResponse<String> generatePrediction(@PathVariable String ticker) {
        log.info("POST /api/v1/crypto/{}/predictions - manual prediction generation", ticker);
        CryptoPrediction prediction = generatePredictionUseCase.generatePrediction(ticker);
        if (prediction != null) {
            return ApiResponse.success("Prediction generated: " + prediction.getPredictionLabel());
        }
        return ApiResponse.success("No prediction generated");
    }
}
