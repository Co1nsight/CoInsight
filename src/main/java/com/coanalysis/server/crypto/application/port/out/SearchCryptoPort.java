package com.coanalysis.server.crypto.application.port.out;

import com.coanalysis.server.crypto.application.domain.Crypto;

import java.util.List;

public interface SearchCryptoPort {
    List<Crypto> searchByKeyword(String keyword);
}
