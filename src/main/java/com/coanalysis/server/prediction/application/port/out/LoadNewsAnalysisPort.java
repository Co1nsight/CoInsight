package com.coanalysis.server.prediction.application.port.out;

import java.time.LocalDateTime;
import java.util.Map;

public interface LoadNewsAnalysisPort {

    Map<String, Integer> countNewsBySentiment(String ticker, LocalDateTime from, LocalDateTime to);
}
