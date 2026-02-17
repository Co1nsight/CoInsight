package com.coanalysis.server.batch.application.domain;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.coanalysis.server.news.application.enums.Language;

public record CollectedNews(
    String externalId,
    String title,
    String body,
    String originalLink,
    String source,
    LocalDateTime publishedAt,
    Set<String> categories,
    Set<String> tags,
    Language language
) {

    public static CollectedNews fromApiResponse(String id, String title, String url, String body,
            String source, Long publishedOn, String categories, String tags, Language language) {
        return new CollectedNews(
            id,
            title,
            body,
            url,
            source,
            LocalDateTime.ofInstant(Instant.ofEpochSecond(publishedOn), ZoneId.systemDefault()),
            parseDelimitedString(categories),
            parseDelimitedString(tags),
            language
        );
    }

    public static CollectedNews fromRssItem(String id, String title, String url, String body,
            String source, LocalDateTime publishedAt, Language language) {
        return new CollectedNews(
            id,
            title,
            body,
            url,
            source,
            publishedAt,
            Set.of(),
            Set.of(),
            language
        );
    }

    private static Set<String> parseDelimitedString(String value) {
        if (value == null || value.isBlank()) {
            return Set.of();
        }
        return Arrays.stream(value.split("\\|"))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toSet());
    }
}
