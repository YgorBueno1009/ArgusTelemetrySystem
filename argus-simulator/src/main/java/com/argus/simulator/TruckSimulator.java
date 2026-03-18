package com.argus.simulator;

import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Random;

/**
 * Simula um rastreador físico enviando pacotes binários via TCP.
 */
public class TruckSimulator implements Runnable {

    // 1. ATRIBUTOS (Agora acessíveis por todos os métodos)
    private final String truckId;
    private final Random random = new Random();
    private double latitude = -15.7941;
    private double longitude = -47.8825;
    private float speed = 0;
    private OutputStream out;
    private Socket socket;

    public TruckSimulator(String truckId) {
        this.truckId = truckId;
    }

    public String getTruckId() {
        return truckId;
    }

    @Override
    public void run() {
        try {
            // Conecta uma vez e mantém a conexão aberta
            this.socket = new Socket("localhost", 8080);
            this.out = socket.getOutputStream();
            System.out.println("🚛 " + truckId + " conectado ao Core.");
        } catch (Exception e) {
            System.err.println("❌ Erro ao conectar " + truckId + ": " + e.getMessage());
        }
    }

    /**
     * Atualiza os dados e envia para o Core.
     * Chamado pelo agendador do MainSimulator a cada 5 segundos.
     */
    public void sendData() {
        if (out == null) {
            System.out.println("⚠️ " + truckId + " ainda não conectou. Tentando reconectar...");
            new Thread(this).start(); // Tenta abrir o socket de novo se caiu
            return;
        }

        try {
            this.latitude += (Math.random() - 0.5) * 0.001;
            this.longitude += (Math.random() - 0.5) * 0.001;
            this.speed = 60 + (float) (Math.random() * 40);

            byte[] packet = generateFakePacket();
            out.write(packet);
            out.flush();

            // Log para você ver no terminal do SIMULATOR que está saindo dado
            System.out.println("📡 " + truckId + " enviou: Lat=" + String.format("%.4f", latitude) + " Vel=" + String.format("%.1f", speed));

        } catch (Exception e) {
            System.err.println("❌ Erro no envio: " + e.getMessage());
            this.out = null; // Reseta para tentar reconectar no próximo ciclo
        }
    }

    private byte[] generateFakePacket() {
        ByteBuffer buffer = ByteBuffer.allocate(26);
        buffer.putInt(extractIdNumeric(truckId));
        buffer.putDouble(this.latitude);
        buffer.putDouble(this.longitude);
        buffer.putFloat(this.speed);
        buffer.putShort((short) (1500 + random.nextInt(2000))); // RPM
        return buffer.array();
    }

    private int extractIdNumeric(String truckId) {
        return Integer.parseInt(truckId.replaceAll("\\D+", ""));
    }
}