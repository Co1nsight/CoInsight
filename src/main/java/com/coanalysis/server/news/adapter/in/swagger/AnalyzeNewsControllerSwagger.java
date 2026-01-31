package com.coanalysis.server.news.adapter.in.swagger;

import com.coanalysis.server.infrastructure.response.ApiResponse;
import com.coanalysis.server.news.adapter.in.dto.AnalyzeNewsRequest;
import com.coanalysis.server.news.adapter.in.dto.AnalyzeNewsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "News Analysis", description = "뉴스 감성 분석 API - HuggingFace AI 모델을 활용한 뉴스 감성 분석 기능을 제공합니다.")
public interface AnalyzeNewsControllerSwagger {

    @Operation(
            summary = "뉴스 ID로 감성 분석",
            description = """
                    저장된 뉴스 ID를 기반으로 감성 분석을 수행합니다.

                    **분석 프로세스:**
                    1. 뉴스 ID로 저장된 뉴스 조회
                    2. HuggingFace 감성 분석 모델로 분석 수행
                    3. 긍정/중립/부정 감성 레이블과 신뢰도 점수 반환

                    **감성 레이블:**
                    - `POSITIVE`: 긍정적 뉴스 (호재, 상승 기대 등)
                    - `NEUTRAL`: 중립적 뉴스 (객관적 정보 전달)
                    - `NEGATIVE`: 부정적 뉴스 (악재, 하락 우려 등)

                    **API 토큰:**
                    - 헤더에 HuggingFace API 토큰을 전달하면 해당 토큰으로 분석합니다.
                    - 토큰이 없으면 서버에 설정된 기본 토큰을 사용합니다.
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "분석 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "분석 성공 예시",
                                    value = """
                                            {
                                                "success": true,
                                                "data": {
                                                    "sentimentLabel": "POSITIVE",
                                                    "sentimentScore": 0.85,
                                                    "summary": "감성분석 결과: 긍정 (신뢰도: 85.0%) | 긍정: 85.0%, 중립: 10.0%, 부정: 5.0%"
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
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "감성 분석 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "분석 실패 오류",
                                    value = """
                                            {
                                                "success": false,
                                                "data": null,
                                                "error": {
                                                    "code": "SENTIMENT_ANALYSIS_FAILED",
                                                    "message": "감성 분석에 실패했습니다. 잠시 후 다시 시도해주세요."
                                                }
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<AnalyzeNewsResponse>> analyzeById(
            @Parameter(
                    name = "newsId",
                    description = "분석할 뉴스의 고유 ID",
                    required = true,
                    example = "1"
            ) Long newsId,
            @Parameter(
                    name = "X-HUGGINGFACE-API-TOKEN",
                    description = "HuggingFace API 토큰 (선택사항). 제공하지 않으면 서버 기본 토큰 사용",
                    required = false,
                    example = "hf_xxxxxxxxxxxxxxxxxxxxx"
            ) String apiToken);

    @Operation(
            summary = "텍스트 감성 분석",
            description = """
                    입력된 텍스트(제목, 내용)를 기반으로 감성 분석을 수행합니다.

                    **사용 사례:**
                    - 저장되지 않은 뉴스 텍스트 분석
                    - 실시간 뉴스 감성 분석
                    - 사용자 입력 텍스트 분석

                    **분석 프로세스:**
                    1. 제목과 내용을 결합하여 분석 텍스트 생성
                    2. HuggingFace 감성 분석 모델로 분석 수행
                    3. 긍정/중립/부정 감성 레이블과 신뢰도 점수 반환

                    **입력 권장사항:**
                    - 제목은 필수입니다.
                    - 내용은 선택사항이지만, 더 정확한 분석을 위해 제공을 권장합니다.
                    - 한국어와 영어 텍스트 모두 분석 가능합니다.
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "분석 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "긍정적 분석 결과",
                                            value = """
                                                    {
                                                        "success": true,
                                                        "data": {
                                                            "sentimentLabel": "POSITIVE",
                                                            "sentimentScore": 0.92,
                                                            "summary": "감성분석 결과: 긍정 (신뢰도: 92.0%) | 긍정: 92.0%, 중립: 5.0%, 부정: 3.0%"
                                                        },
                                                        "error": null
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "부정적 분석 결과",
                                            value = """
                                                    {
                                                        "success": true,
                                                        "data": {
                                                            "sentimentLabel": "NEGATIVE",
                                                            "sentimentScore": 0.78,
                                                            "summary": "감성분석 결과: 부정 (신뢰도: 78.0%) | 긍정: 12.0%, 중립: 10.0%, 부정: 78.0%"
                                                        },
                                                        "error": null
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "제목 누락 오류",
                                    value = """
                                            {
                                                "success": false,
                                                "data": null,
                                                "error": {
                                                    "code": "INVALID_INPUT_VALUE",
                                                    "message": "뉴스 제목이 필요합니다."
                                                }
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "감성 분석 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "분석 실패 오류",
                                    value = """
                                            {
                                                "success": false,
                                                "data": null,
                                                "error": {
                                                    "code": "SENTIMENT_ANALYSIS_FAILED",
                                                    "message": "감성 분석에 실패했습니다. 잠시 후 다시 시도해주세요."
                                                }
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<AnalyzeNewsResponse>> analyzeText(
            @RequestBody(
                    description = "분석할 뉴스 텍스트",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AnalyzeNewsRequest.class),
                            examples = @ExampleObject(
                                    name = "분석 요청 예시",
                                    value = """
                                            {
                                                "title": "비트코인 사상 최고가 경신",
                                                "content": "비트코인이 10만 달러를 돌파하며 사상 최고가를 기록했다. 기관 투자자들의 대규모 매수세가 이어지면서 상승 모멘텀이 지속되고 있다."
                                            }
                                            """
                            )
                    )
            ) AnalyzeNewsRequest request,
            @Parameter(
                    name = "X-HUGGINGFACE-API-TOKEN",
                    description = "HuggingFace API 토큰 (선택사항). 제공하지 않으면 서버 기본 토큰 사용",
                    required = false,
                    example = "hf_xxxxxxxxxxxxxxxxxxxxx"
            ) String apiToken);
}