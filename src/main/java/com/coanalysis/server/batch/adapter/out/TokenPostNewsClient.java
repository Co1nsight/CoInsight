package com.coanalysis.server.batch.adapter.out;

import com.coanalysis.server.batch.application.domain.CollectedNews;
import com.coanalysis.server.batch.application.port.out.FetchCryptoNewsPort;
import com.coanalysis.server.news.application.enums.Language;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenPostNewsClient implements FetchCryptoNewsPort {

    private final String rssUrl;
    private final int maxRetries;

    public TokenPostNewsClient(
            @Value("${tokenpost.rss.url:https://www.tokenpost.kr/rss}") String rssUrl,
            @Value("${tokenpost.rss.max-retries:3}") int maxRetries) {
        this.rssUrl = rssUrl;
        this.maxRetries = maxRetries;
    }

    @Override
    public List<CollectedNews> fetchLatestNews() {
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                log.info("Fetching Korean crypto news from TokenPost (attempt {}/{})", attempt, maxRetries);

                SyndFeedInput input = new SyndFeedInput();
                SyndFeed feed = input.build(new XmlReader(new URL(rssUrl)));

                List<CollectedNews> news = feed.getEntries().stream()
                        .map(this::toCollectedNews)
                        .collect(Collectors.toList());

                log.info("Successfully fetched {} Korean news articles from TokenPost", news.size());
                return news;

            } catch (Exception e) {
                log.warn("Failed to fetch Korean news (attempt {}/{}): {}", attempt, maxRetries, e.getMessage());
                if (attempt == maxRetries) {
                    log.error("All retry attempts exhausted for TokenPost RSS", e);
                    return Collections.emptyList();
                }
                try {
                    Thread.sleep(1000L * attempt);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return Collections.emptyList();
                }
            }
        }
        return Collections.emptyList();
    }

    private CollectedNews toCollectedNews(SyndEntry entry) {
        String id = entry.getUri() != null ? entry.getUri() : UUID.randomUUID().toString();
        String title = entry.getTitle() != null ? entry.getTitle() : "";
        String link = entry.getLink() != null ? entry.getLink() : "";
        String description = entry.getDescription() != null ? entry.getDescription().getValue() : "";

        LocalDateTime publishedAt = entry.getPublishedDate() != null
                ? entry.getPublishedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                : LocalDateTime.now();

        return CollectedNews.fromRssItem(
                id,
                cleanHtml(title),
                link,
                cleanHtml(description),
                "TokenPost",
                publishedAt,
                Language.KO
        );
    }

    private String cleanHtml(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }
        return text.replaceAll("<[^>]*>", "").trim();
    }
}
