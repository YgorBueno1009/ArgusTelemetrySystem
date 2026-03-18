package com.argus.core.engine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("speed_violations")
public class ViolationEntity {
    @Id
    private Long id;
    private String truckId;
    private Float speed;
    private Double latitude;
    private Double longitude;
    private LocalDateTime timestamp;
}