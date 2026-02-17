package com.coanalysis.server.batch.adapter.out.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CryptoCompareNewsItem {
    private String id;
    private String title;
    private String url;
    private String body;
    private String source;

    @JsonProperty("published_on")
    private Long publishedOn;

    private String categories;  // "BTC|ETH|Market"
    private String tags;        // "Bitcoin|Ethereum"
}
