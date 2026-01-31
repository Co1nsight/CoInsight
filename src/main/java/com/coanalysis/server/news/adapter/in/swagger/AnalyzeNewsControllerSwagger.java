package com.coanalysis.server.news.adapter.in.swagger;

import com.coanalysis.server.infrastructure.response.ApiResponse;
import com.coanalysis.server.news.adapter.in.dto.AnalyzeNewsRequest;
import com.coanalysis.server.news.adapter.in.dto.AnalyzeNewsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "News Analysis", description = "뉴스 감성 분석 API")
public interface AnalyzeNewsControllerSwagger {

    @Operation(summary = "뉴스 ID로 감성 분석", description = "저장된 뉴스 ID를 기반으로 감성 분석을 수행합니다.")
    ResponseEntity<ApiResponse<AnalyzeNewsResponse>> analyzeById(
            @Parameter(name = "newsId", description = "뉴스 ID", required = true) Long newsId,
            @Parameter(name = "X-HUGGINGFACE-API-TOKEN", description = "HuggingFace API 토큰 (선택)") String apiToken);

    @Operation(summary = "텍스트 감성 분석", description = "입력된 텍스트(제목, 내용)를 기반으로 감성 분석을 수행합니다.")
    ResponseEntity<ApiResponse<AnalyzeNewsResponse>> analyzeText(
            AnalyzeNewsRequest request,
            @Parameter(name = "X-HUGGINGFACE-API-TOKEN", description = "HuggingFace API 토큰 (선택)") String apiToken);
}