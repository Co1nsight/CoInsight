package com.coanalysis.server.main.adapter.in.swagger;

import com.coanalysis.server.infrastructure.response.ApiResponse;
import com.coanalysis.server.infrastructure.response.PageResponse;
import com.coanalysis.server.main.adapter.in.dto.MainCryptoResponse;
import com.coanalysis.server.main.adapter.in.dto.MainNewsResponse;
import com.coanalysis.server.main.adapter.in.dto.UnifiedSearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Main", description = "메인화면 API - 뉴스, 코인 시세, 통합 검색 기능을 제공합니다.")
public interface MainControllerSwagger {

    @Operation(
            summary = "최신 뉴스 기사 목록 조회",
            description = """
                    메인화면에 표시할 최신 뉴스 기사 목록을 조회합니다.

                    **포함 정보:**
                    - 기사 제목, 출처, 간략 내용 (100자)
                    - AI 감성 분석 결과 (호재/악재)
                    - 연관된 코인 목록

                    **정렬:** 최신순 (발행일시 내림차순)
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
                                                            "title": "비트코인 10만 달러 돌파, 사상 최고가 경신",
                                                            "publisher": "TokenPost",
                                                            "contentSnippet": "비트코인이 10만 달러를 돌파하며 사상 최고가를 경신했다. 기관 투자자들의...",
                                                            "publishedAt": "2025-01-15T09:30:00",
                                                            "language": "KO",
                                                            "sentimentLabel": "POSITIVE",
                                                            "sentimentScore": 0.92,
                                                            "relatedCryptos": [
                                                                {"ticker": "BTC", "name": "비트코인", "logoUrl": "..."}
                                                            ]
                                                        }
                                                    ],
                                                    "page": 0,
                                                    "size": 10,
                                                    "totalElements": 100,
                                                    "totalPages": 10
                                                },
                                                "error": null
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<PageResponse<MainNewsResponse>>> getMainNews(
            @Parameter(name = "page", description = "페이지 번호 (0부터 시작)", example = "0") Integer page,
            @Parameter(name = "size", description = "페이지 크기 (최대 50)", example = "10") Integer size);

    @Operation(
            summary = "거래대금 순 코인 시세 목록 조회",
            description = """
                    메인화면에 표시할 코인 시세 목록을 거래대금 순으로 조회합니다.

                    **포함 정보:**
                    - 코인 기본 정보 (티커, 이름, 로고)
                    - 현재가, 24시간 거래대금
                    - 등락률, 등락가
                    - 24시간 고가/저가

                    **정렬:** 24시간 거래대금 내림차순

                    **데이터 출처:** 빗썸 API (실시간)
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
                                                            "ticker": "BTC",
                                                            "name": "비트코인",
                                                            "logoUrl": "https://example.com/btc.png",
                                                            "currentPrice": 135000000,
                                                            "tradingVolume24h": 500000000000,
                                                            "changeRate": 0.0523,
                                                            "changePrice": 6500000,
                                                            "highPrice": 138000000,
                                                            "lowPrice": 132000000
                                                        }
                                                    ],
                                                    "page": 0,
                                                    "size": 10,
                                                    "totalElements": 50,
                                                    "totalPages": 5
                                                },
                                                "error": null
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<PageResponse<MainCryptoResponse>>> getCryptosByTradingVolume(
            @Parameter(name = "page", description = "페이지 번호 (0부터 시작)", example = "0") Integer page,
            @Parameter(name = "size", description = "페이지 크기 (최대 50)", example = "10") Integer size);

    @Operation(
            summary = "통합 검색 (코인 + 뉴스)",
            description = """
                    상단 검색창에서 사용하는 통합 검색 API입니다.

                    **검색 대상:**
                    - 코인: 티커, 이름으로 검색
                    - 뉴스: 제목, 내용으로 검색

                    **결과:** 코인과 뉴스 목록을 동시에 반환

                    **제한:** 각각 최대 5개씩 반환 (빠른 응답을 위해)
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "검색 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "검색 성공 예시",
                                    value = """
                                            {
                                                "success": true,
                                                "data": {
                                                    "cryptos": [
                                                        {"ticker": "BTC", "name": "비트코인", "logoUrl": "..."}
                                                    ],
                                                    "news": [
                                                        {
                                                            "id": 1,
                                                            "title": "비트코인 10만 달러 돌파",
                                                            "publisher": "TokenPost",
                                                            "publishedAt": "2025-01-15T09:30:00",
                                                            "sentimentLabel": "POSITIVE"
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
    ResponseEntity<ApiResponse<UnifiedSearchResponse>> unifiedSearch(
            @Parameter(name = "keyword", description = "검색 키워드", required = true, example = "비트코인") String keyword,
            @Parameter(name = "cryptoLimit", description = "코인 검색 결과 최대 개수", example = "5") Integer cryptoLimit,
            @Parameter(name = "newsLimit", description = "뉴스 검색 결과 최대 개수", example = "5") Integer newsLimit);
}
