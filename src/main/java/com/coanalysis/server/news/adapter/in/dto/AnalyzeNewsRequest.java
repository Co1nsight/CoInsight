package com.coanalysis.server.news.adapter.in.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "뉴스 감성 분석 요청")
public class AnalyzeNewsRequest {

    @Schema(description = "뉴스 제목", example = "비트코인 사상 최고가 경신")
    private String title;

    @Schema(description = "뉴스 내용", example = "비트코인이 10만 달러를 돌파하며 사상 최고가를 기록했다.")
    private String content;
}