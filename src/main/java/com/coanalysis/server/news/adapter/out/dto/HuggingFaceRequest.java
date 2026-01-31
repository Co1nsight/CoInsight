package com.coanalysis.server.news.adapter.out.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HuggingFaceRequest {

    private String inputs;

    public static HuggingFaceRequest of(String text) {
        return new HuggingFaceRequest(text);
    }
}