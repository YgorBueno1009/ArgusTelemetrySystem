package com.argus.core.engine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/trucks")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@Slf4j // Adicionado para funcionar o log.error
public class VehicleController {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ViolationRepository violationRepository;
    private final ShardRouterService shardRouter;
    private static final String REDIS_PREFIX = "vehicle:state:";

    // Rota 1: Estado Atual (Lê do Redis e converte para JSON)
    @GetMapping("/{truckId}")
    public Mono<TelemetryEntity> getTruckLocation(@PathVariable String truckId) {
        return redisTemplate.opsForValue()
                .get(REDIS_PREFIX + truckId)
                .map(data -> {
                    // Caso o dado esteja no formato ToString do Lombok
                    if (data.contains("latitude=")) {
                        return parseLombokString(data, truckId);
                    }

                    // Caso o dado esteja no formato "lat;lon;speed;rpm"
                    String[] parts = data.split(";");
                    return TelemetryEntity.builder()
                            .truckId(truckId)
                            .latitude(Double.parseDouble(parts[0]))
                            .longitude(Double.parseDouble(parts[1]))
                            .speed(Float.parseFloat(parts[2]))
                            .rpm(Integer.parseInt(parts[3]))
                            .build();
                })
                .doOnError(e -> log.error("❌ Erro ao processar telemetria para {}: {}", truckId, e.getMessage()));
    }

    // Rota 2: Relatório de Multas (Postgres Sharding)
    @GetMapping("/{truckId}/violations")
    public Flux<ViolationEntity> getViolations(@PathVariable String truckId) {
        int shardId = shardRouter.getShardId(truckId);

        return violationRepository.findAllByTruckId(truckId)
                .contextWrite(ctx -> ctx.put("SHARD_ID", shardId));
    }

    // Método auxiliar para processar a String do Lombok
    private TelemetryEntity parseLombokString(String data, String truckId) {
        try {
            double lat = Double.parseDouble(data.split("latitude=")[1].split(",")[0].split("\\)")[0]);
            double lon = Double.parseDouble(data.split("longitude=")[1].split(",")[0].split("\\)")[0]);
            float speed = Float.parseFloat(data.split("speed=")[1].split(",")[0].split("\\)")[0]);

            return TelemetryEntity.builder()
                    .truckId(truckId)
                    .latitude(lat)
                    .longitude(lon)
                    .speed(speed)
                    .build();
        } catch (Exception e) {
            log.error("⚠️ Falha no parse da String Lombok: {}", data);
            return TelemetryEntity.builder().truckId(truckId).build();
        }
    }
}