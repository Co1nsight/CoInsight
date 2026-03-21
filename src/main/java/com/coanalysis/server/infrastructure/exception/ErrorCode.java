package com.coanalysis.server.infrastructure.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C001", "서버 내부 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C002", "잘못된 입력값입니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "C003", "잘못된 타입입니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "C004", "리소스를 찾을 수 없습니다."),

    // News
    NEWS_NOT_FOUND(HttpStatus.NOT_FOUND, "N001", "뉴스를 찾을 수 없습니다."),

    // Sentiment Analysis
    SENTIMENT_ANALYSIS_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "S001", "감성 분석에 실패했습니다."),
    SENTIMENT_ANALYSIS_EMPTY_TEXT(HttpStatus.BAD_REQUEST, "S002", "분석할 텍스트가 비어있습니다."),
    SENTIMENT_ANALYSIS_API_ERROR(HttpStatus.BAD_GATEWAY, "S003", "외부 API 호출에 실패했습니다."),
    SENTIMENT_ANALYSIS_PARSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S004", "API 응답 파싱에 실패했습니다."),
    HUGGINGFACE_QUOTA_EXCEEDED(HttpStatus.PAYMENT_REQUIRED, "S005", "HuggingFace API 크레딧이 소진되었습니다."),

    // Crypto
    CRYPTO_NOT_FOUND(HttpStatus.NOT_FOUND, "CR001", "암호화폐를 찾을 수 없습니다."),

    // Bithumb
    BITHUMB_API_ERROR(HttpStatus.BAD_GATEWAY, "B001", "빗썸 API 호출에 실패했습니다."),
    BITHUMB_INVALID_MARKET(HttpStatus.BAD_REQUEST, "B002", "지원하지 않는 마켓입니다."),
    BITHUMB_RATE_LIMIT(HttpStatus.TOO_MANY_REQUESTS, "B003", "API 요청 제한을 초과했습니다."),
    BITHUMB_PARSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "B004", "API 응답 파싱에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}