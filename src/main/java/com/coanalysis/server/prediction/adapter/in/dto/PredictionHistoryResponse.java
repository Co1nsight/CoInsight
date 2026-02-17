package com.coanalysis.server.prediction.adapter.in.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "예측 히스토리 응답")
public class PredictionHistoryResponse {

    @Schema(description = "예측 ID", example = "1")
    private Long predictionId;

    @Schema(description = "예측 날짜", example = "2024-01-15")
    private LocalDate predictionDate;

    @Schema(description = "예측 시각", example = "2024-01-15T09:00:00")
    private LocalDateTime predictionTime;

    @Schema(description = "AI 예측 결과", example = "UP")
    private String predictionLabel;

    @Schema(description = "긍정 비율 (0~1)", example = "0.65")
    private Double positiveRatio;

    @Schema(description = "긍정 뉴스 수", example = "15")
    private Integer positiveCount;

    @Schema(description = "부정 뉴스 수", example = "8")
    private Integer negativeCount;

    @Schema(description = "중립 뉴스 수", example = "5")
    private Integer neutralCount;

    @Schema(description = "예측 시점 가격", example = "65000000.0")
    private Double priceAtPrediction;

    @Schema(description = "간격별 검증 결과 목록")
    private List<VerificationResult> verifications;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "검증 결과")
    public static class VerificationResult {

        @Schema(description = "검증 간격", example = "HOUR_1")
        private String intervalType;

        @Schema(description = "검증 시점 가격", example = "65500000.0")
        private Double priceAtVerification;

        @Schema(description = "가격 변동률 (%)", example = "0.77")
        private Double priceChangePercent;

        @Schema(description = "예측 성공 여부", example = "true")
        private Boolean isSuccess;

        @Schema(description = "검증 시각", example = "2024-01-15T10:00:00")
        private LocalDateTime verifiedAt;
    }

    public int getTotalNewsCount() {
        return positiveCount + negativeCount + neutralCount;
    }
}
