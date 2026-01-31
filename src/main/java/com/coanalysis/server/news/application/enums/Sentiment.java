package com.coanalysis.server.news.application.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Sentiment {

    POSITIVE("positive", "긍정"),
    NEUTRAL("neutral", "중립"),
    NEGATIVE("negative", "부정");

    private final String label;
    private final String koreanLabel;

    public static Sentiment fromLabel(String label) {
        for (Sentiment sentiment : values()) {
            if (sentiment.label.equalsIgnoreCase(label)) {
                return sentiment;
            }
        }
        return NEUTRAL;
    }
}