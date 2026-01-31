package com.coanalysis.server.news.adapter.in.swagger;

import com.coanalysis.server.infrastructure.response.ApiResponse;
import com.coanalysis.server.infrastructure.response.PageResponse;
import com.coanalysis.server.news.adapter.in.dto.NewsDetailResponse;
import com.coanalysis.server.news.adapter.in.dto.NewsWithAnalysisResponse;
import com.coanalysis.server.news.adapter.in.dto.SearchNewsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "News Search", description = "뉴스 검색 API - 암호화폐 관련 뉴스 조회 기능을 제공합니다.")
public interface SearchNewsControllerSwagger {

    @Operation(
            summary = "뉴스 ID로 조회",
            description = """
                    뉴스 ID를 기반으로 특정 뉴스를 조회합니다.

                    - 뉴스의 제목, 발행사, 발행일시 등 상세 정보를 제공합니다.
                    - 감성 분석 결과가 있는 경우 함께 반환됩니다.
                    - 존재하지 않는 ID로 조회 시 404 에러가 반환됩니다.
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "조회 성공 예시",
                                    value = """
                                            {
                                                "success": true,
                                                "data": {
                                                    "id": 1,
                                                    "title": "비트코인 사상 최고가 경신, 10만 달러 돌파",
                                                    "publisher": "코인데스크",
                                                    "publishedAt": "2025-01-15T09:30:00",
                                                    "sentimentLabel": "POSITIVE",
                                                    "sentimentScore": 0.85
                                                },
                                                "error": null
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (ID 누락)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "ID 누락 오류",
                                    value = """
                                            {
                                                "success": false,
                                                "data": null,
                                                "error": {
                                                    "code": "INVALID_INPUT_VALUE",
                                                    "message": "뉴스 ID가 필요합니다."
                                                }
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "뉴스를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "뉴스 없음 오류",
                                    value = """
                                            {
                                                "success": false,
                                                "data": null,
                                                "error": {
                                                    "code": "NEWS_NOT_FOUND",
                                                    "message": "해당 뉴스를 찾을 수 없습니다."
                                                }
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<SearchNewsResponse>> searchById(
            @Parameter(
                    name = "id",
                    description = "뉴스 고유 ID",
                    required = true,
                    example = "1"
            ) Long id);

    @Operation(
            summary = "전체 뉴스 목록 조회",
            description = """
                    저장된 모든 뉴스 목록을 페이징하여 조회합니다.

                    - 최신 뉴스부터 정렬되어 반환됩니다.
                    - 각 뉴스의 제목, 발행사, 발행일시 정보를 포함합니다.
                    - 감성 분석 결과가 있는 경우 함께 반환됩니다.
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "조회 성공 예시",
                                    value = """
                                            {
                                                "success": true,
                                                "data": {
                                                    "content": [
                                                        {
                                                            "id": 1,
                                                            "title": "비트코인 사상 최고가 경신, 10만 달러 돌파",
                                                            "publisher": "코인데스크",
                                                            "publishedAt": "2025-01-15T09:30:00",
                                                            "sentimentLabel": "POSITIVE",
                                                            "sentimentScore": 0.85
                                                        },
                                                        {
                                                            "id": 2,
                                                            "title": "이더리움 2.0 업그레이드 완료",
                                                            "publisher": "블록미디어",
                                                            "publishedAt": "2025-01-14T14:20:00",
                                                            "sentimentLabel": "POSITIVE",
                                                            "sentimentScore": 0.72
                                                        }
                                                    ],
                                                    "page": 0,
                                                    "size": 20,
                                                    "totalElements": 150,
                                                    "totalPages": 8,
                                                    "first": true,
                                                    "last": false,
                                                    "hasNext": true,
                                                    "hasPrevious": false
                                                },
                                                "error": null
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<PageResponse<SearchNewsResponse>>> searchNewsList(
            @Parameter(
                    name = "page",
                    description = "페이지 번호 (0부터 시작)",
                    required = false,
                    example = "0",
                    schema = @Schema(type = "integer", defaultValue = "0", minimum = "0")
            ) Integer page,
            @Parameter(
                    name = "size",
                    description = "페이지 크기 (기본값: 20, 최대: 50)",
                    required = false,
                    example = "20",
                    schema = @Schema(type = "integer", defaultValue = "20", minimum = "1", maximum = "50")
            ) Integer size);

    @Operation(
            summary = "키워드로 뉴스 검색",
            description = """
                    키워드를 기반으로 뉴스를 검색합니다.

                    - 뉴스 제목과 내용에서 키워드를 검색합니다.
                    - 대소문자를 구분하지 않습니다.
                    - 검색 결과는 최신순으로 정렬됩니다.

                    **검색 예시:**
                    - "비트코인" → 비트코인 관련 뉴스
                    - "이더리움" → 이더리움 관련 뉴스
                    - "규제" → 규제 관련 뉴스
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "검색 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "검색 성공 예시",
                                    value = """
                                            {
                                                "success": true,
                                                "data": {
                                                    "content": [
                                                        {
                                                            "id": 1,
                                                            "title": "비트코인 사상 최고가 경신, 10만 달러 돌파",
                                                            "publisher": "코인데스크",
                                                            "publishedAt": "2025-01-15T09:30:00",
                                                            "sentimentLabel": "POSITIVE",
                                                            "sentimentScore": 0.85
                                                        },
                                                        {
                                                            "id": 3,
                                                            "title": "비트코인 ETF 거래량 신기록",
                                                            "publisher": "한경코인",
                                                            "publishedAt": "2025-01-13T11:00:00",
                                                            "sentimentLabel": "POSITIVE",
                                                            "sentimentScore": 0.78
                                                        }
                                                    ],
                                                    "page": 0,
                                                    "size": 20,
                                                    "totalElements": 25,
                                                    "totalPages": 2,
                                                    "first": true,
                                                    "last": false,
                                                    "hasNext": true,
                                                    "hasPrevious": false
                                                },
                                                "error": null
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (키워드 누락)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "키워드 누락 오류",
                                    value = """
                                            {
                                                "success": false,
                                                "data": null,
                                                "error": {
                                                    "code": "INVALID_INPUT_VALUE",
                                                    "message": "검색 키워드가 필요합니다."
                                                }
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<PageResponse<SearchNewsResponse>>> searchByKeyword(
            @Parameter(
                    name = "keyword",
                    description = "검색 키워드",
                    required = true,
                    example = "비트코인"
            ) String keyword,
            @Parameter(
                    name = "page",
                    description = "페이지 번호 (0부터 시작)",
                    required = false,
                    example = "0",
                    schema = @Schema(type = "integer", defaultValue = "0", minimum = "0")
            ) Integer page,
            @Parameter(
                    name = "size",
                    description = "페이지 크기 (기본값: 20, 최대: 50)",
                    required = false,
                    example = "20",
                    schema = @Schema(type = "integer", defaultValue = "20", minimum = "1", maximum = "50")
            ) Integer size);

    @Operation(
            summary = "[메인] AI 분석 뉴스 목록 조회",
            description = """
                    메인 페이지용 AI 분석 결과가 포함된 뉴스 목록을 페이징하여 조회합니다.

                    **반환 정보:**
                    - 뉴스 제목, 출처, 발행 시점
                    - AI 감성 분석 결과 (호재/악재/중립 뱃지)
                    - 분석 신뢰도 (0.0 ~ 1.0)
                    - 관련 코인 목록 (뱃지 표시용)

                    **감성 레이블:**
                    - `POSITIVE`: 호재 (긍정적 뉴스)
                    - `NEGATIVE`: 악재 (부정적 뉴스)
                    - `NEUTRAL`: 중립 (객관적 정보)

                    **정렬:**
                    - 최신 발행일시 기준 내림차순

                    **사용처:**
                    - 메인 페이지 뉴스 섹션
                    - 뉴스 클릭 시 상세 페이지 이동 (id 활용)
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "조회 성공 예시",
                                    value = """
                                            {
                                                "success": true,
                                                "data": {
                                                    "content": [
                                                        {
                                                            "id": 1,
                                                            "title": "비트코인 사상 최고가 경신, 10만 달러 돌파",
                                                            "publisher": "코인데스크",
                                                            "publishedAt": "2025-01-15T09:30:00",
                                                            "originalLink": "https://coindesk.com/news/123",
                                                            "sentimentLabel": "POSITIVE",
                                                            "sentimentScore": 0.85,
                                                            "relatedCryptos": [
                                                                {
                                                                    "ticker": "BTC",
                                                                    "name": "비트코인",
                                                                    "logoUrl": "https://example.com/btc.png"
                                                                }
                                                            ]
                                                        },
                                                        {
                                                            "id": 2,
                                                            "title": "SEC, 이더리움 ETF 승인 검토 중",
                                                            "publisher": "블록미디어",
                                                            "publishedAt": "2025-01-14T14:20:00",
                                                            "originalLink": "https://blockmedia.com/news/456",
                                                            "sentimentLabel": "NEUTRAL",
                                                            "sentimentScore": 0.62,
                                                            "relatedCryptos": [
                                                                {
                                                                    "ticker": "ETH",
                                                                    "name": "이더리움",
                                                                    "logoUrl": "https://example.com/eth.png"
                                                                }
                                                            ]
                                                        }
                                                    ],
                                                    "page": 0,
                                                    "size": 20,
                                                    "totalElements": 150,
                                                    "totalPages": 8,
                                                    "first": true,
                                                    "last": false,
                                                    "hasNext": true,
                                                    "hasPrevious": false
                                                },
                                                "error": null
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<PageResponse<NewsWithAnalysisResponse>>> getNewsWithAnalysis(
            @Parameter(
                    name = "page",
                    description = "페이지 번호 (0부터 시작)",
                    required = false,
                    example = "0",
                    schema = @Schema(type = "integer", defaultValue = "0", minimum = "0")
            ) Integer page,
            @Parameter(
                    name = "size",
                    description = "페이지 크기 (기본값: 20, 최대: 50)",
                    required = false,
                    example = "20",
                    schema = @Schema(type = "integer", defaultValue = "20", minimum = "1", maximum = "50")
            ) Integer size);

    @Operation(
            summary = "[상세] 뉴스 상세 및 AI 분석 결과 조회",
            description = """
                    특정 뉴스의 상세 정보와 AI 분석 결과를 조회합니다.

                    **반환 정보:**
                    - 뉴스 기본 정보 (제목, 본문, 출처, 발행일시, 원문링크)
                    - AI 감성 분석 결과 (호재/악재/중립, 신뢰도, 분석 요약)
                    - 관련 코인 목록

                    **감성 레이블:**
                    - `POSITIVE`: 호재 (긍정적 뉴스, 상승 기대)
                    - `NEGATIVE`: 악재 (부정적 뉴스, 하락 우려)
                    - `NEUTRAL`: 중립 (객관적 정보 전달)

                    **사용처:**
                    - 뉴스 상세 페이지
                    - 메인 페이지에서 뉴스 클릭 시 이동하는 페이지
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "조회 성공 예시",
                                    value = """
                                            {
                                                "success": true,
                                                "data": {
                                                    "id": 1,
                                                    "title": "비트코인 사상 최고가 경신, 10만 달러 돌파",
                                                    "content": "비트코인이 10만 달러를 돌파하며 사상 최고가를 기록했다. 기관 투자자들의 대규모 매수세가 이어지면서 상승 모멘텀이 지속되고 있다. 전문가들은 이번 상승이 ETF 승인 기대감과 반감기 효과가 맞물린 결과라고 분석했다.",
                                                    "publisher": "코인데스크",
                                                    "publishedAt": "2025-01-15T09:30:00",
                                                    "originalLink": "https://coindesk.com/news/123",
                                                    "analysis": {
                                                        "sentimentLabel": "POSITIVE",
                                                        "sentimentScore": 0.85,
                                                        "summary": "감성분석 결과: 긍정 (신뢰도: 85.0%) | 긍정: 85.0%, 중립: 10.0%, 부정: 5.0%"
                                                    },
                                                    "relatedCryptos": [
                                                        {
                                                            "ticker": "BTC",
                                                            "name": "비트코인",
                                                            "logoUrl": "https://example.com/btc.png"
                                                        }
                                                    ]
                                                },
                                                "error": null
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "뉴스를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "뉴스 없음 오류",
                                    value = """
                                            {
                                                "success": false,
                                                "data": null,
                                                "error": {
                                                    "code": "NEWS_NOT_FOUND",
                                                    "message": "해당 뉴스를 찾을 수 없습니다."
                                                }
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<NewsDetailResponse>> getNewsDetail(
            @Parameter(
                    name = "id",
                    description = "뉴스 고유 ID",
                    required = true,
                    example = "1"
            ) Long id);
}
