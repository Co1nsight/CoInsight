package com.coanalysis.server.news.adapter.out;

import com.coanalysis.server.infrastructure.repository.NewsRepository;
import com.coanalysis.server.news.adapter.in.dto.SearchNewsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SearchNewsQuery {

    private final NewsRepository repository;

    public List<SearchNewsResponse> searchAllNews() {
        return repository.searchAllNews();
    }

}
