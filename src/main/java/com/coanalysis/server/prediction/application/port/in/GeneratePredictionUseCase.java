package com.coanalysis.server.prediction.application.port.in;

import com.coanalysis.server.prediction.application.domain.CryptoPrediction;

import java.util.List;

public interface GeneratePredictionUseCase {

    CryptoPrediction generatePrediction(String ticker);

    List<CryptoPrediction> generateAllPredictions();
}
