package com.coanalysis.server.news.adapter.in;

import com.coanalysis.server.infrastructure.exception.CustomException;
import com.coanalysis.server.infrastructure.exception.ErrorCode;
import com.coanalysis.server.infrastructure.response.ApiResponse;
import com.coanalysis.server.news.adapter.in.dto.SearchNewsResponse;
import com.coanalysis.server.news.adapter.in.swagger.SearchNewsControllerSwagger;
import com.coanalysis.server.news.adapter.out.SearchNewsQuery;
import com.coanalysis.server.news.application.domain.News;
import com.coanalysis.server.news.application.port.in.SearchNewsUseCase;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<SearchNewsResponse>>> searchNewsList() {
        List<SearchNewsResponse> newsList = searchNewsQuery.searchAllNews();

        return ResponseEntity.ok(ApiResponse.success(newsList));
    } //바로 out으로  단순 페이지 쿼리로 꼽는 애들이니까 서치뉴스쿼리를 따로 만들어서 바로 꽂아버림
    //usecase가 dto를 거치면 안됨 response 객체가 service를 거치면 안됨

    @GetMapping("/search")
    public ResponseEntity<List<SearchNewsResponse>> searchByKeyword(@RequestParam String keyword) {
        List<SearchNewsResponse> newsList = searchNewsQuery.searchByKeyword(keyword);
        return ResponseEntity.ok(newsList);
    }

}
