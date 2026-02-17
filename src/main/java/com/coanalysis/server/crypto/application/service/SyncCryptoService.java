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

    private Crypto toCrypto(BithumbMarketDto market) {
        return Crypto.builder()
                .ticker(market.getSymbol())
                .name(market.getKoreanName())
                .englishName(market.getEnglishName())
                .logoUrl(null)  // 로고 URL은 별도 API에서 가져오거나 null로 유지
                .currentPrice(0)
                .tradingVolume(0)
                .build();
    }
}
