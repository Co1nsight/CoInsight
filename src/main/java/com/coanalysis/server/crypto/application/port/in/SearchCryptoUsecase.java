package com.coanalysis.server.crypto.application.port.in;

import com.coanalysis.server.crypto.application.domain.Crypto;

import java.util.List;

public interface SearchCryptoUsecase {
    List<Crypto> searchByKeyword(String keyword);
}
