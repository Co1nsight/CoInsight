# 코인 뉴스 배치 수집 및 호재/악재 분석 기능 명세서

## 1. 요구사항 요약

### 기능 요구사항
- CryptoCompare News API에서 암호화폐 뉴스 수집
- 1시간 주기 배치 실행
- 중복 뉴스 제거 (originalLink 기준)
- 코인-뉴스 매핑 (categories + 키워드 매칭)
- 호재/악재 분석 (HuggingFace KR-FinBert-SC)

### 기술 스택
- Spring Boot 3.5.9, Java 21
- 헥사고날 아키텍처
- H2 인메모리 DB
- Virtual Thread 활성화

---

## 2. 파일 구조

```
src/main/java/com/coanalysis/server/
├── batch/
│   ├── adapter/
│   │   ├── in/
│   │   │   └── NewsCollectionScheduler.java
│   │   └── out/
│   │       ├── CryptoCompareNewsClient.java
│   │       ├── NewsCollectionAdapter.java
│   │       ├── CryptoNewsMappingAdapter.java
│   │       └── dto/
│   │           ├── CryptoCompareNewsResponse.java
│   │           └── CryptoCompareNewsItem.java
│   └── application/
│       ├── domain/
│       │   └── CollectedNews.java
│       ├── port/
│       │   ├── in/
│       │   │   └── CollectNewsUseCase.java
│       │   └── out/
│       │       ├── FetchCryptoNewsPort.java
│       │       ├── SaveCollectedNewsPort.java
│       │       ├── FindDuplicateNewsPort.java
│       │       └── MapCryptoNewsPort.java
│       └── service/
│           └── NewsCollectionService.java
├── infrastructure/
│   └── config/
│       └── SchedulingConfig.java
```

---

## 3. 배치 흐름

1. CryptoCompare API 호출 (최신 50개 뉴스)
2. DTO → Domain 변환
3. 중복 제거 (DB 조회)
4. 코인 매핑 추출 (categories + 키워드)
5. News 엔티티 저장
6. 감성 분석 (Virtual Thread 병렬)
7. CryptoNews 매핑 저장
8. 로깅 및 완료

---

## 4. 수정할 기존 파일

- `NewsRepository.java`: existsByOriginalLink 메서드 추가
- `application.properties`: CryptoCompare 설정 추가

---

## 5. API 정보

**CryptoCompare News API:**
```
GET https://min-api.cryptocompare.com/data/v2/news/?lang=EN
```

무료 Tier: 100,000 calls/month
