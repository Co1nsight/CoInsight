package com.coanalysis.server.news.adapter.out.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HuggingFaceSentimentResult {

    private String label;
    private Double score;
}