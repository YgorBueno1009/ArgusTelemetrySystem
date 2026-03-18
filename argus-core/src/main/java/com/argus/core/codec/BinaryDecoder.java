package com.argus.core.codec;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;

/**
 * Decodificador de alto desempenho para o protocolo binário do Argus.
 */
public class BinaryDecoder {

    /**
     * Decodifica bytes brutos seguindo o layout:
     * [0-3] ID (Int) | [4-11] Lat (Double) | [12-19] Lon (Double) | [20-23] Speed (Float)
     */
    public TelemetryPacket decode(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);

        return TelemetryPacket.builder()
                .truckId("TRUCK-" + buffer.getInt())
                .latitude(buffer.getDouble())
                .longitude(buffer.getDouble())
                .speed(buffer.getFloat())
                // Lemos o Short que acabamos de adicionar no simulador
                .rpm(Short.toUnsignedInt(buffer.getShort()))
                .timestamp(LocalDateTime.now())
                .build();
    }
}