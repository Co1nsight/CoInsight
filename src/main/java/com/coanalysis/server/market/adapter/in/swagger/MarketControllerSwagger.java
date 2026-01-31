package com.coanalysis.server.market.adapter.in.swagger;

import com.coanalysis.server.infrastructure.response.ApiResponse;
import com.coanalysis.server.market.adapter.in.dto.CandleResponse;
import com.coanalysis.server.market.adapter.in.dto.TickerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Market", description = "시세 조회 API - 빗썸 거래소 연동을 통한 실시간 암호화폐 시세 정보를 제공합니다.")
public interface MarketControllerSwagger {

    @Operation(
            summary = "코인 분봉 차트 조회",
            description = """
                    특정 코인의 분봉 캔들 데이터를 조회합니다.

                    **캔들 데이터 설명:**
                    - `timestamp`: 캔들 시작 시간 (Unix timestamp, milliseconds)
                    - `openPrice`: 시가 (해당 기간 시작 가격)
                    - `highPrice`: 고가 (해당 기간 최고 가격)
                    - `lowPrice`: 저가 (해당 기간 최저 가격)
                    - `closePrice`: 종가 (해당 기간 마지막 가격)
                    - `volume`: 거래량 (해당 기간 거래된 코인 수량)

                    **지원 분봉 단위:**
                    - 1분, 3분, 5분, 10분, 15분, 30분, 60분(1시간), 240분(4시간)

                    **캐싱:**
                    - 데이터는 1분 단위로 캐싱되어 빠른 응답을 제공합니다.

                    **사용 예시:**
                    - 차트 렌더링용 데이터 조회
                    - 기술적 분석 지표 계산
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
                                                        "timestamp": 1705312800000,
                                                        "openPrice": 135000000,
                                                        "highPrice": 135500000,
                                                        "lowPrice": 134800000,
                                                        "closePrice": 135200000,
                                                        "volume": 12.5
                                                    },
                                                    {
                                                        "timestamp": 1705312860000,
                                                        "openPrice": 135200000,
                                                        "highPrice": 135800000,
                                                        "lowPrice": 135100000,
                                                        "closePrice": 135700000,
                                                        "volume": 8.3
                                                    }
                                                ],
                                                "error": null
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "잘못된 심볼 오류",
                                    value = """
                                            {
                                                "success": false,
                                                "data": null,
                                                "error": {
                                                    "code": "INVALID_INPUT_VALUE",
                                                    "message": "지원하지 않는 코인 심볼입니다."
                                                }
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "외부 API 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "외부 API 오류",
                                    value = """
                                            {
                                                "success": false,
                                                "data": null,
                                                "error": {
                                                    "code": "EXTERNAL_API_ERROR",
                                                    "message": "시세 정보를 가져오는데 실패했습니다."
                                                }
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<List<CandleResponse>>> getCandles(
            @Parameter(
                    name = "symbol",
                    description = "코인 심볼 (티커). 대문자로 입력 권장",
                    required = true,
                    example = "BTC",
                    schema = @Schema(type = "string", allowableValues = {"BTC", "ETH", "XRP", "EOS", "TRX", "ADA", "LINK", "DOT"})
            ) String symbol,
            @Parameter(
                    name = "unit",
                    description = "분봉 단위. 지원: 1, 3, 5, 10, 15, 30, 60, 240",
                    required = false,
                    example = "1",
                    schema = @Schema(type = "integer", defaultValue = "1", allowableValues = {"1", "3", "5", "10", "15", "30", "60", "240"})
            ) Integer unit,
            @Parameter(
                    name = "count",
                    description = "조회할 캔들 개수 (최소 1, 최대 200)",
                    required = false,
                    example = "200",
                    schema = @Schema(type = "integer", defaultValue = "200", minimum = "1", maximum = "200")
            ) Integer count
    );

    @Operation(
            summary = "전체 코인 목록 조회",
            description = """
                    홈화면용 전체 코인 시세 목록을 조회합니다.

                    **반환 데이터:**
                    - `symbol`: 코인 심볼 (예: BTC, ETH)
                    - `name`: 코인 한글명 (예: 비트코인, 이더리움)
                    - `price`: 현재가
                    - `changeRate`: 24시간 변동률 (소수점, 예: 0.05 = 5%)
                    - `changePrice`: 24시간 변동 금액
                    - `tradeValue`: 24시간 거래대금
                    - `market`: 마켓 타입 (KRW, BTC)

                    **마켓 타입:**
                    - `KRW`: 원화 마켓 (원화로 거래)
                    - `BTC`: 비트코인 마켓 (비트코인으로 거래)

                    **정렬 기준:**
                    - `tradeValue`: 24시간 거래대금 순 (기본값)

                    **캐싱:**
                    - 데이터는 1분 단위로 캐싱되어 빠른 응답을 제공합니다.
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
                                                        "symbol": "BTC",
                                                        "name": "비트코인",
                                                        "price": 135000000,
                                                        "changeRate": 0.0523,
                                                        "changePrice": 6700000,
                                                        "tradeValue": 892000000000,
                                                        "market": "KRW"
                                                    },
                                                    {
                                                        "symbol": "ETH",
                                                        "name": "이더리움",
                                                        "price": 4500000,
                                                        "changeRate": -0.0215,
                                                        "changePrice": -98000,
                                                        "tradeValue": 456000000000,
                                                        "market": "KRW"
                                                    },
                                                    {
                                                        "symbol": "XRP",
                                                        "name": "리플",
                                                        "price": 3200,
                                                        "changeRate": 0.0812,
                                                        "changePrice": 240,
                                                        "tradeValue": 234000000000,
                                                        "market": "KRW"
                                                    }
                                                ],
                                                "error": null
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "외부 API 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "외부 API 오류",
                                    value = """
                                            {
                                                "success": false,
                                                "data": null,
                                                "error": {
                                                    "code": "EXTERNAL_API_ERROR",
                                                    "message": "시세 정보를 가져오는데 실패했습니다."
                                                }
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<List<TickerResponse>>> getTickers(
            @Parameter(
                    name = "marketType",
                    description = "마켓 타입. KRW(원화마켓) 또는 BTC(비트코인마켓)",
                    required = false,
                    example = "KRW",
                    schema = @Schema(type = "string", defaultValue = "KRW", allowableValues = {"KRW", "BTC"})
            ) String marketType,
            @Parameter(
                    name = "sortBy",
                    description = "정렬 기준. 현재 거래대금순(tradeValue)만 지원",
                    required = false,
                    example = "tradeValue",
                    schema = @Schema(type = "string", defaultValue = "tradeValue", allowableValues = {"tradeValue"})
            ) String sortBy
    );
}
