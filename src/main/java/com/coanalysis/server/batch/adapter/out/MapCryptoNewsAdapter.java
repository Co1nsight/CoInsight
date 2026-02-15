package com.coanalysis.server.batch.adapter.out;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.coanalysis.server.batch.application.port.out.MapCryptoNewsPort;
import com.coanalysis.server.crypto.application.domain.Crypto;
import com.coanalysis.server.crypto.application.domain.CryptoNews;
import com.coanalysis.server.crypto.application.port.out.RegisterCryptoNewsPort;
import com.coanalysis.server.crypto.application.port.out.SearchCryptoPort;
import com.coanalysis.server.news.application.domain.News;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MapCryptoNewsAdapter implements MapCryptoNewsPort {

	private final SearchCryptoPort searchCryptoPort;

	private final RegisterCryptoNewsPort registerCryptoNewsPort;

	@Override
	public void mapNewsToCoins(News news, Set<String> coinTickers) {
		Map<String, Crypto> tickerToCryptoMap = searchCryptoPort.findAllByTickers(coinTickers).stream()
			.filter(v->v.getTicker() != null)
				.collect(Collectors.toMap(Crypto::getTicker, crypto -> crypto));

		List<CryptoNews> mappedNews = coinTickers.stream()
				.map(ticker -> CryptoNews.builder()
					.crypto(tickerToCryptoMap.get(ticker))
					.news(news)
					.build())
			.filter(v->v.getCrypto() != null)
				.collect(Collectors.toList());

		registerCryptoNewsPort.saveAll(mappedNews);
	}

	@Override
	public Set<String> getAllKnownTickers() {
		return searchCryptoPort.findAllTickers();
	}
}
