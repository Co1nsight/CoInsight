package com.coanalysis.server.news.application.enums;

public enum Language {
    EN("English"),
    KO("Korean");

    private final String displayName;

    Language(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
