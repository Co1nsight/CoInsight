package com.coanalysis.server.infrastructure.repository;

import com.coanalysis.server.prediction.application.domain.PredictionVerification;
import com.coanalysis.server.prediction.application.enums.IntervalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PredictionVerificationRepository extends JpaRepository<PredictionVerification, Long> {

    List<PredictionVerification> findByPredictionId(Long predictionId);

    @Query("SELECT v FROM PredictionVerification v WHERE v.prediction.crypto.ticker = :ticker")
    List<PredictionVerification> findByTicker(@Param("ticker") String ticker);

    @Query("SELECT COUNT(v) FROM PredictionVerification v WHERE v.prediction.crypto.ticker = :ticker")
    long countByTicker(@Param("ticker") String ticker);

    @Query("SELECT COUNT(v) FROM PredictionVerification v WHERE v.prediction.crypto.ticker = :ticker AND v.isSuccess = true")
    long countSuccessByTicker(@Param("ticker") String ticker);

    @Query("SELECT COUNT(v) FROM PredictionVerification v WHERE v.prediction.crypto.ticker = :ticker AND v.intervalType = :intervalType")
    long countByTickerAndIntervalType(@Param("ticker") String ticker, @Param("intervalType") IntervalType intervalType);

    @Query("SELECT COUNT(v) FROM PredictionVerification v WHERE v.prediction.crypto.ticker = :ticker AND v.intervalType = :intervalType AND v.isSuccess = true")
    long countSuccessByTickerAndIntervalType(@Param("ticker") String ticker, @Param("intervalType") IntervalType intervalType);

    boolean existsByPredictionIdAndIntervalType(Long predictionId, IntervalType intervalType);
}
