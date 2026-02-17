package com.coanalysis.server.market.adapter.out;

import com.coanalysis.server.infrastructure.exception.CustomException;
import com.coanalysis.server.infrastructure.exception.ErrorCode;
import com.coanalysis.server.market.adapter.out.dto.BithumbCandleDto;
import com.coanalysis.server.market.adapter.out.dto.BithumbMarketDto;
import com.coanalysis.server.market.adapter.out.dto.BithumbTickerDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.SecretKey;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BithumbClient {

    private final RestTemplate restTemplate;

    @Value("${bithumb.api.base-url}")
    private String baseUrl;

    @Value("${bithumb.api.access-key:}")
    private String accessKey;

    @Value("${bithumb.api.secret-key:}")
    private String secretKey;

    public List<BithumbMarketDto> getMarkets() {
        String url = baseUrl + "/v1/market/all";
        log.info("Fetching markets from Bithumb API: {}", url);

        try {
            HttpEntity<Void> entity = new HttpEntity<>(createAuthHeaders(null));
            ResponseEntity<List<BithumbMarketDto>> response = restTemplate.exchange(
                    URI.create(url),
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            handleHttpError(e);
            throw new CustomException(ErrorCode.BITHUMB_API_ERROR);
        } catch (RestClientException e) {
            log.error("Failed to fetch markets from Bithumb API", e);
            throw new CustomException(ErrorCode.BITHUMB_API_ERROR, e.getMessage());
        }
    }

    public List<BithumbTickerDto> getTickers(List<String> markets) {
        String marketsParam = String.join(",", markets);
        String queryString = "markets=" + marketsParam;
        String url = baseUrl + "/v1/ticker?" + queryString;
        log.info("Fetching tickers from Bithumb API for {} markets", markets.size());

        try {
            HttpEntity<Void> entity = new HttpEntity<>(createAuthHeaders(queryString));
            ResponseEntity<List<BithumbTickerDto>> response = restTemplate.exchange(
                    URI.create(url),
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );

            List<BithumbTickerDto> result = response.getBody();
            log.info("Bithumb API returned {} tickers", result != null ? result.size() : 0);

            return result;
        } catch (HttpClientErrorException e) {
            handleHttpError(e);
            throw new CustomException(ErrorCode.BITHUMB_API_ERROR);
        } catch (RestClientException e) {
            log.error("Failed to fetch tickers from Bithumb API: {}", e.getMessage());
            throw new CustomException(ErrorCode.BITHUMB_API_ERROR, e.getMessage());
        }
    }

    public List<BithumbCandleDto> getCandles(String market, int unit, int count) {
        String queryString = String.format("market=%s&count=%d", market, count);
        String url = String.format("%s/v1/candles/minutes/%d?%s", baseUrl, unit, queryString);
        log.info("Fetching candles from Bithumb API: {}", url);

        try {
            HttpEntity<Void> entity = new HttpEntity<>(createAuthHeaders(queryString));
            ResponseEntity<List<BithumbCandleDto>> response = restTemplate.exchange(
                    URI.create(url),
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            handleHttpError(e);
            throw new CustomException(ErrorCode.BITHUMB_API_ERROR);
        } catch (RestClientException e) {
            log.error("Failed to fetch candles from Bithumb API", e);
            throw new CustomException(ErrorCode.BITHUMB_API_ERROR, e.getMessage());
        }
    }

    private HttpHeaders createAuthHeaders(String queryString) {
        HttpHeaders headers = new HttpHeaders();

        if (!StringUtils.hasText(accessKey) || !StringUtils.hasText(secretKey)) {
            log.debug("Bithumb API keys not configured, skipping authentication");
            return headers;
        }

        try {
            String token = generateJwtToken(queryString);
            headers.set("Authorization", "Bearer " + token);
        } catch (Exception e) {
            log.warn("Failed to generate JWT token for Bithumb API", e);
        }

        return headers;
    }

    private String generateJwtToken(String queryString) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("access_key", accessKey);
        claims.put("nonce", UUID.randomUUID().toString());
        claims.put("timestamp", System.currentTimeMillis());

        if (StringUtils.hasText(queryString)) {
            String queryHash = hashQueryString(queryString);
            claims.put("query_hash", queryHash);
            claims.put("query_hash_alg", "SHA512");
        }

        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .claims(claims)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    private String hashQueryString(String queryString) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] digest = md.digest(queryString.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new CustomException(ErrorCode.BITHUMB_PARSE_ERROR, "SHA-512 algorithm not available");
        }
    }

    private void handleHttpError(HttpClientErrorException e) {
        if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
            log.warn("Bithumb API rate limit exceeded");
            throw new CustomException(ErrorCode.BITHUMB_RATE_LIMIT);
        } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
            log.warn("Invalid market requested: {}", e.getMessage());
            throw new CustomException(ErrorCode.BITHUMB_INVALID_MARKET);
        }
        log.error("Bithumb API error: {} - {}", e.getStatusCode(), e.getMessage());
    }
}
