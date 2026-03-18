package com.argus.simulator;

import lombok.SneakyThrows;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Classe principal do simulador.
 * Mantém os 100 caminhões enviando dados para o Core de 5 em 5 segundos.
 */
public class MainSimulator {
    @SneakyThrows
    public static void main(String[] args) {

        // 1. Criamos a frota de 100 caminhões
        List<TruckSimulator> trucks = IntStream.rangeClosed(1, 100)
                .mapToObj(i -> new TruckSimulator("TRUCK-" + i))
                .toList();

        System.out.println("🚛 [ARGUS-SIMULATOR] Iniciado.");
        System.out.println("📡 Enviando telemetria de 100 veículos a cada 5 segundos...");

        // 2. Pool de threads para gerenciar o agendamento sem travar o PC
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);

        // 3. Agendamos o envio para cada caminhão
        // 3. Primeiro, conectamos todos os caminhões
        trucks.forEach(truck -> {
            new Thread(truck).start(); // Isso chama o run() e abre o Socket
        });

// Dá 2 segundos para todos os sockets abrirem antes de começar o agendamento
        Thread.sleep(2000);

// 4. Agora sim, agendamos o envio constante
        trucks.forEach(truck -> {
            executor.scheduleAtFixedRate(() -> {
                truck.sendData();
            }, 0, 5, TimeUnit.SECONDS);
        });

        // 4. TRAVA DE SEGURANÇA: Impede que o programa feche sozinho
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("🛑 Desligando simulador...");
            executor.shutdown();
        }));
    }
}