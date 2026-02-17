package com.coanalysis.server.news.adapter.in;

import com.coanalysis.server.infrastructure.exception.CustomException;
import com.coanalysis.server.infrastructure.exception.ErrorCode;
import com.coanalysis.server.infrastructure.response.ApiResponse;
import com.coanalysis.server.infrastructure.response.PageResponse;
import com.coanalysis.server.news.adapter.in.dto.NewsDetailResponse;
import com.coanalysis.server.news.adapter.in.dto.NewsWithAnalysisResponse;
import com.coanalysis.server.news.adapter.in.dto.SearchNewsResponse;
import com.coanalysis.server.news.adapter.in.swagger.SearchNewsControllerSwagger;
import com.coanalysis.server.news.adapter.out.SearchNewsQuery;
import com.coanalysis.server.news.application.domain.News;
import com.coanalysis.server.news.application.port.in.SearchNewsUseCase;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
public class SearchNewsController implements SearchNewsControllerSwagger {

    private final SearchNewsUseCase searchNewsUseCase;
    private final SearchNewsQuery searchNewsQuery;

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SearchNewsResponse>> searchById(@PathVariable("id") Long id) {
        if (ObjectUtils.isEmpty(id)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "뉴스 ID가 필요합니다.");
        }

        News news = searchNewsUseCase.searchById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NEWS_NOT_FOUND));

        return ResponseEntity.ok(ApiResponse.success(SearchNewsResponse.of(news)));
    }

    @Override
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<PageResponse<SearchNewsResponse>>> searchNewsList(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);

        PageResponse<SearchNewsResponse> newsList = searchNewsQuery.searchAllNews(safePage, safeSize);
        return ResponseEntity.ok(ApiResponse.success(newsList));
    }

    @Override
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<SearchNewsResponse>>> searchByKeyword(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);

        PageResponse<SearchNewsResponse> newsList = searchNewsQuery.searchByKeyword(keyword, safePage, safeSize);
        return ResponseEntity.ok(ApiResponse.success(newsList));
    }

    @Override
    @GetMapping("/analysis")
    public ResponseEntity<ApiResponse<PageResponse<NewsWithAnalysisResponse>>> getNewsWithAnalysis(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);

        PageResponse<NewsWithAnalysisResponse> newsList = searchNewsQuery.findNewsWithAnalysis(safePage, safeSize);
        return ResponseEntity.ok(ApiResponse.success(newsList));
    }

    @Override
    @GetMapping("/{id}/detail")
    public ResponseEntity<ApiResponse<NewsDetailResponse>> getNewsDetail(@PathVariable("id") Long id) {
        if (ObjectUtils.isEmpty(id)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "뉴스 ID가 필요합니다.");
        }

        NewsDetailResponse newsDetail = searchNewsQuery.findNewsDetailById(id);
        if (newsDetail == null) {
            throw new CustomException(ErrorCode.NEWS_NOT_FOUND);
        }

        return ResponseEntity.ok(ApiResponse.success(newsDetail));
    }

}
