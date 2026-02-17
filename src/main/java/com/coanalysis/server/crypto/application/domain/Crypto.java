package com.coanalysis.server.crypto.application.domain;

import com.coanalysis.server.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "CRYPTO")
@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Crypto extends BaseEntity {

    @Id
    @Column(nullable = false, length = 10)
    private String ticker;   //BTC,ETH

    @Column(name = "NAME", length = 1000, nullable = false)
    private String name;  // 한글명 (비트코인)

    @Column(name = "ENGLISH_NAME", length = 1000)
    private String englishName;  // 영문명 (Bitcoin)

    @Column(name = "LOGO_URL", length = 500)
    private String logoUrl;

    private double currentPrice; //현재가

    private double tradingVolume; //거래대금

}
