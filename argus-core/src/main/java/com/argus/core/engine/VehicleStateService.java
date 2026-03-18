package com.argus.core.engine;

import com.argus.core.codec.TelemetryPacket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleStateService {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final TelemetryRepository telemetryRepository;
    private final ViolationRepository violationRepository; // NOVO
    private final ShardRouterService shardRouter;

    public Mono<Void> updateState(TelemetryPacket packet) {
        int shardId = shardRouter.getShardId(packet.getTruckId());
        String key = "vehicle:state:" + packet.getTruckId();

        // 1. Lógica de Log e Gravação de Multa
        Mono<Void> violationMono = Mono.empty();

        if (packet.getSpeed() > 80.0) {
            log.info("\u001B[31m🚨 DADOS RECEBIDOS (MULTA): {}\u001B[0m", packet);

            ViolationEntity violation = ViolationEntity.builder()
                    .truckId(packet.getTruckId())
                    .speed((float)packet.getSpeed())
                    .latitude(packet.getLatitude())
                    .longitude(packet.getLongitude())
                    .timestamp(packet.getTimestamp())
                    .build();

            // Prepara a gravação da multa no shard correto
            violationMono = violationRepository.save(violation)
                    .contextWrite(ctx -> ctx.put("SHARD_ID", shardId))
                    .then();
        } else {
            log.info("✅ DADOS RECEBIDOS: {}", packet);
        }

        TelemetryEntity telemetry = TelemetryEntity.builder()
                .truckId(packet.getTruckId())
                .latitude(packet.getLatitude())
                .longitude(packet.getLongitude())
                .speed((float)packet.getSpeed())
                .rpm(packet.getRpm())
                .timestamp(packet.getTimestamp())
                .build();

        // 2. Fluxo: Salva Redis -> Salva Telemetria -> Salva Multa (se houver)
        return redisTemplate.opsForValue()
                .set(key, telemetry.toString(), Duration.ofHours(24))
                .then(
                        telemetryRepository.save(telemetry)
                                .contextWrite(ctx -> ctx.put("SHARD_ID", shardId))
                )
                .then(violationMono); // Executa a gravação da multa
    }
}