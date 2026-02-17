package com.coanalysis.server.prediction.application.enums;

public enum PredictionLabel {
    UP("상승"),
    DOWN("하락"),
    NEUTRAL("중립");

    private final String description;

    PredictionLabel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
