package com.coanalysis.server.prediction.application.domain;

import com.coanalysis.server.prediction.application.enums.IntervalType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Table(name = "PREDICTION_VERIFICATION")
@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class PredictionVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prediction_id", nullable = false)
    private CryptoPrediction prediction;

    @Enumerated(EnumType.STRING)
    @Column(name = "INTERVAL_TYPE", nullable = false, length = 10)
    private IntervalType intervalType;

    @Column(name = "VERIFIED_AT", nullable = false)
    private LocalDateTime verifiedAt;

    @Column(name = "PRICE_AT_VERIFICATION", nullable = false)
    private Double priceAtVerification;

    @Column(name = "PRICE_CHANGE_PERCENT", nullable = false)
    private Double priceChangePercent;

    @Column(name = "IS_SUCCESS", nullable = false)
    private Boolean isSuccess;

    @CreatedDate
    @Column(name = "CREATED_AT", updatable = false, nullable = false)
    private LocalDateTime createdAt;
}
