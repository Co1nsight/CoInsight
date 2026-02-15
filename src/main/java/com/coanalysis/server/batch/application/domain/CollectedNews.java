package com.coanalysis.server.batch.application.domain;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record CollectedNews(
    String externalId,
    String title,
    String body,
    String originalLink,
    String source,
    LocalDateTime publishedAt,
    Set<String> categories,
    Set<String> tags
) {
    private static final Map<String, String> KEYWORD_TO_TICKER = Map.ofEntries(
        Map.entry("bitcoin", "BTC"),
        Map.entry("btc", "BTC"),
        Map.entry("ethereum", "ETH"),
        Map.entry("eth", "ETH"),
        Map.entry("ripple", "XRP"),
        Map.entry("xrp", "XRP"),
        Map.entry("solana", "SOL"),
        Map.entry("sol", "SOL"),
        Map.entry("cardano", "ADA"),
        Map.entry("ada", "ADA"),
        Map.entry("dogecoin", "DOGE"),
        Map.entry("doge", "DOGE"),
        Map.entry("avalanche", "AVAX"),
        Map.entry("avax", "AVAX"),
        Map.entry("polygon", "MATIC"),
        Map.entry("matic", "MATIC")
    );

    public Set<String> extractCoinTickers(Set<String> knownTickers) {
        Set<String> matched = new HashSet<>();

        // 1. categories에서 직접 매칭
        for (String category : categories) {
            String upper = category.toUpperCase();
            if (knownTickers.contains(upper)) {
                matched.add(upper);
            }
        }

        // 2. title/body에서 키워드 매칭
        String searchText = (title + " " + (body != null ? body : "")).toLowerCase();
        for (Map.Entry<String, String> entry : KEYWORD_TO_TICKER.entrySet()) {
            if (searchText.contains(entry.getKey()) && knownTickers.contains(entry.getValue())) {
                matched.add(entry.getValue());
            }
        }

        return matched;
    }

    public static CollectedNews fromApiResponse(String id, String title, String url, String body,
            String source, Long publishedOn, String categories, String tags) {
        return new CollectedNews(
            id,
            title,
            body,
            url,
            source,
            LocalDateTime.ofInstant(Instant.ofEpochSecond(publishedOn), ZoneId.systemDefault()),
            parseDelimitedString(categories),
            parseDelimitedString(tags)
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
