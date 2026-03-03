package com.coanalysis.server.batch.adapter.out;

import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ArticleContentScraper {

    private static final int TIMEOUT_MS = 5000;
    private static final int DELAY_MS = 100;

    private static final Set<String> RSS_SOURCES = Set.of("TokenPost", "DigitalToday");

    private static final Map<String, String> SOURCE_SELECTORS = Map.of(
            "TokenPost", ".article_content p",
            "DigitalToday", "#article-view-content-div"
    );

    // 제거할 불필요 요소 셀렉터
    private static final String REMOVE_SELECTORS = "script, style, iframe, .ad, .advertisement, .banner, .social-share, .related-articles";

    public boolean isRssSource(String source) {
        return RSS_SOURCES.contains(source);
    }

    public String scrapeContent(String url, String source) {
        String selector = SOURCE_SELECTORS.get(source);
        if (selector == null) {
            return null;
        }

        try {
            Thread.sleep(DELAY_MS);

            Document doc = Jsoup.connect(url)
                    .timeout(TIMEOUT_MS)
                    .get();

            // 불필요 요소 제거
            doc.select(REMOVE_SELECTORS).remove();

            Elements elements = doc.select(selector);
            if (elements.isEmpty()) {
                log.warn("No content found with selector '{}' for URL: {}", selector, url);
                return null;
            }

            String content = extractText(elements);
            if (content.isBlank()) {
                return null;
            }

            log.info("Scraped full content for {} ({}chars): {}", source, content.length(), url);
            return content;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Scraping interrupted for URL: {}", url);
            return null;
        } catch (Exception e) {
            log.warn("Failed to scrape content from {}: {}", url, e.getMessage());
            return null;
        }
    }

    private String extractText(Elements elements) {
        StringBuilder sb = new StringBuilder();
        for (Element el : elements) {
            String text = el.text().trim();
            if (!text.isEmpty()) {
                if (sb.length() > 0) {
                    sb.append(" ");
                }
                sb.append(text);
            }
        }
        return sb.toString().trim();
    }
}
