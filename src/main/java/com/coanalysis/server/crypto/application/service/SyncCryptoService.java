package com.coanalysis.server.crypto.application.service;

import com.coanalysis.server.crypto.application.domain.Crypto;
import com.coanalysis.server.crypto.application.port.in.SyncCryptoUsecase;
import com.coanalysis.server.crypto.application.port.out.SaveCryptoPort;
import com.coanalysis.server.market.adapter.out.BithumbClient;
import com.coanalysis.server.market.adapter.out.dto.BithumbMarketDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyncCryptoService implements SyncCryptoUsecase {

    private final BithumbClient bithumbClient;
    private final SaveCryptoPort saveCryptoPort;

    @Override
    @Transactional
    public int syncCryptos() {
        log.info("코인 목록 동기화 시작");

        // 0. 기존 코인 중 로고 URL이 없는 코인들 업데이트
        updateMissingLogoUrls();

        // 1. Bithumb API에서 전체 마켓 정보 조회
        List<BithumbMarketDto> markets = bithumbClient.getMarkets();
        log.info("Bithumb API에서 {} 개의 마켓 정보 조회됨", markets.size());

        // 2. KRW 마켓만 필터링 (한국 원화 거래 코인만)
        List<BithumbMarketDto> krwMarkets = markets.stream()
                .filter(m -> "KRW".equals(m.getMarketType()))
                .collect(Collectors.toList());
        log.info("KRW 마켓: {} 개", krwMarkets.size());

        // 3. 기존 DB에 있는 ticker 조회
        Set<String> existingTickers = saveCryptoPort.findAllTickers();
        log.info("기존 DB의 코인 수: {}", existingTickers.size());

        // 4. 새로운 코인만 필터링 (기존에 없는 것만)
        List<Crypto> newCryptos = krwMarkets.stream()
                .filter(m -> !existingTickers.contains(m.getSymbol()))
                .map(this::toCrypto)
                .collect(Collectors.toList());

        if (newCryptos.isEmpty()) {
            log.info("새로 추가할 코인이 없습니다");
            return 0;
        }

        // 5. 새로운 코인 저장
        List<Crypto> savedCryptos = saveCryptoPort.saveAll(newCryptos);
        log.info("새로 추가된 코인 수: {}", savedCryptos.size());

        for (Crypto crypto : savedCryptos) {
            log.debug("추가된 코인: {} ({})", crypto.getName(), crypto.getTicker());
        }

        return savedCryptos.size();
    }

    /**
     * 로고 URL이 없는 기존 코인들의 로고 URL을 업데이트합니다.
     */
    private void updateMissingLogoUrls() {
        List<Crypto> cryptosWithoutLogo = saveCryptoPort.findAllWithoutLogoUrl();
        if (cryptosWithoutLogo.isEmpty()) {
            return;
        }

        log.info("로고 URL 업데이트가 필요한 코인 수: {}", cryptosWithoutLogo.size());
        for (Crypto crypto : cryptosWithoutLogo) {
            String logoUrl = generateLogoUrl(crypto.getTicker());
            saveCryptoPort.updateLogoUrl(crypto.getTicker(), logoUrl);
            log.debug("로고 URL 업데이트: {} -> {}", crypto.getTicker(), logoUrl);
        }
        log.info("로고 URL 업데이트 완료: {} 개", cryptosWithoutLogo.size());
    }

    /**
     * 모든 코인의 로고 URL을 새 URL 패턴으로 업데이트합니다.
     */
    @Override
    @Transactional
    public int updateAllLogoUrls() {
        List<Crypto> allCryptos = saveCryptoPort.findAll();
        log.info("모든 코인 로고 URL 업데이트 시작: {} 개", allCryptos.size());

        int updatedCount = 0;
        for (Crypto crypto : allCryptos) {
            String newLogoUrl = generateLogoUrl(crypto.getTicker());
            saveCryptoPort.updateLogoUrl(crypto.getTicker(), newLogoUrl);
            updatedCount++;
            log.debug("로고 URL 업데이트: {} -> {}", crypto.getTicker(), newLogoUrl);
        }

        log.info("모든 코인 로고 URL 업데이트 완료: {} 개", updatedCount);
        return updatedCount;
    }

    private Crypto toCrypto(BithumbMarketDto market) {
        return Crypto.builder()
                .ticker(market.getSymbol())
                .name(market.getKoreanName())
                .englishName(market.getEnglishName())
                .logoUrl(generateLogoUrl(market.getSymbol()))
                .currentPrice(0)
                .tradingVolume(0)
                .build();
    }

    /**
     * cryptocurrency-icons GitHub 저장소 기반 로고 URL 생성
     * JSDelivr CDN을 통해 제공되어 안정적임
     * https://cdn.jsdelivr.net/gh/spothq/cryptocurrency-icons@master/128/color/{symbol}.png
     */
    private String generateLogoUrl(String symbol) {
        if (symbol == null || symbol.isBlank()) {
            return null;
        }
        return String.format("https://cdn.jsdelivr.net/gh/spothq/cryptocurrency-icons@master/128/color/%s.png", symbol.toLowerCase());
    }
}
