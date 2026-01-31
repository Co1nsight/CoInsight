package com.coanalysis.server.infrastructure.repository;

import com.coanalysis.server.crypto.application.domain.CryptoNews;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CryptoNewsRepository extends JpaRepository<CryptoNews, Long> {
}
