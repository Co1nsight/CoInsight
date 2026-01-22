package com.coanalysis.server.crypto.adapter.in;

import com.coanalysis.server.crypto.adapter.in.dto.SearchCryptoResponse;
import com.coanalysis.server.crypto.adapter.in.swagger.SearchCryptoControllerSwagger;
import com.coanalysis.server.crypto.application.domain.Crypto;
import com.coanalysis.server.crypto.application.port.in.SearchCryptoUsecase;
import com.coanalysis.server.news.application.port.in.SearchNewsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/crypto")
@RequiredArgsConstructor
public class SearchCryptoController implements SearchCryptoControllerSwagger {

    private final SearchCryptoUsecase searchCryptoUsecase;

    @GetMapping("/search")
    public ResponseEntity<List<SearchCryptoResponse>> searchByKeyword(@RequestParam String keyword) {
        List<Crypto> cryptoList = searchCryptoUsecase.searchByKeyword(keyword);

        return ResponseEntity.ok(SearchCryptoResponse.from(cryptoList));
    }
}
