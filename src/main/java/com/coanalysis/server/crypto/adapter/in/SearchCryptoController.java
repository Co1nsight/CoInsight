package com.coanalysis.server.crypto.adapter.in;

import com.coanalysis.server.crypto.adapter.in.dto.SearchCryptoResponse;
import com.coanalysis.server.crypto.adapter.in.swagger.SearchCryptoControllerSwagger;
import com.coanalysis.server.crypto.application.domain.Crypto;
import com.coanalysis.server.crypto.application.port.in.SearchCryptoUsecase;
import com.coanalysis.server.infrastructure.exception.CustomException;
import com.coanalysis.server.infrastructure.exception.ErrorCode;
import com.coanalysis.server.infrastructure.response.ApiResponse;
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

    @Override
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SearchCryptoResponse>>> searchByKeyword(@RequestParam String keyword) {
        List<Crypto> cryptoList = searchCryptoUsecase.searchByKeyword(keyword);
        return ResponseEntity.ok(ApiResponse.success(SearchCryptoResponse.from(cryptoList)));
    }

    @Override
    @GetMapping("/{ticker}")
    public ResponseEntity<ApiResponse<SearchCryptoResponse>> findByTicker(@PathVariable(value = "ticker") String ticker) {
        if (ObjectUtils.isEmpty(ticker)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "코인 티커가 필요합니다.");
        }

        Crypto crypto = searchCryptoUsecase.findByTicker(ticker)
                .orElseThrow(() -> new CustomException(ErrorCode.CRYPTO_NOT_FOUND));

        return ResponseEntity.ok(ApiResponse.success(SearchCryptoResponse.of(crypto)));
    }
}
