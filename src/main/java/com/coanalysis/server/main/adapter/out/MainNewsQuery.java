package com.coanalysis.server.main.adapter.out;

import com.coanalysis.server.infrastructure.repository.NewsRepository;
import com.coanalysis.server.infrastructure.response.PageResponse;
import com.coanalysis.server.main.adapter.in.dto.MainNewsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MainNewsQuery {

    private final NewsRepository newsRepository;

    public PageResponse<MainNewsResponse> findMainNews(int page, int size) {
        List<MainNewsResponse> newsList = newsRepository.findMainNews(page, size);

        // 각 뉴스에 관련 코인 정보 추가
        newsList.forEach(news -> {
            List<MainNewsResponse.RelatedCrypto> relatedCryptos =
                    newsRepository.findRelatedCryptosForMainNews(news.getId());
            news.setRelatedCryptos(relatedCryptos);
        });

        long totalElements = newsRepository.countMainNews();
        return PageResponse.of(newsList, page, size, totalElements);
    }
}
