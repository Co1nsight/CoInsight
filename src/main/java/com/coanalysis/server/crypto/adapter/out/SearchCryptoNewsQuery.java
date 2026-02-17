package com.coanalysis.server.crypto.adapter.out;

import com.coanalysis.server.infrastructure.repository.NewsRepository;
import com.coanalysis.server.infrastructure.response.PageResponse;
import com.coanalysis.server.news.adapter.in.dto.SearchNewsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SearchCryptoNewsQuery {

    private final NewsRepository newsRepository;

    public PageResponse<SearchNewsResponse> findNewsByTicker(String ticker, int page, int size) {
        List<SearchNewsResponse> content = newsRepository.findNewsByTicker(ticker, page, size);
        long totalElements = newsRepository.countNewsByTicker(ticker);
        return PageResponse.of(content, page, size, totalElements);
    }
}
