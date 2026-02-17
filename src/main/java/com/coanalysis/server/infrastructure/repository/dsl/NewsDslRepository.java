package com.coanalysis.server.infrastructure.repository.dsl;

import com.coanalysis.server.news.adapter.in.dto.NewsDetailResponse;
import com.coanalysis.server.news.adapter.in.dto.NewsWithAnalysisResponse;
import com.coanalysis.server.news.adapter.in.dto.SearchNewsResponse;

import java.util.List;

public interface NewsDslRepository {

    List<SearchNewsResponse> searchAllNews(int page, int size);

    long countAllNews();

    List<SearchNewsResponse> searchByKeyword(String keyword, int page, int size);

    long countByKeyword(String keyword);

    List<NewsWithAnalysisResponse> findNewsWithAnalysis(int page, int size);

    long countNewsWithAnalysis();

    List<NewsWithAnalysisResponse.RelatedCrypto> findRelatedCryptosByNewsId(Long newsId);

    NewsDetailResponse findNewsDetailById(Long newsId);

    NewsDetailResponse.AnalysisResult findAnalysisByNewsId(Long newsId);

    List<NewsDetailResponse.RelatedCrypto> findDetailRelatedCryptosByNewsId(Long newsId);

    List<SearchNewsResponse> findNewsByTicker(String ticker, int page, int size);

    long countNewsByTicker(String ticker);

}
