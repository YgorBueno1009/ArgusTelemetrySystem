package com.argus.core.network;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import reactor.netty.tcp.TcpServer;

/**
 * Configuração Bare-Metal do servidor TCP usando Reactor Netty.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class TcpServerConfig {

    private final TcpServerHandler handler;

    @PostConstruct
    public void startTcpServer() {
        // Criamos o servidor e guardamos a referência do 'DisposableServer'
        var server = TcpServer.create()
                .port(8080)
                .handle(handler::handle)
                .bindNow(); // Inicia o servidor AGORA nesta linha

        log.info("🚀 Argus Telemetry bloqueado e escutando na porta: {}", server.port());

        // O SEGREDO: Esta linha impede que o Java encerre o processo.
        // Ela diz: "Fique parado aqui até que alguém mate o servidor".
        Thread.ofPlatform().start(() -> server.onDispose().block());
    }
}