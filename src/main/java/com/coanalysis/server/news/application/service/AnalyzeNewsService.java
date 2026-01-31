package com.coanalysis.server.news.application.service;

import com.coanalysis.server.news.application.domain.News;
import com.coanalysis.server.news.application.domain.NewsAnalysis;
import com.coanalysis.server.news.application.port.in.AnalyzeNewsUseCase;
import com.coanalysis.server.news.application.port.out.AnalyzingNewsPort;
import com.coanalysis.server.news.application.port.out.SearchNewsPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalyzeNewsService implements AnalyzeNewsUseCase {

    private final AnalyzingNewsPort analyzingNewsPort;
    private final SearchNewsPort searchNewsPort;

    @Override
    public NewsAnalysis analyzeNews(Long newsId) {
        News news = searchNewsPort.searchById(newsId)
                .orElseThrow(() -> new IllegalArgumentException("News not found: " + newsId));

        return analyzingNewsPort.analyzeContent(news.getTitle(), news.getContent());
    }

    @Override
    public NewsAnalysis analyzeText(String title, String content) {
        return analyzingNewsPort.analyzeContent(title, content);
    }
}