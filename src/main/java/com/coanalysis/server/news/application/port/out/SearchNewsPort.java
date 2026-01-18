package com.coanalysis.server.news.application.port.out;

import com.coanalysis.server.news.application.domain.News;

import java.util.Optional;

public interface SearchNewsPort {

    Optional<News> searchById(Long id);

}
