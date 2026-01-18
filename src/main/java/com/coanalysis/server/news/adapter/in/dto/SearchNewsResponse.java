package com.coanalysis.server.news.adapter.in.dto;

import com.coanalysis.server.news.application.domain.News;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SearchNewsResponse {

    private Long id;

    public static SearchNewsResponse of(News news) {
        return SearchNewsResponse.builder()
                .id(news.getId())
                .build();
    }

}
