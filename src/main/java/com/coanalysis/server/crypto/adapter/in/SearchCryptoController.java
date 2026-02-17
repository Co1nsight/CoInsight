package com.coanalysis.server.crypto.adapter.in;

import com.coanalysis.server.crypto.adapter.in.dto.SearchCryptoResponse;
import com.coanalysis.server.crypto.adapter.in.swagger.SearchCryptoControllerSwagger;
import com.coanalysis.server.crypto.adapter.out.SearchCryptoNewsQuery;
import com.coanalysis.server.crypto.application.domain.Crypto;
import com.coanalysis.server.crypto.application.port.in.SearchCryptoUsecase;
import com.coanalysis.server.infrastructure.exception.CustomException;
import com.coanalysis.server.infrastructure.exception.ErrorCode;
import com.coanalysis.server.infrastructure.response.ApiResponse;
import com.coanalysis.server.infrastructure.response.PageResponse;
import com.coanalysis.server.news.adapter.in.dto.SearchNewsResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/crypto")
@RequiredArgsConstructor
public class SearchCryptoController implements SearchCryptoControllerSwagger {

    private final SearchCryptoUsecase searchCryptoUsecase;
    private final SearchCryptoNewsQuery searchCryptoNewsQuery;

    @Override
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SearchCryptoResponse>>> searchByKeyword(@RequestParam String keyword) {
        List<Crypto> cryptoList = searchCryptoUsecase.searchByKeyword(keyword);
        return ResponseEntity.ok(ApiResponse.success(SearchCryptoResponse.from(cryptoList)));
    }

    @Override
    @GetMapping("/{ticker}")
    public ResponseEntity<ApiResponse<SearchCryptoResponse>> findByTicker(
            @PathVariable(value = "ticker") String ticker,
            @RequestParam(value = "market", defaultValue = "KRW") String market) {
        if (ObjectUtils.isEmpty(ticker)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "코인 티커가 필요합니다.");
        }

        Crypto crypto = searchCryptoUsecase.findByTickerAndMarket(ticker, market)
                .orElseThrow(() -> new CustomException(ErrorCode.CRYPTO_NOT_FOUND));

        return ResponseEntity.ok(ApiResponse.success(SearchCryptoResponse.of(crypto, market)));
    }

    @Override
    @GetMapping("/{ticker}/news")
    public ResponseEntity<ApiResponse<PageResponse<SearchNewsResponse>>> findNewsByTicker(
            @PathVariable("ticker") String ticker,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        if (ObjectUtils.isEmpty(ticker)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "코인 티커가 필요합니다.");
        }

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);

        PageResponse<SearchNewsResponse> newsList = searchCryptoNewsQuery.findNewsByTicker(ticker, safePage, safeSize);
        return ResponseEntity.ok(ApiResponse.success(newsList));
    }
}
