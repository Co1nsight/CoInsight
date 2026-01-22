package com.coanalysis.server.news.adapter.in.swagger;

import com.coanalysis.server.news.adapter.in.dto.SearchNewsResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;

public interface SearchNewsControllerSwagger {

    @Schema(name = "아이디로 뉴스 조회")
    @Parameters({
            @Parameter(name = "id"),
    })
    ResponseEntity<SearchNewsResponse> searchById(Long id);


}
