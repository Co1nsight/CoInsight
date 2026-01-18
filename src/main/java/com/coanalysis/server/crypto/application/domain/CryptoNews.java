package com.coanalysis.server.crypto.application.domain;

import com.coanalysis.server.crypto.application.domain.id.CryptoNewsId;
import com.coanalysis.server.infrastructure.entity.BaseEntity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Table(name = "CRYPTO_NEWS_MAPPING")
@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CryptoNews extends BaseEntity {

    @EmbeddedId
    private CryptoNewsId id;

}
