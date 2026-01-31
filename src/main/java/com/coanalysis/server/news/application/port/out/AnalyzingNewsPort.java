package com.coanalysis.server.news.application.port.out;

import com.coanalysis.server.news.application.domain.NewsAnalysis;

public interface AnalyzingNewsPort {

	NewsAnalysis analyzeContent(String title, String content);

}
