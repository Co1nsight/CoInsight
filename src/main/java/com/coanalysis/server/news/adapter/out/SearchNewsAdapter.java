package com.coanalysis.server.news.adapter.out;

import com.coanalysis.server.infrastructure.repository.NewsRepository;
import com.coanalysis.server.news.application.domain.News;
import com.coanalysis.server.news.application.port.out.SearchNewsPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SearchNewsAdapter implements SearchNewsPort {

    private final NewsRepository repository;

    @Override
    public Optional<News> searchById(Long id) {
        return repository.findById(id);
    }

}
