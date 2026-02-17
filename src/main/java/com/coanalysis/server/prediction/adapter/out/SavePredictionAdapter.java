package com.coanalysis.server.prediction.adapter.out;

import com.coanalysis.server.infrastructure.repository.CryptoPredictionRepository;
import com.coanalysis.server.infrastructure.repository.PredictionVerificationRepository;
import com.coanalysis.server.prediction.application.domain.CryptoPrediction;
import com.coanalysis.server.prediction.application.domain.PredictionVerification;
import com.coanalysis.server.prediction.application.port.out.SavePredictionPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SavePredictionAdapter implements SavePredictionPort {

    private final CryptoPredictionRepository predictionRepository;
    private final PredictionVerificationRepository verificationRepository;

    @Override
    public CryptoPrediction savePrediction(CryptoPrediction prediction) {
        log.debug("Saving prediction for crypto: {}", prediction.getCrypto().getTicker());
        return predictionRepository.save(prediction);
    }

    @Override
    public PredictionVerification saveVerification(PredictionVerification verification) {
        log.debug("Saving verification for prediction: {}, interval: {}",
                verification.getPrediction().getId(), verification.getIntervalType());
        return verificationRepository.save(verification);
    }
}
