package com.coanalysis.server.crypto.adapter.in.swagger;

import com.coanalysis.server.crypto.adapter.in.dto.SearchCryptoResponse;
import com.coanalysis.server.infrastructure.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Crypto Search", description = "암호화폐 검색 API - 코인 정보 검색 및 상세 조회 기능을 제공합니다.")
public interface SearchCryptoControllerSwagger {

    @Operation(
            summary = "키워드로 코인 검색",
            description = """
                    키워드를 기반으로 암호화폐를 검색합니다.

                    - 코인명(한글/영문) 또는 티커(심볼)로 검색 가능합니다.
                    - 검색 결과는 현재가와 거래대금 정보를 포함합니다.
                    - 대소문자를 구분하지 않습니다.

                    **검색 예시:**
                    - "비트" → 비트코인, 비트코인캐시 등
                    - "BTC" → 비트코인
                    - "이더" → 이더리움, 이더리움클래식 등
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
                                                "data": [
                                                    {
                                                        "id": 1,
                                                        "ticker": "BTC",
                                                        "name": "비트코인",
                                                        "logoUrl": "https://example.com/btc.png",
                                                        "currentPrice": 135000000.0,
                                                        "tradingVolume": 500000000000.0
                                                    },
                                                    {
                                                        "id": 2,
                                                        "ticker": "BCH",
                                                        "name": "비트코인캐시",
                                                        "logoUrl": "https://example.com/bch.png",
                                                        "currentPrice": 650000.0,
                                                        "tradingVolume": 50000000000.0
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
    ResponseEntity<ApiResponse<List<SearchCryptoResponse>>> searchByKeyword(
            @Parameter(
                    name = "keyword",
                    description = "검색 키워드 (코인명 또는 티커)",
                    required = true,
                    example = "비트코인"
            ) String keyword);

    @Operation(
            summary = "코인 상세 조회",
            description = """
                    코인 ID를 기반으로 암호화폐 상세 정보를 조회합니다.

                    - 코인의 현재가, 거래대금 등 상세 정보를 제공합니다.
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
                                                    "ticker": "BTC",
                                                    "name": "비트코인",
                                                    "logoUrl": "https://example.com/btc.png",
                                                    "currentPrice": 135000000.0,
                                                    "tradingVolume": 500000000000.0
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
                                                    "message": "코인 ID가 필요합니다."
                                                }
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "코인을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "코인 없음 오류",
                                    value = """
                                            {
                                                "success": false,
                                                "data": null,
                                                "error": {
                                                    "code": "CRYPTO_NOT_FOUND",
                                                    "message": "해당 코인을 찾을 수 없습니다."
                                                }
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<SearchCryptoResponse>> findById(
            @Parameter(
                    name = "id",
                    description = "코인 고유 ID",
                    required = true,
                    example = "1"
            ) Long id);
}