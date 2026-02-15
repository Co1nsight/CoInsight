package com.coanalysis.server.batch.application.port.out;

import com.coanalysis.server.batch.application.domain.CollectedNews;
import com.coanalysis.server.news.application.domain.News;
import java.util.List;

public interface SaveCollectedNewsPort {
    News save(CollectedNews collectedNews);
    List<News> saveAll(List<CollectedNews> collectedNewsList);
}
