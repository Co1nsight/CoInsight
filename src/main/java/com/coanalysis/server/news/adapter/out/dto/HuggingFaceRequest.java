package com.coanalysis.server.news.adapter.out.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HuggingFaceRequest {

    private String inputs;
    private Parameters parameters;

    public static HuggingFaceRequest of(String text) {
        return HuggingFaceRequest.builder()
                .inputs(text)
                .parameters(Parameters.withTruncation())
                .build();
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Parameters {
        private Boolean truncation;

        public static Parameters withTruncation() {
            return Parameters.builder()
                    .truncation(true)
                    .build();
        }
    }
}