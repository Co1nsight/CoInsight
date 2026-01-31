package com.coanalysis.server.news.adapter.in.swagger;

import com.coanalysis.server.infrastructure.response.ApiResponse;
import com.coanalysis.server.news.adapter.in.dto.SearchNewsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

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
                    저장된 모든 뉴스 목록을 조회합니다.

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
                                                "data": [
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
                                                "error": null
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<List<SearchNewsResponse>>> searchNewsList();

    @Operation(
            summary = "키워드로 뉴스 검색",
            description = """
                    키워드를 기반으로 뉴스를 검색합니다.

                    - 뉴스 제목에서 키워드를 검색합니다.
                    - 대소문자를 구분하지 않습니다.
                    - 검색 결과는 관련도순으로 정렬됩니다.

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
                                            [
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
                                            ]
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
    ResponseEntity<List<SearchNewsResponse>> searchByKeyword(
            @Parameter(
                    name = "keyword",
                    description = "검색 키워드",
                    required = true,
                    example = "비트코인"
            ) String keyword);
}