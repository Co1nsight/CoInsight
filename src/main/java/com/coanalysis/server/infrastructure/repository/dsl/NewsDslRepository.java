package com.coanalysis.server.infrastructure.repository.dsl;

import com.coanalysis.server.news.adapter.in.dto.SearchNewsResponse;

import java.util.List;

public interface NewsDslRepository {

    List<SearchNewsResponse> searchAllNews();

}
