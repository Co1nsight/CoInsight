package com.coanalysis.server.batch.application.service;

import com.coanalysis.server.crypto.application.domain.Crypto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 뉴스 텍스트에서 암호화폐 키워드를 매칭하는 컴포넌트
 * 다양한 패턴과 별명을 사용하여 매칭률을 높입니다.
 */
@Slf4j
@Component
public class CryptoKeywordMatcher {

    // 주요 코인 별명/약칭 매핑 (소문자로 저장)
    private static final Map<String, String> COIN_ALIASES = new HashMap<>();

    static {
        // BTC 별명
        COIN_ALIASES.put("비트", "BTC");
        COIN_ALIASES.put("비트코인", "BTC");
        COIN_ALIASES.put("bitcoin", "BTC");
        COIN_ALIASES.put("btc", "BTC");
        COIN_ALIASES.put("비코", "BTC");
        COIN_ALIASES.put("빗코", "BTC");

        // ETH 별명
        COIN_ALIASES.put("이더", "ETH");
        COIN_ALIASES.put("이더리움", "ETH");
        COIN_ALIASES.put("ethereum", "ETH");
        COIN_ALIASES.put("eth", "ETH");
        COIN_ALIASES.put("이리움", "ETH");
        COIN_ALIASES.put("ether", "ETH");

        // XRP 별명
        COIN_ALIASES.put("리플", "XRP");
        COIN_ALIASES.put("ripple", "XRP");
        COIN_ALIASES.put("xrp", "XRP");

        // SOL 별명
        COIN_ALIASES.put("솔라나", "SOL");
        COIN_ALIASES.put("solana", "SOL");
        COIN_ALIASES.put("sol", "SOL");

        // DOGE 별명
        COIN_ALIASES.put("도지", "DOGE");
        COIN_ALIASES.put("도지코인", "DOGE");
        COIN_ALIASES.put("dogecoin", "DOGE");
        COIN_ALIASES.put("doge", "DOGE");
        COIN_ALIASES.put("시바", "DOGE");

        // ADA 별명
        COIN_ALIASES.put("에이다", "ADA");
        COIN_ALIASES.put("카르다노", "ADA");
        COIN_ALIASES.put("cardano", "ADA");
        COIN_ALIASES.put("ada", "ADA");

        // MATIC/POL 별명
        COIN_ALIASES.put("폴리곤", "MATIC");
        COIN_ALIASES.put("매틱", "MATIC");
        COIN_ALIASES.put("polygon", "MATIC");
        COIN_ALIASES.put("matic", "MATIC");

        // AVAX 별명
        COIN_ALIASES.put("아발란체", "AVAX");
        COIN_ALIASES.put("avalanche", "AVAX");
        COIN_ALIASES.put("avax", "AVAX");

        // DOT 별명
        COIN_ALIASES.put("폴카닷", "DOT");
        COIN_ALIASES.put("polkadot", "DOT");
        COIN_ALIASES.put("dot", "DOT");

        // LINK 별명
        COIN_ALIASES.put("체인링크", "LINK");
        COIN_ALIASES.put("chainlink", "LINK");
        COIN_ALIASES.put("link", "LINK");

        // TRX 별명
        COIN_ALIASES.put("트론", "TRX");
        COIN_ALIASES.put("tron", "TRX");
        COIN_ALIASES.put("trx", "TRX");

        // LTC 별명
        COIN_ALIASES.put("라이트코인", "LTC");
        COIN_ALIASES.put("litecoin", "LTC");
        COIN_ALIASES.put("ltc", "LTC");

        // BCH 별명
        COIN_ALIASES.put("비트코인캐시", "BCH");
        COIN_ALIASES.put("비캐", "BCH");
        COIN_ALIASES.put("bitcoin cash", "BCH");
        COIN_ALIASES.put("bch", "BCH");

        // ATOM 별명
        COIN_ALIASES.put("코스모스", "ATOM");
        COIN_ALIASES.put("아톰", "ATOM");
        COIN_ALIASES.put("cosmos", "ATOM");
        COIN_ALIASES.put("atom", "ATOM");

        // XLM 별명
        COIN_ALIASES.put("스텔라", "XLM");
        COIN_ALIASES.put("스텔라루멘", "XLM");
        COIN_ALIASES.put("stellar", "XLM");
        COIN_ALIASES.put("xlm", "XLM");

        // EOS 별명
        COIN_ALIASES.put("이오스", "EOS");
        COIN_ALIASES.put("eos", "EOS");

        // SHIB 별명
        COIN_ALIASES.put("시바이누", "SHIB");
        COIN_ALIASES.put("시바코인", "SHIB");
        COIN_ALIASES.put("shiba", "SHIB");
        COIN_ALIASES.put("shiba inu", "SHIB");
        COIN_ALIASES.put("shib", "SHIB");

        // ARB 별명
        COIN_ALIASES.put("아비트럼", "ARB");
        COIN_ALIASES.put("arbitrum", "ARB");
        COIN_ALIASES.put("arb", "ARB");

        // OP 별명
        COIN_ALIASES.put("옵티미즘", "OP");
        COIN_ALIASES.put("optimism", "OP");

        // NEAR 별명
        COIN_ALIASES.put("니어", "NEAR");
        COIN_ALIASES.put("near protocol", "NEAR");
        COIN_ALIASES.put("near", "NEAR");

        // APT 별명
        COIN_ALIASES.put("앱토스", "APT");
        COIN_ALIASES.put("aptos", "APT");
        COIN_ALIASES.put("apt", "APT");

        // SUI 별명
        COIN_ALIASES.put("수이", "SUI");
        COIN_ALIASES.put("sui", "SUI");

        // PEPE 별명
        COIN_ALIASES.put("페페", "PEPE");
        COIN_ALIASES.put("pepe", "PEPE");

        // UNI 별명
        COIN_ALIASES.put("유니스왑", "UNI");
        COIN_ALIASES.put("uniswap", "UNI");
        COIN_ALIASES.put("uni", "UNI");

        // AAVE 별명
        COIN_ALIASES.put("에이브", "AAVE");
        COIN_ALIASES.put("aave", "AAVE");

        // FIL 별명
        COIN_ALIASES.put("파일코인", "FIL");
        COIN_ALIASES.put("filecoin", "FIL");
        COIN_ALIASES.put("fil", "FIL");

        // SAND 별명
        COIN_ALIASES.put("샌드박스", "SAND");
        COIN_ALIASES.put("sandbox", "SAND");
        COIN_ALIASES.put("sand", "SAND");

        // MANA 별명
        COIN_ALIASES.put("디센트럴랜드", "MANA");
        COIN_ALIASES.put("decentraland", "MANA");
        COIN_ALIASES.put("mana", "MANA");

        // AXS 별명
        COIN_ALIASES.put("엑시인피니티", "AXS");
        COIN_ALIASES.put("axie infinity", "AXS");
        COIN_ALIASES.put("axie", "AXS");
        COIN_ALIASES.put("axs", "AXS");

        // IMX 별명
        COIN_ALIASES.put("이뮤터블", "IMX");
        COIN_ALIASES.put("이뮤터블엑스", "IMX");
        COIN_ALIASES.put("immutable", "IMX");
        COIN_ALIASES.put("immutable x", "IMX");
        COIN_ALIASES.put("imx", "IMX");

        // ALGO 별명
        COIN_ALIASES.put("알고랜드", "ALGO");
        COIN_ALIASES.put("algorand", "ALGO");
        COIN_ALIASES.put("algo", "ALGO");

        // VET 별명
        COIN_ALIASES.put("비체인", "VET");
        COIN_ALIASES.put("vechain", "VET");
        COIN_ALIASES.put("vet", "VET");

        // FLOW 별명
        COIN_ALIASES.put("플로우", "FLOW");
        COIN_ALIASES.put("flow", "FLOW");

        // GALA 별명
        COIN_ALIASES.put("갈라", "GALA");
        COIN_ALIASES.put("gala", "GALA");
        COIN_ALIASES.put("gala games", "GALA");

        // ENS 별명
        COIN_ALIASES.put("이더리움네임서비스", "ENS");
        COIN_ALIASES.put("ethereum name service", "ENS");
        COIN_ALIASES.put("ens", "ENS");

        // GRT 별명
        COIN_ALIASES.put("더그래프", "GRT");
        COIN_ALIASES.put("그래프", "GRT");
        COIN_ALIASES.put("the graph", "GRT");
        COIN_ALIASES.put("grt", "GRT");

        // RENDER 별명
        COIN_ALIASES.put("렌더", "RENDER");
        COIN_ALIASES.put("render", "RENDER");
        COIN_ALIASES.put("rndr", "RENDER");

        // INJ 별명
        COIN_ALIASES.put("인젝티브", "INJ");
        COIN_ALIASES.put("injective", "INJ");
        COIN_ALIASES.put("inj", "INJ");

        // FET 별명
        COIN_ALIASES.put("페치", "FET");
        COIN_ALIASES.put("fetch", "FET");
        COIN_ALIASES.put("fetch.ai", "FET");
        COIN_ALIASES.put("fet", "FET");

        // AGIX 별명
        COIN_ALIASES.put("싱귤래리티넷", "AGIX");
        COIN_ALIASES.put("singularitynet", "AGIX");
        COIN_ALIASES.put("agix", "AGIX");

        // WLD 별명
        COIN_ALIASES.put("월드코인", "WLD");
        COIN_ALIASES.put("worldcoin", "WLD");
        COIN_ALIASES.put("wld", "WLD");
    }

