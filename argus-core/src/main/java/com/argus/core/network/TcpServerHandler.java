package com.argus.core.network;

import com.argus.core.codec.BinaryDecoder;
import com.argus.core.codec.TelemetryPacket;
import com.argus.core.engine.VehicleStateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.NettyInbound;
import reactor.netty.NettyOutbound;

/**
 * Handler responsável por processar os dados recebidos via TCP.
 * Segue o padrão Pipeline do Netty para processamento assíncrono.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TcpServerHandler {

    private final BinaryDecoder decoder = new BinaryDecoder();
    private final VehicleStateService stateService; // Sem os comentários que estavam no print

    public Mono<Void> handle(NettyInbound inbound, NettyOutbound outbound) {

        return inbound.receive()
                .asByteArray()
                .flatMap(bytes -> {
                    try {
                        TelemetryPacket packet = decoder.decode(bytes);

                        // Salvamos no Redis e, quando terminar, printamos o pacote COMPLETO
                        return stateService.updateState(packet)
                                .doOnSuccess(v -> log.info("🚛 DADOS RECEBIDOS: {}", packet));
                        // O 'packet' aqui vai mostrar ID, Lat, Lon, Speed e RPM
                    } catch (Exception e) {
                        log.error("❌ Falha crítica no decode: {}", e.getMessage());
                        return Mono.empty();
                    }
                })
                .then();
    }
}