package com.coanalysis.server.main.adapter.in;

import com.coanalysis.server.infrastructure.exception.CustomException;
import com.coanalysis.server.infrastructure.exception.ErrorCode;
import com.coanalysis.server.infrastructure.response.ApiResponse;
import com.coanalysis.server.infrastructure.response.PageResponse;
import com.coanalysis.server.main.adapter.in.dto.MainCryptoResponse;
import com.coanalysis.server.main.adapter.in.dto.MainNewsResponse;
import com.coanalysis.server.main.adapter.in.dto.UnifiedSearchResponse;
import com.coanalysis.server.main.adapter.in.swagger.MainControllerSwagger;
import com.coanalysis.server.main.adapter.out.MainCryptoQuery;
import com.coanalysis.server.main.adapter.out.MainNewsQuery;
import com.coanalysis.server.main.adapter.out.UnifiedSearchQuery;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/main")
@RequiredArgsConstructor
public class MainController implements MainControllerSwagger {

    private final MainNewsQuery mainNewsQuery;
    private final MainCryptoQuery mainCryptoQuery;
    private final UnifiedSearchQuery unifiedSearchQuery;

    @Override
    @GetMapping("/news")
    public ResponseEntity<ApiResponse<PageResponse<MainNewsResponse>>> getMainNews(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);

        PageResponse<MainNewsResponse> newsList = mainNewsQuery.findMainNews(safePage, safeSize);
        return ResponseEntity.ok(ApiResponse.success(newsList));
    }

    @Override
    @GetMapping("/cryptos")
    public ResponseEntity<ApiResponse<PageResponse<MainCryptoResponse>>> getCryptosByTradingVolume(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);

        PageResponse<MainCryptoResponse> cryptoList = mainCryptoQuery.findCryptosByTradingVolume(safePage, safeSize);
        return ResponseEntity.ok(ApiResponse.success(cryptoList));
    }

    @Override
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<UnifiedSearchResponse>> unifiedSearch(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "5") Integer cryptoLimit,
            @RequestParam(defaultValue = "5") Integer newsLimit) {
        if (ObjectUtils.isEmpty(keyword)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "검색 키워드가 필요합니다.");
        }

        int safeCryptoLimit = Math.min(Math.max(cryptoLimit, 1), 10);
        int safeNewsLimit = Math.min(Math.max(newsLimit, 1), 10);

        UnifiedSearchResponse result = unifiedSearchQuery.search(keyword, safeCryptoLimit, safeNewsLimit);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
