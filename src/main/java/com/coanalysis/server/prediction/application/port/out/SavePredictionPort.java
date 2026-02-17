package com.coanalysis.server.prediction.application.port.out;

import com.coanalysis.server.prediction.application.domain.CryptoPrediction;
import com.coanalysis.server.prediction.application.domain.PredictionVerification;

public interface SavePredictionPort {

    CryptoPrediction savePrediction(CryptoPrediction prediction);

    PredictionVerification saveVerification(PredictionVerification verification);
}
