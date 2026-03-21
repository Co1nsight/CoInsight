package com.coanalysis.server.infrastructure.repository;

import com.coanalysis.server.prediction.application.domain.CryptoPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CryptoPredictionRepository extends JpaRepository<CryptoPrediction, Long> {

    @Query("SELECT p FROM CryptoPrediction p WHERE p.crypto.ticker = :ticker ORDER BY p.predictionTime DESC")
    List<CryptoPrediction> findByTickerOrderByPredictionTimeDesc(@Param("ticker") String ticker);

    @Query("SELECT p FROM CryptoPrediction p WHERE p.crypto.ticker = :ticker ORDER BY p.predictionTime DESC LIMIT :limit OFFSET :offset")
    List<CryptoPrediction> findByTickerWithPaging(@Param("ticker") String ticker, @Param("offset") int offset, @Param("limit") int limit);

    @Query("SELECT COUNT(p) FROM CryptoPrediction p WHERE p.crypto.ticker = :ticker")
    long countByTicker(@Param("ticker") String ticker);

    @Query("SELECT p FROM CryptoPrediction p WHERE p.predictionTime <= :targetTime AND p.id NOT IN " +
           "(SELECT v.prediction.id FROM PredictionVerification v WHERE v.intervalType = :intervalType)")
    List<CryptoPrediction> findUnverifiedPredictions(@Param("targetTime") LocalDateTime targetTime,
                                                      @Param("intervalType") com.coanalysis.server.prediction.application.enums.IntervalType intervalType);

    @Query("SELECT MAX(p.predictionTime) FROM CryptoPrediction p WHERE p.crypto.ticker = :ticker")
    LocalDateTime findLastPredictionTimeByTicker(@Param("ticker") String ticker);

    @Query("SELECT p.priceAtPrediction FROM CryptoPrediction p WHERE p.crypto.ticker = :ticker ORDER BY p.predictionTime DESC LIMIT 1")
    Optional<Double> findLastPriceAtPredictionByTicker(@Param("ticker") String ticker);

    @Query("SELECT p FROM CryptoPrediction p WHERE p.crypto.ticker = :ticker ORDER BY p.predictionTime DESC LIMIT 1")
    Optional<CryptoPrediction> findLastPredictionByTicker(@Param("ticker") String ticker);

    @Query("SELECT p.predictionTime FROM CryptoPrediction p WHERE p.crypto.ticker = :ticker ORDER BY p.predictionTime DESC LIMIT 2")
    List<LocalDateTime> findLastTwoPredictionTimesByTicker(@Param("ticker") String ticker);
}
