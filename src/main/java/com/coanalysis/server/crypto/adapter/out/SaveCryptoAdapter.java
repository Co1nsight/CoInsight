package com.coanalysis.server.crypto.adapter.out;

import com.coanalysis.server.crypto.application.domain.Crypto;
import com.coanalysis.server.crypto.application.port.out.SaveCryptoPort;
import com.coanalysis.server.infrastructure.repository.CryptoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class SaveCryptoAdapter implements SaveCryptoPort {

    private final CryptoRepository cryptoRepository;

    @Override
    public List<Crypto> saveAll(List<Crypto> cryptos) {
        return cryptoRepository.saveAll(cryptos);
    }

    @Override
    public Set<String> findAllTickers() {
        return cryptoRepository.findAllTickers();
    }

    @Override
    public List<Crypto> findAllWithoutLogoUrl() {
        return cryptoRepository.findAllWithoutLogoUrl();
    }

    @Override
    public void updateLogoUrl(String ticker, String logoUrl) {
        cryptoRepository.updateLogoUrl(ticker, logoUrl);
    }

    @Override
    public List<Crypto> findAll() {
        return cryptoRepository.findAll();
    }
}
