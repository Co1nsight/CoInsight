package com.coanalysis.server.prediction.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NewsSignalItem {
    private final String sentimentLabel;
    private final Double sentimentScore;
    private final LocalDateTime publishedAt;
}