    // 짧은 티커 목록 (3자 이하) - 단어 경계 검사 필요
    private static final Set<String> SHORT_TICKERS = Set.of(
            "BTC", "ETH", "XRP", "SOL", "ADA", "DOT", "UNI", "LTC", "BCH", "EOS",
            "TRX", "XLM", "VET", "FIL", "ENS", "GRT", "IMX", "APT", "SUI", "ARB",
            "OP", "INJ", "FET", "WLD"
    );

    // 일반 영어 단어와 겹치는 티커 - 매칭에서 제외
    // 관사, 전치사, 접속사 등 문장에서 자주 단독으로 사용되는 단어들
    private static final Set<String> EXCLUDED_TICKERS = Set.of(
            "A",        // 부정관사
            "THE",      // 정관사
            "AN",       // 부정관사
            "AND",      // 접속사
            "OR",       // 접속사
            "FOR",      // 전치사
            "TO",       // 전치사
            "OF",       // 전치사
            "IN",       // 전치사
            "ON",       // 전치사
            "AT",       // 전치사
            "BY",       // 전치사
            "AS",       // 접속사/전치사
            "IS",       // be동사
            "IT",       // 대명사
            "BE",       // be동사
            "WE",       // 대명사
            "GO",       // 동사
            "DO",       // 동사
            "NO",       // 부사
            "SO",       // 부사
            "UP",       // 부사/전치사
            "IF",       // 접속사
            "ME",       // 대명사
            "MY",       // 대명사
            "HE",       // 대명사
            "US",       // 대명사
            "ANY",      // 형용사
            "ALL",      // 형용사
            "NEW",      // 형용사
            "ONE",      // 숫자
            "TWO",      // 숫자
            "GET",      // 동사
            "GOT",      // 동사
            "CAN",      // 조동사
            "MAY",      // 조동사
            "BIG",      // 형용사
            "PRO",      // 명사/형용사
            "KEY",      // 명사
            "TOP",      // 명사/형용사
            "WIN",      // 동사
            "HOT",      // 형용사
            "NOW",      // 부사
            "OLD",      // 형용사
            "OWN"       // 형용사/동사
    );

