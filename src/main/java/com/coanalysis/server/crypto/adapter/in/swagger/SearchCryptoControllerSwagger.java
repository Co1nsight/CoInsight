package com.coanalysis.server.crypto.adapter.in.swagger;

import com.coanalysis.server.crypto.adapter.in.dto.SearchCryptoResponse;
import com.coanalysis.server.infrastructure.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Crypto Search", description = "암호화폐 검색 API")
public interface SearchCryptoControllerSwagger {

    @Operation(summary = "키워드로 코인 검색", description = "키워드를 기반으로 암호화폐를 검색합니다.")
    ResponseEntity<ApiResponse<List<SearchCryptoResponse>>> searchByKeyword(
            @Parameter(name = "keyword", description = "검색 키워드", required = true) String keyword);

    @Operation(summary = "코인 상세 조회", description = "코인 ID를 기반으로 암호화폐 상세 정보를 조회합니다.")
    ResponseEntity<ApiResponse<SearchCryptoResponse>> findById(
            @Parameter(name = "id", description = "코인 ID", required = true) Long id);
}