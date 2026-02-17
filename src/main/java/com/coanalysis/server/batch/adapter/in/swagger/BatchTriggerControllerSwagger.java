package com.coanalysis.server.batch.adapter.in.swagger;

import com.coanalysis.server.infrastructure.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "[BE 테스트 전용] Batch Trigger", description = "배치 작업 수동 트리거 API - BE 테스트 전용으로, 운영 환경에서 사용하지 마세요.")
public interface BatchTriggerControllerSwagger {

    @Operation(
            summary = "[BE 테스트 전용] 뉴스 수집 배치 실행",
            description = """
                    **BE 테스트 전용 API입니다. 운영 환경에서 사용하지 마세요.**

                    뉴스 수집 배치를 수동으로 실행합니다.
                    - 정상 스케줄: 1시간마다 자동 실행
                    - 수집된 뉴스 개수를 반환합니다.
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "배치 실행 완료",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "실행 성공",
                                    value = """
                                            {
                                                "success": true,
                                                "data": "News collection completed. Processed: 15 articles",
                                                "error": null
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "배치 실행 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "실행 실패",
                                    value = """
                                            {
                                                "success": false,
                                                "data": null,
                                                "error": {
                                                    "code": "INTERNAL_SERVER_ERROR",
                                                    "message": "뉴스 수집 배치 실행 중 오류가 발생했습니다."
                                                }
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<String>> triggerNewsCollection();

    @Operation(
            summary = "[BE 테스트 전용] 암호화폐 동기화 배치 실행",
            description = """
                    **BE 테스트 전용 API입니다. 운영 환경에서 사용하지 마세요.**

                    암호화폐 목록 동기화 배치를 수동으로 실행합니다.
                    - 정상 스케줄: 매일 새벽 3시 자동 실행
                    - 추가된 코인 개수를 반환합니다.
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "배치 실행 완료",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "실행 성공",
                                    value = """
                                            {
                                                "success": true,
                                                "data": "Crypto sync completed. Added: 5 cryptos",
                                                "error": null
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "배치 실행 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "실행 실패",
                                    value = """
                                            {
                                                "success": false,
                                                "data": null,
                                                "error": {
                                                    "code": "INTERNAL_SERVER_ERROR",
                                                    "message": "암호화폐 동기화 배치 실행 중 오류가 발생했습니다."
                                                }
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<String>> triggerCryptoSync();

    @Operation(
            summary = "[BE 테스트 전용] 예측 생성 배치 실행",
            description = """
                    **BE 테스트 전용 API입니다. 운영 환경에서 사용하지 마세요.**

                    모든 코인에 대한 예측 생성 배치를 수동으로 실행합니다.
                    - 정상 스케줄: 매일 09:00 자동 실행
                    - 생성된 예측 개수를 반환합니다.
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "배치 실행 완료",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "실행 성공",
                                    value = """
                                            {
                                                "success": true,
                                                "data": "Prediction generation completed. Generated: 10 predictions",
                                                "error": null
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "배치 실행 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "실행 실패",
                                    value = """
                                            {
                                                "success": false,
                                                "data": null,
                                                "error": {
                                                    "code": "INTERNAL_SERVER_ERROR",
                                                    "message": "예측 생성 배치 실행 중 오류가 발생했습니다."
                                                }
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<String>> triggerPredictionGeneration();

    @Operation(
            summary = "[BE 테스트 전용] 예측 검증 배치 실행",
            description = """
                    **BE 테스트 전용 API입니다. 운영 환경에서 사용하지 마세요.**

                    미검증 예측 결과 검증 배치를 수동으로 실행합니다.
                    - 정상 스케줄: 매 시간 정각에 자동 실행
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "배치 실행 완료",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "실행 성공",
                                    value = """
                                            {
                                                "success": true,
                                                "data": "Prediction verification completed",
                                                "error": null
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "배치 실행 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "실행 실패",
                                    value = """
                                            {
                                                "success": false,
                                                "data": null,
                                                "error": {
                                                    "code": "INTERNAL_SERVER_ERROR",
                                                    "message": "예측 검증 배치 실행 중 오류가 발생했습니다."
                                                }
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<String>> triggerPredictionVerification();
}
