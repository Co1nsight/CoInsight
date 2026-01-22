package com.coanalysis.server.crypto.adapter.in;

import com.coanalysis.server.crypto.adapter.in.dto.SearchCryptoResponse;
import com.coanalysis.server.crypto.adapter.in.swagger.SearchCryptoControllerSwagger;
import com.coanalysis.server.crypto.application.domain.Crypto;
import com.coanalysis.server.crypto.application.port.in.SearchCryptoUsecase;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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


    @GetMapping("/{id}")
    public ResponseEntity<SearchCryptoResponse> findById(@PathVariable(value = "id") Long id) {

        if (ObjectUtils.isEmpty(id)) {
            return ResponseEntity.ok(null);
        }

        Optional<Crypto> cryptoIf = searchCryptoUsecase.findById(id);

        if (cryptoIf.isEmpty()) {
            return ResponseEntity.ok(null);
        }

        return ResponseEntity.ok(SearchCryptoResponse.of(cryptoIf.get()));
    }
}