    /**
     * 뉴스에서 코인 티커를 추출합니다.
     */
    public Set<String> extractTickers(String title, String content, Set<String> categories,
                                       Set<String> knownTickers, Map<String, String> keywordToTicker) {
        Set<String> matched = new HashSet<>();

        // 일반 단어와 겹치는 티커는 매칭 대상에서 제외
        Set<String> filteredTickers = new HashSet<>(knownTickers);
        filteredTickers.removeAll(EXCLUDED_TICKERS);

        // 텍스트 준비
        String fullText = prepareText(title, content);
        String lowerText = fullText.toLowerCase();

        // 1. 카테고리에서 직접 매칭
        matchFromCategories(categories, filteredTickers, matched);

        // 2. 해시태그/캐시태그 패턴 매칭 ($BTC, #Bitcoin 등)
        matchHashtagPatterns(fullText, filteredTickers, matched);

        // 3. 별명/약칭 매칭 (한글/영문 별명)
        matchAliases(lowerText, filteredTickers, matched);

        // 4. 영문명 정확 매칭 (단어 경계 검사)
        matchEnglishNames(fullText, keywordToTicker, filteredTickers, matched);

        // 5. 한글명 매칭
        matchKoreanNames(lowerText, keywordToTicker, filteredTickers, matched);

        // 6. 티커 직접 매칭 (단어 경계 검사)
        matchTickers(fullText, filteredTickers, matched);

        // 7. 복합 패턴 매칭 (Bitcoin ETF, Ethereum 2.0 등)
        matchCompoundPatterns(lowerText, matched, filteredTickers);

        // 최종 결과에서도 제외 티커 필터링
        matched.removeAll(EXCLUDED_TICKERS);

        log.debug("Matched tickers from text: {}", matched);
        return matched;
    }

