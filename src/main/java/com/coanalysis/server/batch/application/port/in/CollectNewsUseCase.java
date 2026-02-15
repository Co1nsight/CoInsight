package com.coanalysis.server.batch.application.port.in;

public interface CollectNewsUseCase {
    /**
     * 뉴스 수집, 중복 제거, 감성 분석, 코인 매핑 전체 프로세스 실행
     * @return 새로 수집/처리된 뉴스 개수
     */
    int collectAndProcessNews();
}
