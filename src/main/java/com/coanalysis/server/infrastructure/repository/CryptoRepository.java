package com.coanalysis.server.infrastructure.repository;

import com.coanalysis.server.crypto.application.domain.Crypto;
import com.coanalysis.server.infrastructure.repository.dsl.CryptoDslRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;


public interface CryptoRepository extends JpaRepository<Crypto, Long>, CryptoDslRepository {

    @Query("SELECT c FROM Crypto c WHERE c.name LIKE %:keyword% OR c.ticker LIKE %:keyword% ORDER BY c.tradingVolume DESC LIMIT 4")
    List<Crypto> searchByKeyword(@Param("keyword") String keyword);
}
