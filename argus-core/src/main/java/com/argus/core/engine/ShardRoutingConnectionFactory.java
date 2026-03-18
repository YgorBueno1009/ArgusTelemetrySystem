package com.argus.core.engine;

import org.springframework.r2dbc.connection.lookup.AbstractRoutingConnectionFactory;
import reactor.core.publisher.Mono;

/**
 * Roteador dinâmico de conexões.
 * Ele olha para o "Contexto" atual e escolhe o Shard certo.
 */
public class ShardRoutingConnectionFactory extends AbstractRoutingConnectionFactory {

    @Override
    protected Mono<Object> determineCurrentLookupKey() {
        // Busca o ID do Shard (1 ou 2) que guardaremos no contexto da thread reativa
        return Mono.deferContextual(context -> Mono.justOrEmpty(context.getOrEmpty("SHARD_ID")));
    }
}