package com.coanalysis.server.crypto.adapter.in.swagger;
import com.coanalysis.server.crypto.adapter.in.dto.SearchCryptoResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SearchCryptoControllerSwagger {

    @Schema(name = "키워드로 코인 조회")
    @Parameters({
            @Parameter(name = "keyword")
    })
    ResponseEntity<List<SearchCryptoResponse>> searchByKeyword(String keyword);

    @Schema(name = "코인 상세 조회")
    @Parameters({
            @Parameter(name = "id")
    })
    ResponseEntity<SearchCryptoResponse> findById(Long id);
}