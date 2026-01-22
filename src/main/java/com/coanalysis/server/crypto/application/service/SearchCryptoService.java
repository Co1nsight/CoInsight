package com.coanalysis.server.crypto.application.service;

import com.coanalysis.server.crypto.application.domain.Crypto;
import com.coanalysis.server.crypto.application.port.in.SearchCryptoUsecase;
import com.coanalysis.server.crypto.application.port.out.SearchCryptoPort;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SearchCryptoService implements SearchCryptoUsecase {
    private final SearchCryptoPort searchCryptoPort;

    @Override
    public List<Crypto> searchByKeyword(String keyword) {
        return searchCryptoPort.searchByKeyword(keyword);
    }

    @Override
    public Optional<Crypto> findById(Long id) {
        return searchCryptoPort.findById(id);
    }
}
