package com.coanalysis.server.infrastructure.repository;

import com.coanalysis.server.infrastructure.repository.dsl.NewsDslRepository;
import com.coanalysis.server.news.application.domain.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface NewsRepository extends JpaRepository<News, Long>, NewsDslRepository {

    boolean existsByOriginalLink(String originalLink);

    List<News> findByOriginalLinkIn(Set<String> originalLinks);

    @Query("SELECT COUNT(n) FROM News n WHERE n.publishedAt >= :from")
    long countNewsPublishedSince(@Param("from") LocalDateTime from);
}
