package com.coanalysis.server.prediction.adapter.in.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "예측 통계 응답")
public class PredictionStatsResponse {

    @Schema(description = "코인 티커", example = "BTC")
    private String ticker;

    @Schema(description = "총 예측 수", example = "100")
    private Long totalPredictions;

    @Schema(description = "총 검증 수", example = "380")
    private Long totalVerifications;

    @Schema(description = "총 성공 수", example = "250")
    private Long totalSuccesses;

    @Schema(description = "전체 성공률 (%)", example = "65.8")
    private Double overallSuccessRate;

    @Schema(description = "간격별 통계 목록")
    private List<IntervalStats> intervalStats;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "간격별 통계")
    public static class IntervalStats {

        @Schema(description = "검증 간격", example = "HOUR_1")
        private String intervalType;

        @Schema(description = "간격 설명", example = "1시간")
        private String description;

        @Schema(description = "검증 수", example = "95")
        private Long verificationCount;

        @Schema(description = "성공 수", example = "62")
        private Long successCount;

        @Schema(description = "성공률 (%)", example = "65.3")
        private Double successRate;
    }
}
