package com.coanalysis.server.infrastructure.repository;

import com.coanalysis.server.news.application.domain.NewsAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsAnalysisRepository extends JpaRepository<NewsAnalysis, Long> {
}
