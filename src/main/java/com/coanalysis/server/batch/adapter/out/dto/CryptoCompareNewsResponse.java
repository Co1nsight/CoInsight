package com.coanalysis.server.batch.adapter.out.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class CryptoCompareNewsResponse {
    @JsonProperty("Data")
    private List<CryptoCompareNewsItem> data;

    public List<CryptoCompareNewsItem> getDataAsList() {
        if (data == null) return List.of();
        return List.copyOf(data);
    }
}
