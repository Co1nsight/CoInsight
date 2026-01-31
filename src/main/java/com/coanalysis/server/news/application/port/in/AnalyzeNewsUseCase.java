package com.coanalysis.server.news.application.port.in;

import com.coanalysis.server.news.application.domain.NewsAnalysis;

public interface AnalyzeNewsUseCase {

    NewsAnalysis analyzeNews(Long newsId);

    NewsAnalysis analyzeText(String title, String content);
}