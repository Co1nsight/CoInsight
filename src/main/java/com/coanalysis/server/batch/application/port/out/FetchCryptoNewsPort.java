package com.coanalysis.server.batch.application.port.out;

import com.coanalysis.server.batch.application.domain.CollectedNews;
import java.util.List;

public interface FetchCryptoNewsPort {
    List<CollectedNews> fetchLatestNews();
}
