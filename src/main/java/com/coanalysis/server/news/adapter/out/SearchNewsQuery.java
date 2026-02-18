package com.coanalysis.server.news.adapter.out;

import com.coanalysis.server.infrastructure.repository.NewsRepository;
import com.coanalysis.server.infrastructure.repository.PredictionVerificationRepository;
import com.coanalysis.server.infrastructure.response.PageResponse;
import com.coanalysis.server.news.adapter.in.dto.NewsDetailResponse;
import com.coanalysis.server.news.adapter.in.dto.NewsWithAnalysisResponse;
import com.coanalysis.server.news.adapter.in.dto.SearchNewsResponse;
import com.coanalysis.server.prediction.application.domain.PredictionVerification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SearchNewsQuery {

    private final NewsRepository repository;
    private final PredictionVerificationRepository verificationRepository;

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

        // 각 관련 코인에 대해 예측 검증 결과 (실제 가격 변동) 추가
        LocalDateTime publishedAt = newsDetail.getPublishedAt();
        List<NewsDetailResponse.RelatedCrypto> cryptosWithPriceChange = relatedCryptos.stream()
                .map(crypto -> addPriceChangeInfo(crypto, publishedAt))
                .toList();

        return NewsDetailResponse.builder()
                .id(newsDetail.getId())
                .title(newsDetail.getTitle())
                .content(newsDetail.getContent())
                .publisher(newsDetail.getPublisher())
                .publishedAt(newsDetail.getPublishedAt())
                .originalLink(newsDetail.getOriginalLink())
                .language(newsDetail.getLanguage())
                .analysis(analysis)
                .relatedCryptos(cryptosWithPriceChange)
                .build();
    }

    /**
     * 관련 코인에 예측 검증 결과 기반 가격 변동 정보를 추가합니다.
     */
    private NewsDetailResponse.RelatedCrypto addPriceChangeInfo(
            NewsDetailResponse.RelatedCrypto crypto,
            LocalDateTime newsPublishedAt) {

        // 기사 발행 시점 이후의 가장 가까운 예측 검증 결과 조회
        PredictionVerification verification = verificationRepository
                .findFirstVerificationAfterTime(crypto.getTicker(), newsPublishedAt);

        NewsDetailResponse.PriceChange priceChange;
        if (verification == null) {
            // 검증 결과가 없는 경우
            priceChange = NewsDetailResponse.PriceChange.builder()
                    .available(false)
                    .unavailableReason("아직 검증된 예측이 없습니다")
                    .build();
        } else {
            // 검증 결과가 있는 경우
            var prediction = verification.getPrediction();
            String actualDirection = verification.getPriceChangePercent() >= 0 ? "UP" : "DOWN";

            priceChange = NewsDetailResponse.PriceChange.builder()
                    .available(true)
                    .priceAtPrediction(prediction.getPriceAtPrediction())
                    .priceAtVerification(verification.getPriceAtVerification())
                    .changePercent(Math.round(verification.getPriceChangePercent() * 100.0) / 100.0)
                    .actualDirection(actualDirection)
                    .predictedDirection(prediction.getPredictionLabel().name())
                    .predictionSuccess(verification.getIsSuccess())
                    .intervalType(verification.getIntervalType().name())
                    .predictionTime(prediction.getPredictionTime())
                    .verifiedAt(verification.getVerifiedAt())
                    .build();
        }

        return NewsDetailResponse.RelatedCrypto.builder()
                .ticker(crypto.getTicker())
                .name(crypto.getName())
                .logoUrl(crypto.getLogoUrl())
                .priceChange(priceChange)
                .build();
    }

}
