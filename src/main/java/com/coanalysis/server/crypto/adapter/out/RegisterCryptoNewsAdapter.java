package com.coanalysis.server.crypto.adapter.out;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.coanalysis.server.crypto.application.domain.CryptoNews;
import com.coanalysis.server.crypto.application.port.out.RegisterCryptoNewsPort;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RegisterCryptoNewsAdapter implements RegisterCryptoNewsPort {
	@Override
	public CryptoNews save(CryptoNews cryptoNews) {
		return null;
	}

	@Override
	public List<CryptoNews> saveAll(List<CryptoNews> cryptoNewsList) {
		return List.of();
	}
}
