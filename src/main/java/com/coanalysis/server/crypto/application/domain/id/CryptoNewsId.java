package com.coanalysis.server.crypto.application.domain.id;

import jakarta.persistence.*;

@Embeddable
public class CryptoNewsId {

    @Column(name = "CRYPTO_ID")
    private Long cryptoId;

    @Column(name = "NEWS_ID")
    private Long newsId;

}
