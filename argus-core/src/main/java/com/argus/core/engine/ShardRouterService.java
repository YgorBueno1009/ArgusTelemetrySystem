package com.argus.core.engine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ShardRouterService {

    /**
     * Define qual Shard deve processar o caminhão.
     * Regra: ID par -> Shard 1 | ID ímpar -> Shard 2
     */
    public int getShardId(String truckId) {
        try {
            // Extrai apenas o número do "TRUCK-123"
            int idNumeric = Integer.parseInt(truckId.replaceAll("\\D+", ""));
            return (idNumeric % 2 == 0) ? 1 : 2;
        } catch (Exception e) {
            return 1; // Fallback para o Shard 1
        }
    }
}