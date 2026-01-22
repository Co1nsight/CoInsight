package com.coanalysis.server.crypto.application.port.out;

import com.coanalysis.server.crypto.application.domain.Crypto;

import java.util.List;
import java.util.Optional;

public interface SearchCryptoPort {
    List<Crypto> searchByKeyword(String keyword);
    Optional<Crypto> findById(Long id);
}
