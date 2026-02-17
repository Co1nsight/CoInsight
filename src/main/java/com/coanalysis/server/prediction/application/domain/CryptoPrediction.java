package com.coanalysis.server.prediction.application.domain;

import com.coanalysis.server.crypto.application.domain.Crypto;
import com.coanalysis.server.prediction.application.enums.PredictionLabel;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "CRYPTO_PREDICTION")
@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class CryptoPrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crypto_ticker", nullable = false)
    private Crypto crypto;

    @Column(name = "PREDICTION_DATE", nullable = false)
    private LocalDate predictionDate;

    @Column(name = "PREDICTION_TIME", nullable = false)
    private LocalDateTime predictionTime;

    @Column(name = "POSITIVE_COUNT", nullable = false)
    private Integer positiveCount;

    @Column(name = "NEGATIVE_COUNT", nullable = false)
    private Integer negativeCount;

    @Column(name = "NEUTRAL_COUNT", nullable = false)
    private Integer neutralCount;

    @Column(name = "POSITIVE_RATIO", nullable = false)
    private Double positiveRatio;

    @Enumerated(EnumType.STRING)
    @Column(name = "PREDICTION_LABEL", nullable = false, length = 10)
    private PredictionLabel predictionLabel;

    @Column(name = "PRICE_AT_PREDICTION", nullable = false)
    private Double priceAtPrediction;

    @CreatedDate
    @Column(name = "CREATED_AT", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    public int getTotalNewsCount() {
        return positiveCount + negativeCount + neutralCount;
    }
}
