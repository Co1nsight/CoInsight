package com.coanalysis.server.market.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CandleType {
    MINUTES("minutes", "분봉"),
    DAYS("days", "일봉"),
    WEEKS("weeks", "주봉"),
    MONTHS("months", "월봉");

    private final String apiPath;
    private final String description;
}
