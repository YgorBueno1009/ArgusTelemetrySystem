package com.argus.core.engine;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelemetryRepository extends ReactiveCrudRepository<TelemetryEntity, Long> {
    // Deixe vazio por enquanto, o Spring cuida do resto!
}