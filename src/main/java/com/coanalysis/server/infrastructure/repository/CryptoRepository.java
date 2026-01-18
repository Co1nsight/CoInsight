package com.coanalysis.server.infrastructure.repository;

import com.coanalysis.server.crypto.application.domain.Crypto;
import com.coanalysis.server.infrastructure.repository.dsl.CryptoDslRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CryptoRepository extends JpaRepository<Crypto, Long>, CryptoDslRepository {
}
