package com.coanalysis.server.news.adapter.in;

import com.coanalysis.server.infrastructure.exception.CustomException;
import com.coanalysis.server.infrastructure.exception.ErrorCode;
import com.coanalysis.server.infrastructure.response.ApiResponse;
import com.coanalysis.server.news.adapter.in.dto.SearchNewsResponse;
import com.coanalysis.server.news.adapter.in.swagger.SearchNewsControllerSwagger;
import com.coanalysis.server.news.adapter.out.SearchNewsQuery;
import com.coanalysis.server.news.application.domain.News;
import com.coanalysis.server.news.application.port.in.SearchNewsUseCase;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
public class SearchNewsController implements SearchNewsControllerSwagger {

    private final SearchNewsUseCase searchNewsUseCase;
    private final SearchNewsQuery searchNewsQuery;

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SearchNewsResponse>> searchById(@PathParam(value = "id") Long id) {
        if (ObjectUtils.isEmpty(id)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "뉴스 ID가 필요합니다.");
        }

        News news = searchNewsUseCase.searchById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NEWS_NOT_FOUND));

        return ResponseEntity.ok(ApiResponse.success(SearchNewsResponse.of(news)));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<SearchNewsResponse>>> searchNewsList() {
        List<SearchNewsResponse> newsList = searchNewsQuery.searchAllNews();
        return ResponseEntity.ok(ApiResponse.success(newsList));
    }
}
