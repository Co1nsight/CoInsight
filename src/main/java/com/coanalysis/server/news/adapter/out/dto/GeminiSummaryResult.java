package com.coanalysis.server.news.adapter.out.dto;

import java.util.Set;

public record GeminiSummaryResult(String summary, Set<String> tickers) {
}
