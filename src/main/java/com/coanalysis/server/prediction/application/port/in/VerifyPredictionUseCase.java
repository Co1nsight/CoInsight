package com.coanalysis.server.prediction.application.port.in;

import com.coanalysis.server.prediction.application.domain.PredictionVerification;
import com.coanalysis.server.prediction.application.enums.IntervalType;

import java.util.List;

public interface VerifyPredictionUseCase {

    List<PredictionVerification> verifyPendingPredictions(IntervalType intervalType);

    void verifyAllPendingPredictions();
}
