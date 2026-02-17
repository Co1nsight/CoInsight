package com.coanalysis.server.crypto.adapter.out;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.coanalysis.server.crypto.application.domain.CryptoNews;
import com.coanalysis.server.crypto.application.port.out.RegisterCryptoNewsPort;
import com.coanalysis.server.infrastructure.repository.CryptoNewsRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RegisterCryptoNewsAdapter implements RegisterCryptoNewsPort {

	private final CryptoNewsRepository repository;

	@Override
	public CryptoNews save(CryptoNews cryptoNews) {
		return repository.save(cryptoNews);
	}

	@Override
	public List<CryptoNews> saveAll(List<CryptoNews> cryptoNewsList) {
		return repository.saveAll(cryptoNewsList);
	}

}
