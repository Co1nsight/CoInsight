package com.coanalysis.server.batch.application.port.out;

import java.util.Set;

public interface FindDuplicateNewsPort {
    boolean existsByOriginalLink(String originalLink);
    Set<String> findExistingLinks(Set<String> links);
}
