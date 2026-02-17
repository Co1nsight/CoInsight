package com.coanalysis.server.prediction.application.enums;

public enum IntervalType {
    HOUR_1(1, "1시간"),
    HOUR_3(3, "3시간"),
    HOUR_12(12, "12시간"),
    HOUR_24(24, "24시간");

    private final int hours;
    private final String description;

    IntervalType(int hours, String description) {
        this.hours = hours;
        this.description = description;
    }

    public int getHours() {
        return hours;
    }

    public String getDescription() {
        return description;
    }
}
