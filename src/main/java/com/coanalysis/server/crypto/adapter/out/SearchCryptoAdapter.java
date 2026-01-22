package com.coanalysis.server.crypto.adapter.out;

import com.coanalysis.server.crypto.application.domain.Crypto;
import com.coanalysis.server.crypto.application.port.out.SearchCryptoPort;
import com.coanalysis.server.infrastructure.repository.CryptoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SearchCryptoAdapter implements SearchCryptoPort {
    private final CryptoRepository cryptoRepository;

    @Override
    public List<Crypto> searchByKeyword(String keyword) {
        return cryptoRepository.searchByKeyword(keyword);
    }
}
