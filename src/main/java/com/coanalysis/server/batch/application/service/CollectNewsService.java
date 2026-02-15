package com.coanalysis.server.batch.application.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coanalysis.server.batch.application.domain.CollectedNews;
import com.coanalysis.server.batch.application.port.in.CollectNewsUseCase;
import com.coanalysis.server.batch.application.port.out.FetchCryptoNewsPort;
import com.coanalysis.server.batch.application.port.out.FindDuplicateNewsPort;
import com.coanalysis.server.batch.application.port.out.MapCryptoNewsPort;
import com.coanalysis.server.batch.application.port.out.SaveCollectedNewsPort;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CollectNewsService implements CollectNewsUseCase {

	private final FetchCryptoNewsPort fetchCryptoNewsPort;

	private final FindDuplicateNewsPort findDuplicateNewsPort;

	private final SaveCollectedNewsPort saveCollectedNewsPort;

	@Override
	public int collectAndProcessNews() {
		// 1. 외부 API에서 뉴스 수집
		List<CollectedNews> collectedNewsList = fetchCryptoNewsPort.fetchLatestNews();

		// 2. 수집된 뉴스에서 중복 링크 추출
		Set<String> links = collectedNewsList.stream()
				.map(CollectedNews::originalLink)
				.collect(Collectors.toSet());

		// 3. 중복 링크 확인
		Set<String> existingLinks = findDuplicateNewsPort.findExistingLinks(links);

		// 4. 중복되지 않은 뉴스 필터링
		List<CollectedNews> uniqueNews = collectedNewsList.stream()
				.filter(news -> !existingLinks.contains(news.originalLink()))
				.collect(Collectors.toList());

		// 5. 중복되지 않은 뉴스 저장
		if (!uniqueNews.isEmpty()) {
			saveCollectedNewsPort.saveAll(uniqueNews);
			return uniqueNews.size();
		}

		return 0;
	}
}
