package com.coanalysis.server.prediction.adapter.in.swagger;

import com.coanalysis.server.infrastructure.response.ApiResponse;
import com.coanalysis.server.infrastructure.response.PageResponse;
import com.coanalysis.server.prediction.adapter.in.dto.PredictionHistoryResponse;
import com.coanalysis.server.prediction.adapter.in.dto.PredictionNewsResponse;
import com.coanalysis.server.prediction.adapter.in.dto.PredictionStatsResponse;
import com.coanalysis.server.prediction.adapter.in.dto.RecentNewsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Prediction", description = "코인 가격 예측 API")
public interface PredictionControllerSwagger {

    @Operation(summary = "예측 히스토리 조회", description = "특정 코인의 예측 히스토리를 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))
            )
    })
    ApiResponse<PageResponse<PredictionHistoryResponse>> getPredictionHistory(
            @Parameter(description = "코인 티커", example = "BTC") @PathVariable String ticker,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") int size
    );

    @Operation(summary = "예측에 사용된 뉴스 목록 조회", description = "특정 예측에 사용된 뉴스 목록을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = List.class))
            )
    })
    ApiResponse<List<PredictionNewsResponse>> getPredictionNews(
            @Parameter(description = "코인 티커", example = "BTC") @PathVariable String ticker,
            @Parameter(description = "예측 ID", example = "1") @PathVariable Long predictionId
    );

    @Operation(summary = "누적 예측 통계 조회", description = "특정 코인의 누적 예측 통계를 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = PredictionStatsResponse.class))
            )
    })
    ApiResponse<PredictionStatsResponse> getPredictionStats(
            @Parameter(description = "코인 티커", example = "BTC") @PathVariable String ticker
    );

    @Operation(summary = "최근 24시간 뉴스 조회", description = "특정 코인의 최근 24시간 뉴스를 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))
            )
    })
    ApiResponse<PageResponse<RecentNewsResponse>> getRecentNews(
            @Parameter(description = "코인 티커", example = "BTC") @PathVariable String ticker,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") int size
    );
}
