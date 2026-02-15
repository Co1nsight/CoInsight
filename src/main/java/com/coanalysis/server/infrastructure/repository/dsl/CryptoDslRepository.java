package com.coanalysis.server.infrastructure.repository.dsl;

import com.coanalysis.server.crypto.application.domain.Crypto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface CryptoDslRepository {

	List<Crypto> findAllByTickers(Set<String> coinTickers);

	Set<String> findAllTickers();
}
