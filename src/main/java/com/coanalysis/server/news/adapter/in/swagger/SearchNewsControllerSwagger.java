package com.coanalysis.server.news.adapter.in.swagger;

import com.coanalysis.server.infrastructure.response.ApiResponse;
import com.coanalysis.server.news.adapter.in.dto.SearchNewsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "News Search", description = "뉴스 검색 API")
public interface SearchNewsControllerSwagger {

    @Operation(summary = "뉴스 ID로 조회", description = "뉴스 ID를 기반으로 뉴스를 조회합니다.")
    ResponseEntity<ApiResponse<SearchNewsResponse>> searchById(
            @Parameter(name = "id", description = "뉴스 ID", required = true) Long id);
}
