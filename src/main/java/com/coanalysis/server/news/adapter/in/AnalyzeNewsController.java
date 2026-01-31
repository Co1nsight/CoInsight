package com.coanalysis.server.news.adapter.in;

import com.coanalysis.server.news.adapter.in.dto.AnalyzeNewsRequest;
import com.coanalysis.server.news.adapter.in.dto.AnalyzeNewsResponse;
import com.coanalysis.server.news.adapter.in.swagger.AnalyzeNewsControllerSwagger;
import com.coanalysis.server.news.adapter.out.ApiTokenHolder;
import com.coanalysis.server.news.application.domain.NewsAnalysis;
import com.coanalysis.server.news.application.port.in.AnalyzeNewsUseCase;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/news/analyze")
@RequiredArgsConstructor
public class AnalyzeNewsController implements AnalyzeNewsControllerSwagger {

    private final AnalyzeNewsUseCase analyzeNewsUseCase;
    private final ApiTokenHolder apiTokenHolder;

    @Override
    @GetMapping("/{newsId}")
    public ResponseEntity<AnalyzeNewsResponse> analyzeById(
            @PathVariable Long newsId,
            @RequestHeader(value = "X-HUGGINGFACE-API-TOKEN", required = false) String apiToken) {
        try {
            apiTokenHolder.setToken(apiToken);
            NewsAnalysis analysis = analyzeNewsUseCase.analyzeNews(newsId);
            return ResponseEntity.ok(AnalyzeNewsResponse.of(analysis));
        } finally {
            apiTokenHolder.clear();
        }
    }

    @Override
    @PostMapping
    public ResponseEntity<AnalyzeNewsResponse> analyzeText(
            @RequestBody AnalyzeNewsRequest request,
            @RequestHeader(value = "X-HUGGINGFACE-API-TOKEN", required = false) String apiToken) {
        try {
            apiTokenHolder.setToken(apiToken);
            NewsAnalysis analysis = analyzeNewsUseCase.analyzeText(request.getTitle(), request.getContent());
            return ResponseEntity.ok(AnalyzeNewsResponse.of(analysis));
        } finally {
            apiTokenHolder.clear();
        }
    }
}