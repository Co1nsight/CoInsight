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

    /**
     * 특정 코인의 특정 시점 이후 가장 가까운 예측의 검증 결과 조회
     * 기사 발행 시점 이후의 예측 중 가장 빠른 검증 결과를 반환합니다.
     */
    @Query("""
        SELECT v FROM PredictionVerification v
        JOIN FETCH v.prediction p
        WHERE p.crypto.ticker = :ticker
        AND p.predictionTime >= :afterTime
        ORDER BY p.predictionTime ASC, v.intervalType ASC
        LIMIT 1
        """)
    PredictionVerification findFirstVerificationAfterTime(
            @Param("ticker") String ticker,
            @Param("afterTime") java.time.LocalDateTime afterTime);
}
