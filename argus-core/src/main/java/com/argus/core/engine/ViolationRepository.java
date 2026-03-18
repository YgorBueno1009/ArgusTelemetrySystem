package com.argus.core.engine;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ViolationRepository extends ReactiveCrudRepository<ViolationEntity, Long> {
    // Busca todas as multas de um caminhão (Retorna um Flux/Lista)
    Flux<ViolationEntity> findAllByTruckId(String truckId);
}