package com.argus.core.codec;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) representando o pacote de dados do caminhão.
 * Segue o princípio de imutabilidade e clareza.
 */
@Data
@Builder
public class TelemetryPacket {
    private String truckId;
    private double latitude;
    private double longitude;
    private double speed;
    private int rpm;
    private LocalDateTime timestamp;
}