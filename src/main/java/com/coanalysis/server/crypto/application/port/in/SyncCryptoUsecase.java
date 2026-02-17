package com.coanalysis.server.crypto.application.port.in;

public interface SyncCryptoUsecase {

    /**
     * Bithumb API에서 코인 목록을 가져와 DB와 동기화합니다.
     * 새로운 코인만 추가하고, 기존에 있던 코인이 삭제되어도 유지합니다.
     *
     * @return 새로 추가된 코인 수
     */
    int syncCryptos();
}
