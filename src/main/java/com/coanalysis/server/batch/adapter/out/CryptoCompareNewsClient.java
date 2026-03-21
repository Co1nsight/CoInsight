package com.coanalysis.server.batch.adapter.out;

import com.coanalysis.server.batch.adapter.out.dto.CryptoCompareNewsItem;
import com.coanalysis.server.batch.adapter.out.dto.CryptoCompareNewsResponse;
import com.coanalysis.server.batch.application.domain.CollectedNews;
import com.coanalysis.server.batch.application.port.out.FetchCryptoNewsPort;
import com.coanalysis.server.news.application.enums.Language;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CryptoCompareNewsClient implements FetchCryptoNewsPort {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final int maxRetries;

    public CryptoCompareNewsClient(
            @Value("${cryptocompare.api.base-url:https://min-api.cryptocompare.com}") String baseUrl,
            @Value("${cryptocompare.api.max-retries:3}") int maxRetries) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = baseUrl;
        this.maxRetries = maxRetries;
    }

    @Override
    public List<CollectedNews> fetchLatestNews() {
        String url = baseUrl + "/data/v2/news/?lang=EN";

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                log.info("Fetching crypto news from CryptoCompare (attempt {}/{})", attempt, maxRetries);

                CryptoCompareNewsResponse response = restTemplate.getForObject(url, CryptoCompareNewsResponse.class);

                if (response == null || response.getData() == null) {
                    log.warn("Empty response from CryptoCompare API");
                    return Collections.emptyList();
                }

                List<CollectedNews> news = response.getDataAsList().stream()
                        .map(this::toCollectedNews)
                        .collect(Collectors.toList());

                log.info("Successfully fetched {} news articles", news.size());
                return news;

            } catch (RestClientException e) {
                log.warn("Failed to fetch news (attempt {}/{}): {}", attempt, maxRetries, e.getMessage());
                if (attempt == maxRetries) {
                    log.error("All retry attempts exhausted for CryptoCompare API", e);
                    return Collections.emptyList();
                }
                try {
                    Thread.sleep(1000L * attempt); // Exponential backoff
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return Collections.emptyList();
                }
            }
        }
        return Collections.emptyList();
    }

    private CollectedNews toCollectedNews(CryptoCompareNewsItem item) {
        return CollectedNews.fromApiResponse(
                item.getId(),
                item.getTitle(),
                item.getUrl(),
                item.getBody(),
                item.getSource(),
                item.getPublishedOn(),
                item.getCategories(),
                item.getTags(),
                Language.EN
        );
    }
}
