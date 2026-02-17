package com.coanalysis.server.batch.adapter.out;

import com.coanalysis.server.batch.application.domain.CollectedNews;
import com.coanalysis.server.batch.application.port.out.FindDuplicateNewsPort;
import com.coanalysis.server.batch.application.port.out.SaveCollectedNewsPort;
import com.coanalysis.server.infrastructure.repository.NewsRepository;
import com.coanalysis.server.news.application.domain.News;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsCollectionAdapter implements SaveCollectedNewsPort, FindDuplicateNewsPort {

    private final NewsRepository newsRepository;

    @Override
    @Transactional
    public News save(CollectedNews collectedNews) {
        News news = toNewsEntity(collectedNews);
        return newsRepository.save(news);
    }

    @Override
    @Transactional
    public List<News> saveAll(List<CollectedNews> collectedNewsList) {
        List<News> newsEntities = collectedNewsList.stream()
                .map(this::toNewsEntity)
                .collect(Collectors.toList());
        return newsRepository.saveAll(newsEntities);
    }

    @Override
    public boolean existsByOriginalLink(String originalLink) {
        return newsRepository.existsByOriginalLink(originalLink);
    }

    @Override
    public Set<String> findExistingLinks(Set<String> links) {
        if (links == null || links.isEmpty()) {
            return Set.of();
        }
        return newsRepository.findByOriginalLinkIn(links).stream()
                .map(News::getOriginalLink)
                .collect(Collectors.toSet());
    }

    private News toNewsEntity(CollectedNews collected) {
        return News.builder()
                .title(collected.title())
                .originalLink(collected.originalLink())
                .publisher(collected.source())
                .publishedAt(collected.publishedAt())
                .content(collected.body())
                .language(collected.language())
                .build();
    }
}
