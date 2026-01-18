package com.coanalysis.server.infrastructure.repository;

import com.coanalysis.server.infrastructure.repository.dsl.NewsDslRepository;
import com.coanalysis.server.news.application.domain.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News, Long>, NewsDslRepository {
}
