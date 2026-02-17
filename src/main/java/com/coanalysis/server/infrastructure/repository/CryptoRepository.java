package com.coanalysis.server.infrastructure.repository;

import com.coanalysis.server.crypto.application.domain.Crypto;
import com.coanalysis.server.infrastructure.repository.dsl.CryptoDslRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;


public interface CryptoRepository extends JpaRepository<Crypto, String>, CryptoDslRepository {

    @Query("SELECT c FROM Crypto c WHERE c.name LIKE %:keyword% OR c.ticker LIKE %:keyword% ORDER BY c.tradingVolume DESC LIMIT 4")
    List<Crypto> searchByKeyword(@Param("keyword") String keyword);

    List<Crypto> findByTickerIn(Collection<String> tickers);

    @Query("SELECT c.ticker FROM Crypto c")
    Set<String> findAllTickers();

    @Modifying
    @Query("UPDATE Crypto c SET c.logoUrl = :logoUrl WHERE c.ticker = :ticker")
    void updateLogoUrl(@Param("ticker") String ticker, @Param("logoUrl") String logoUrl);

    @Query("SELECT c FROM Crypto c WHERE c.logoUrl IS NULL")
    List<Crypto> findAllWithoutLogoUrl();

}