    private String prepareText(String title, String content) {
        StringBuilder sb = new StringBuilder();
        if (title != null) {
            sb.append(title).append(" ");
        }
        if (content != null) {
            sb.append(content);
        }
        return sb.toString();
    }

    /**
     * 카테고리에서 직접 매칭
     */
    private void matchFromCategories(Set<String> categories, Set<String> knownTickers, Set<String> matched) {
        if (categories == null) return;

        for (String category : categories) {
            String upper = category.toUpperCase();
            if (knownTickers.contains(upper)) {
                matched.add(upper);
            }
            // 카테고리가 별명일 수도 있음
            String aliasMatch = COIN_ALIASES.get(category.toLowerCase());
            if (aliasMatch != null && knownTickers.contains(aliasMatch)) {
                matched.add(aliasMatch);
            }
        }
    }

    /**
     * 해시태그/캐시태그 패턴 매칭 ($BTC, #Bitcoin, @ethereum 등)
     */
    private void matchHashtagPatterns(String text, Set<String> knownTickers, Set<String> matched) {
        // $BTC, #BTC, @BTC 패턴
        Pattern tagPattern = Pattern.compile("[$#@]([A-Za-z]{2,10})\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcher = tagPattern.matcher(text);

        while (matcher.find()) {
            String tag = matcher.group(1).toUpperCase();
            if (knownTickers.contains(tag)) {
                matched.add(tag);
            }
            // 별명으로도 체크
            String aliasMatch = COIN_ALIASES.get(matcher.group(1).toLowerCase());
            if (aliasMatch != null && knownTickers.contains(aliasMatch)) {
                matched.add(aliasMatch);
            }
        }
    }

    /**
     * 별명/약칭 매칭
     */
    private void matchAliases(String lowerText, Set<String> knownTickers, Set<String> matched) {
        for (Map.Entry<String, String> entry : COIN_ALIASES.entrySet()) {
            String alias = entry.getKey();
            String ticker = entry.getValue();

            if (!knownTickers.contains(ticker)) continue;

            // 한글 별명은 그냥 포함 여부로 검사
            if (isKorean(alias)) {
                if (lowerText.contains(alias)) {
                    matched.add(ticker);
                }
            } else {
                // 영문 별명은 단어 경계 검사
                if (containsWord(lowerText, alias)) {
                    matched.add(ticker);
                }
            }
        }
    }

    /**
     * 영문명 정확 매칭 (단어 경계 검사)
     */
    private void matchEnglishNames(String text, Map<String, String> keywordToTicker,
                                    Set<String> knownTickers, Set<String> matched) {
        for (Map.Entry<String, String> entry : keywordToTicker.entrySet()) {
            String keyword = entry.getKey();
            String ticker = entry.getValue();

            if (!knownTickers.contains(ticker)) continue;

            // 영문만 처리 (한글은 별도 처리)
            if (isKorean(keyword)) continue;

            // 단어 경계로 매칭
            if (containsWord(text.toLowerCase(), keyword)) {
                matched.add(ticker);
            }
        }
    }

    /**
     * 한글명 매칭
     */
    private void matchKoreanNames(String lowerText, Map<String, String> keywordToTicker,
                                   Set<String> knownTickers, Set<String> matched) {
        for (Map.Entry<String, String> entry : keywordToTicker.entrySet()) {
            String keyword = entry.getKey();
            String ticker = entry.getValue();

            if (!knownTickers.contains(ticker)) continue;

            // 한글만 처리
            if (!isKorean(keyword)) continue;

            // 한글은 포함 여부로 검사 (2자 이상인 경우만)
            if (keyword.length() >= 2 && lowerText.contains(keyword)) {
                matched.add(ticker);
            }
        }
    }

    /**
     * 티커 직접 매칭 (단어 경계 검사)
     */
    private void matchTickers(String text, Set<String> knownTickers, Set<String> matched) {
        for (String ticker : knownTickers) {
            // 짧은 티커는 단어 경계 검사 필수
            if (SHORT_TICKERS.contains(ticker) || ticker.length() <= 3) {
                if (containsWordExact(text, ticker)) {
                    matched.add(ticker);
                }
            } else {
                // 긴 티커는 대소문자 무시 포함 검사
                if (text.toUpperCase().contains(ticker)) {
                    matched.add(ticker);
                }
            }
        }
    }

    /**
     * 복합 패턴 매칭 (Bitcoin ETF, Ethereum 2.0, BTC halving 등)
     */
    private void matchCompoundPatterns(String lowerText, Set<String> matched, Set<String> knownTickers) {
        // Bitcoin 관련 복합어
        if (containsAny(lowerText, "bitcoin etf", "btc etf", "비트코인 etf", "비트코인etf",
                "bitcoin halving", "btc halving", "비트코인 반감기",
                "bitcoin dominance", "btc dominance", "비트코인 도미넌스",
                "bitcoin mining", "btc mining", "비트코인 채굴",
                "bitcoin whale", "btc whale", "비트코인 고래",
                "satoshi", "사토시")) {
            if (knownTickers.contains("BTC")) matched.add("BTC");
        }

        // Ethereum 관련 복합어
        if (containsAny(lowerText, "ethereum 2.0", "eth 2.0", "이더리움 2.0",
                "ethereum etf", "eth etf", "이더리움 etf",
                "ethereum merge", "eth merge", "이더리움 머지",
                "ethereum gas", "eth gas", "가스비",
                "erc-20", "erc20", "erc-721", "erc721",
                "defi", "디파이")) {
            if (knownTickers.contains("ETH")) matched.add("ETH");
        }

        // XRP 관련
        if (containsAny(lowerText, "ripple labs", "리플 소송", "sec ripple", "sec xrp")) {
            if (knownTickers.contains("XRP")) matched.add("XRP");
        }

        // Solana 관련
        if (containsAny(lowerText, "solana nft", "sol nft", "솔라나 nft",
                "solana defi", "sol defi")) {
            if (knownTickers.contains("SOL")) matched.add("SOL");
        }

        // 밈코인 관련
        if (containsAny(lowerText, "meme coin", "밈코인", "meme token")) {
            if (knownTickers.contains("DOGE")) matched.add("DOGE");
            if (knownTickers.contains("SHIB")) matched.add("SHIB");
            if (knownTickers.contains("PEPE")) matched.add("PEPE");
        }

        // Layer 2 관련
        if (containsAny(lowerText, "layer 2", "layer2", "레이어2", "l2 token", "l2 토큰")) {
            if (knownTickers.contains("MATIC")) matched.add("MATIC");
            if (knownTickers.contains("ARB")) matched.add("ARB");
            if (knownTickers.contains("OP")) matched.add("OP");
        }

        // AI 코인 관련
        if (containsAny(lowerText, "ai coin", "ai token", "ai 코인", "ai 토큰",
                "artificial intelligence crypto")) {
            if (knownTickers.contains("FET")) matched.add("FET");
            if (knownTickers.contains("AGIX")) matched.add("AGIX");
            if (knownTickers.contains("RENDER")) matched.add("RENDER");
            if (knownTickers.contains("WLD")) matched.add("WLD");
        }

        // 게임/메타버스 관련
        if (containsAny(lowerText, "gamefi", "게임파이", "play to earn", "p2e",
                "metaverse", "메타버스")) {
            if (knownTickers.contains("AXS")) matched.add("AXS");
            if (knownTickers.contains("SAND")) matched.add("SAND");
            if (knownTickers.contains("MANA")) matched.add("MANA");
            if (knownTickers.contains("GALA")) matched.add("GALA");
            if (knownTickers.contains("IMX")) matched.add("IMX");
        }
    }

    /**
     * 단어 경계를 고려하여 단어 포함 여부 확인
     */
    private boolean containsWord(String text, String word) {
        String pattern = "(?i)\\b" + Pattern.quote(word) + "\\b";
        return Pattern.compile(pattern).matcher(text).find();
    }

    /**
     * 대소문자 정확히 매칭 (티커용)
     */
    private boolean containsWordExact(String text, String word) {
        // 대문자 티커 매칭
        String patternUpper = "\\b" + Pattern.quote(word) + "\\b";
        if (Pattern.compile(patternUpper).matcher(text).find()) {
            return true;
        }
        // 소문자도 허용 (btc, eth 등)
        String patternLower = "(?i)\\b" + Pattern.quote(word) + "\\b";
        return Pattern.compile(patternLower).matcher(text).find();
    }

    /**
     * 문자열이 한글을 포함하는지 확인
     */
    private boolean isKorean(String text) {
        return text.matches(".*[가-힣]+.*");
    }

    /**
     * 여러 키워드 중 하나라도 포함되는지 확인
     */
    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 외부에서 별명 추가 가능
     */
    public void addAlias(String alias, String ticker) {
        COIN_ALIASES.put(alias.toLowerCase(), ticker.toUpperCase());
    }

    /**
     * 현재 등록된 별명 목록 조회
     */
    public Map<String, String> getAliases() {
        return Collections.unmodifiableMap(COIN_ALIASES);
    }
}
