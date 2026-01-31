package com.coanalysis.server.infrastructure.config;

import com.coanalysis.server.crypto.application.domain.Crypto;
import com.coanalysis.server.crypto.application.domain.CryptoNews;
import com.coanalysis.server.infrastructure.repository.CryptoNewsRepository;
import com.coanalysis.server.infrastructure.repository.CryptoRepository;
import com.coanalysis.server.infrastructure.repository.NewsAnalysisRepository;
import com.coanalysis.server.infrastructure.repository.NewsRepository;
import com.coanalysis.server.news.application.domain.News;
import com.coanalysis.server.news.application.domain.NewsAnalysis;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CryptoRepository cryptoRepository;
    private final NewsRepository newsRepository;
    private final NewsAnalysisRepository newsAnalysisRepository;
    private final CryptoNewsRepository cryptoNewsRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Initializing mock data...");

        List<Crypto> cryptos = initCryptos();
        List<News> newsList = initNews();
        initNewsAnalysis(newsList);
        initCryptoNews(cryptos, newsList);

        log.info("Mock data initialization completed!");
    }

    private List<Crypto> initCryptos() {
        List<Crypto> cryptos = List.of(
                Crypto.builder()
                        .ticker("BTC")
                        .name("비트코인")
                        .logoUrl("https://cryptologos.cc/logos/bitcoin-btc-logo.png")
                        .currentPrice(145000000)
                        .tradingVolume(850000000000.0)
                        .build(),
                Crypto.builder()
                        .ticker("ETH")
                        .name("이더리움")
                        .logoUrl("https://cryptologos.cc/logos/ethereum-eth-logo.png")
                        .currentPrice(4800000)
                        .tradingVolume(320000000000.0)
                        .build(),
                Crypto.builder()
                        .ticker("XRP")
                        .name("리플")
                        .logoUrl("https://cryptologos.cc/logos/xrp-xrp-logo.png")
                        .currentPrice(3200)
                        .tradingVolume(180000000000.0)
                        .build(),
                Crypto.builder()
                        .ticker("SOL")
                        .name("솔라나")
                        .logoUrl("https://cryptologos.cc/logos/solana-sol-logo.png")
                        .currentPrice(320000)
                        .tradingVolume(95000000000.0)
                        .build(),
                Crypto.builder()
                        .ticker("ADA")
                        .name("에이다")
                        .logoUrl("https://cryptologos.cc/logos/cardano-ada-logo.png")
                        .currentPrice(1200)
                        .tradingVolume(45000000000.0)
                        .build(),
                Crypto.builder()
                        .ticker("DOGE")
                        .name("도지코인")
                        .logoUrl("https://cryptologos.cc/logos/dogecoin-doge-logo.png")
                        .currentPrice(450)
                        .tradingVolume(38000000000.0)
                        .build(),
                Crypto.builder()
                        .ticker("AVAX")
                        .name("아발란체")
                        .logoUrl("https://cryptologos.cc/logos/avalanche-avax-logo.png")
                        .currentPrice(52000)
                        .tradingVolume(28000000000.0)
                        .build(),
                Crypto.builder()
                        .ticker("MATIC")
                        .name("폴리곤")
                        .logoUrl("https://cryptologos.cc/logos/polygon-matic-logo.png")
                        .currentPrice(1100)
                        .tradingVolume(22000000000.0)
                        .build()
        );

        return cryptoRepository.saveAll(cryptos);
    }

    private List<News> initNews() {
        LocalDateTime now = LocalDateTime.now();

        List<News> newsList = List.of(
                News.builder()
                        .title("비트코인, 사상 최고가 경신... 1억 5천만원 돌파")
                        .originalLink("https://example.com/news/1")
                        .publisher("코인데스크코리아")
                        .publishedAt(now.minusHours(1))
                        .content("비트코인이 글로벌 기관 투자자들의 대규모 매수세에 힘입어 사상 최고가를 경신했다. 전문가들은 ETF 승인 이후 지속적인 자금 유입이 이어지고 있다고 분석했다.")
                        .build(),
                News.builder()
                        .title("이더리움 2.0 업그레이드 완료, 네트워크 성능 대폭 향상")
                        .originalLink("https://example.com/news/2")
                        .publisher("블록미디어")
                        .publishedAt(now.minusHours(3))
                        .content("이더리움 재단이 예정된 네트워크 업그레이드를 성공적으로 완료했다고 발표했다. 이번 업그레이드로 가스비가 크게 절감되고 처리 속도가 향상될 것으로 기대된다.")
                        .build(),
                News.builder()
                        .title("리플, SEC 소송 최종 승소... XRP 가격 급등")
                        .originalLink("https://example.com/news/3")
                        .publisher("한국경제")
                        .publishedAt(now.minusHours(5))
                        .content("리플랩스가 미국 증권거래위원회(SEC)와의 오랜 법적 분쟁에서 최종 승소했다. 이 소식에 XRP 가격이 24시간 만에 30% 이상 급등했다.")
                        .build(),
                News.builder()
                        .title("솔라나 생태계 TVL 100억 달러 돌파")
                        .originalLink("https://example.com/news/4")
                        .publisher("디센터")
                        .publishedAt(now.minusHours(8))
                        .content("솔라나 블록체인의 디파이 생태계 총 예치금(TVL)이 100억 달러를 돌파했다. 빠른 처리 속도와 낮은 수수료가 개발자들을 끌어들이고 있다.")
                        .build(),
                News.builder()
                        .title("도지코인, 일론 머스크 트윗에 20% 급등")
                        .originalLink("https://example.com/news/5")
                        .publisher("조선비즈")
                        .publishedAt(now.minusHours(12))
                        .content("테슬라 CEO 일론 머스크가 도지코인 관련 트윗을 올리자 가격이 급등했다. 밈코인 시장이 다시 활기를 띠고 있다는 분석이 나온다.")
                        .build(),
                News.builder()
                        .title("글로벌 암호화폐 규제 명확화... 기관 투자 확대 전망")
                        .originalLink("https://example.com/news/6")
                        .publisher("매일경제")
                        .publishedAt(now.minusHours(18))
                        .content("주요 국가들이 암호화폐 규제 프레임워크를 발표하면서 시장의 불확실성이 줄어들고 있다. 전문가들은 이로 인해 기관 투자가 더욱 확대될 것으로 전망했다.")
                        .build(),
                News.builder()
                        .title("국내 거래소 거래량 역대 최고 기록")
                        .originalLink("https://example.com/news/7")
                        .publisher("서울경제")
                        .publishedAt(now.minusDays(1))
                        .content("국내 주요 암호화폐 거래소들의 일일 거래량이 역대 최고치를 기록했다. 개인 투자자들의 참여가 크게 늘어난 것으로 분석된다.")
                        .build(),
                News.builder()
                        .title("아발란체, 대형 게임사와 파트너십 체결")
                        .originalLink("https://example.com/news/8")
                        .publisher("게임메카")
                        .publishedAt(now.minusDays(1).minusHours(6))
                        .content("아발란체 재단이 글로벌 대형 게임사와 블록체인 게임 개발을 위한 전략적 파트너십을 체결했다고 발표했다.")
                        .build(),
                News.builder()
                        .title("폴리곤, 이더리움 레이어2 시장 점유율 1위 탈환")
                        .originalLink("https://example.com/news/9")
                        .publisher("코인니스")
                        .publishedAt(now.minusDays(2))
                        .content("폴리곤이 이더리움 레이어2 솔루션 시장에서 다시 점유율 1위를 차지했다. 최근 zkEVM 출시가 긍정적인 영향을 미쳤다는 분석이다.")
                        .build(),
                News.builder()
                        .title("비트코인 반감기 앞두고 채굴업체 주가 급등")
                        .originalLink("https://example.com/news/10")
                        .publisher("블룸버그코리아")
                        .publishedAt(now.minusDays(3))
                        .content("비트코인 반감기가 다가오면서 주요 채굴 업체들의 주가가 급등하고 있다. 공급 감소에 대한 기대감이 시장에 반영되고 있다.")
                        .build()
        );

        return newsRepository.saveAll(newsList);
    }

    private void initNewsAnalysis(List<News> newsList) {
        List<NewsAnalysis> analyses = List.of(
                NewsAnalysis.builder()
                        .news(newsList.get(0))
                        .summary("비트코인이 기관 투자자 매수세로 사상 최고가를 경신했으며, ETF 승인 후 자금 유입이 지속되고 있음")
                        .sentimentScore(0.92)
                        .sentimentLabel("POSITIVE")
                        .build(),
                NewsAnalysis.builder()
                        .news(newsList.get(1))
                        .summary("이더리움 2.0 업그레이드가 성공적으로 완료되어 가스비 절감과 처리 속도 향상이 예상됨")
                        .sentimentScore(0.85)
                        .sentimentLabel("POSITIVE")
                        .build(),
                NewsAnalysis.builder()
                        .news(newsList.get(2))
                        .summary("리플이 SEC 소송에서 최종 승소하여 XRP 가격이 30% 이상 급등함")
                        .sentimentScore(0.95)
                        .sentimentLabel("POSITIVE")
                        .build(),
                NewsAnalysis.builder()
                        .news(newsList.get(3))
                        .summary("솔라나 디파이 생태계 TVL이 100억 달러를 돌파하며 성장세를 보임")
                        .sentimentScore(0.78)
                        .sentimentLabel("POSITIVE")
                        .build(),
                NewsAnalysis.builder()
                        .news(newsList.get(4))
                        .summary("일론 머스크 트윗으로 도지코인이 20% 급등, 밈코인 시장 활성화")
                        .sentimentScore(0.45)
                        .sentimentLabel("NEUTRAL")
                        .build(),
                NewsAnalysis.builder()
                        .news(newsList.get(5))
                        .summary("글로벌 암호화폐 규제 명확화로 기관 투자 확대가 전망됨")
                        .sentimentScore(0.72)
                        .sentimentLabel("POSITIVE")
                        .build(),
                NewsAnalysis.builder()
                        .news(newsList.get(6))
                        .summary("국내 거래소 거래량이 역대 최고치를 기록, 개인 투자자 참여 증가")
                        .sentimentScore(0.65)
                        .sentimentLabel("POSITIVE")
                        .build(),
                NewsAnalysis.builder()
                        .news(newsList.get(7))
                        .summary("아발란체가 대형 게임사와 블록체인 게임 개발 파트너십을 체결함")
                        .sentimentScore(0.80)
                        .sentimentLabel("POSITIVE")
                        .build(),
                NewsAnalysis.builder()
                        .news(newsList.get(8))
                        .summary("폴리곤이 zkEVM 출시로 이더리움 레이어2 시장 점유율 1위를 탈환함")
                        .sentimentScore(0.75)
                        .sentimentLabel("POSITIVE")
                        .build(),
                NewsAnalysis.builder()
                        .news(newsList.get(9))
                        .summary("비트코인 반감기 기대감으로 채굴업체 주가가 급등하고 있음")
                        .sentimentScore(0.68)
                        .sentimentLabel("POSITIVE")
                        .build()
        );

        newsAnalysisRepository.saveAll(analyses);
    }

    private void initCryptoNews(List<Crypto> cryptos, List<News> newsList) {
        Crypto btc = cryptos.stream().filter(c -> "BTC".equals(c.getTicker())).findFirst().orElseThrow();
        Crypto eth = cryptos.stream().filter(c -> "ETH".equals(c.getTicker())).findFirst().orElseThrow();
        Crypto xrp = cryptos.stream().filter(c -> "XRP".equals(c.getTicker())).findFirst().orElseThrow();
        Crypto sol = cryptos.stream().filter(c -> "SOL".equals(c.getTicker())).findFirst().orElseThrow();
        Crypto doge = cryptos.stream().filter(c -> "DOGE".equals(c.getTicker())).findFirst().orElseThrow();
        Crypto avax = cryptos.stream().filter(c -> "AVAX".equals(c.getTicker())).findFirst().orElseThrow();
        Crypto matic = cryptos.stream().filter(c -> "MATIC".equals(c.getTicker())).findFirst().orElseThrow();

        List<CryptoNews> cryptoNewsList = List.of(
                CryptoNews.builder().crypto(btc).news(newsList.get(0)).build(),
                CryptoNews.builder().crypto(eth).news(newsList.get(1)).build(),
                CryptoNews.builder().crypto(xrp).news(newsList.get(2)).build(),
                CryptoNews.builder().crypto(sol).news(newsList.get(3)).build(),
                CryptoNews.builder().crypto(doge).news(newsList.get(4)).build(),
                CryptoNews.builder().crypto(btc).news(newsList.get(5)).build(),
                CryptoNews.builder().crypto(eth).news(newsList.get(5)).build(),
                CryptoNews.builder().crypto(btc).news(newsList.get(6)).build(),
                CryptoNews.builder().crypto(eth).news(newsList.get(6)).build(),
                CryptoNews.builder().crypto(xrp).news(newsList.get(6)).build(),
                CryptoNews.builder().crypto(avax).news(newsList.get(7)).build(),
                CryptoNews.builder().crypto(matic).news(newsList.get(8)).build(),
                CryptoNews.builder().crypto(eth).news(newsList.get(8)).build(),
                CryptoNews.builder().crypto(btc).news(newsList.get(9)).build()
        );

        cryptoNewsRepository.saveAll(cryptoNewsList);
    }
}
