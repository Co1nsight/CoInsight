package com.coanalysis.server.news.application.port.in;

import com.coanalysis.server.news.application.domain.News;

import java.util.Optional;

public interface SearchNewsUseCase {

    Optional<News> searchById(Long id);

}
