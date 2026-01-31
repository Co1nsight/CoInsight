package com.coanalysis.server.news.adapter.out;

import com.coanalysis.server.infrastructure.repository.NewsRepository;
import com.coanalysis.server.infrastructure.response.PageResponse;
import com.coanalysis.server.news.adapter.in.dto.NewsDetailResponse;
import com.coanalysis.server.news.adapter.in.dto.NewsWithAnalysisResponse;
import com.coanalysis.server.news.adapter.in.dto.SearchNewsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SearchNewsQuery {

    private final NewsRepository repository;

    public PageResponse<SearchNewsResponse> searchAllNews(int page, int size) {
        List<SearchNewsResponse> content = repository.searchAllNews(page, size);
        long totalElements = repository.countAllNews();
        return PageResponse.of(content, page, size, totalElements);
    }

    public PageResponse<SearchNewsResponse> searchByKeyword(String keyword, int page, int size) {
        List<SearchNewsResponse> content = repository.searchByKeyword(keyword, page, size);
        long totalElements = repository.countByKeyword(keyword);
        return PageResponse.of(content, page, size, totalElements);
    }

    public PageResponse<NewsWithAnalysisResponse> findNewsWithAnalysis(int page, int size) {
        List<NewsWithAnalysisResponse> newsList = repository.findNewsWithAnalysis(page, size);

        // 각 뉴스에 관련 코인 정보 추가
        newsList.forEach(news -> {
            List<NewsWithAnalysisResponse.RelatedCrypto> relatedCryptos =
                    repository.findRelatedCryptosByNewsId(news.getId());
            news.setRelatedCryptos(relatedCryptos);
        });

        long totalElements = repository.countNewsWithAnalysis();
        return PageResponse.of(newsList, page, size, totalElements);
    }

    public NewsDetailResponse findNewsDetailById(Long newsId) {
        // 뉴스 기본 정보 조회
        NewsDetailResponse newsDetail = repository.findNewsDetailById(newsId);
        if (newsDetail == null) {
            return null;
        }

        // 분석 결과 조회
        NewsDetailResponse.AnalysisResult analysis = repository.findAnalysisByNewsId(newsId);

        // 관련 코인 조회
        List<NewsDetailResponse.RelatedCrypto> relatedCryptos = repository.findDetailRelatedCryptosByNewsId(newsId);

        return NewsDetailResponse.builder()
                .id(newsDetail.getId())
                .title(newsDetail.getTitle())
                .content(newsDetail.getContent())
                .publisher(newsDetail.getPublisher())
                .publishedAt(newsDetail.getPublishedAt())
                .originalLink(newsDetail.getOriginalLink())
                .analysis(analysis)
                .relatedCryptos(relatedCryptos)
                .build();
    }

}
