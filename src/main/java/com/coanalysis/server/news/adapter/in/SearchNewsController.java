package com.coanalysis.server.news.adapter.in;

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
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
public class SearchNewsController implements SearchNewsControllerSwagger {

    private final SearchNewsUseCase searchNewsUseCase;

    private final SearchNewsQuery searchNewsQuery;

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<SearchNewsResponse> searchById(@PathParam(value = "id") Long id) {

        if (ObjectUtils.isEmpty(id)) {
            return ResponseEntity.ok(null);
        }

        Optional<News> newsIf = searchNewsUseCase.searchById(id);

        if (newsIf.isEmpty()) {
            return ResponseEntity.ok(null);
        }

        return ResponseEntity.ok(SearchNewsResponse.of(newsIf.get()));
    } //내부통과해서

    @GetMapping("/list")
    public ResponseEntity<List<SearchNewsResponse>> searchNewsList() {

        List<SearchNewsResponse> newsList = searchNewsQuery.searchAllNews();

        return ResponseEntity.ok(newsList);
    } //바로 out으로  단순 페이지 쿼리로 꼽는 애들이니까 서치뉴스쿼리를 따로 만들어서 바로 꽂아버림
    //usecase가 dto를 거치면 안됨 response 객체가 service를 거치면 안됨

}
