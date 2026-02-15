package com.coanalysis.server.crypto.application.port.out;

import java.util.List;

import com.coanalysis.server.crypto.application.domain.CryptoNews;

public interface RegisterCryptoNewsPort {

	CryptoNews save(CryptoNews cryptoNews);

	List<CryptoNews> saveAll(List<CryptoNews> cryptoNewsList);

}
