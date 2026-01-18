package com.coanalysis.server.news.application.service;

import com.coanalysis.server.news.application.domain.News;
import com.coanalysis.server.news.application.port.in.SearchNewsUseCase;
import com.coanalysis.server.news.application.port.out.SearchNewsPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SearchNewsService implements SearchNewsUseCase {

    private final SearchNewsPort searchNewsPort;

    @Override
    public Optional<News> searchById(Long id) {
        return searchNewsPort.searchById(id);
    }

}
