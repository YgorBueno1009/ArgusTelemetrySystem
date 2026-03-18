package com.argus.core.engine;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;

import java.util.Map;

@Configuration
public class DatabaseConfig extends AbstractR2dbcConfiguration {

    @Override
    @Bean
    public ConnectionFactory connectionFactory() {
        ShardRoutingConnectionFactory routingFactory = new ShardRoutingConnectionFactory();

        // Configuração do Shard 1 (Porta 5433) - Caminhões Pares
        ConnectionFactory shard1 = new PostgresqlConnectionFactory(
                PostgresqlConnectionConfiguration.builder()
                        .host("localhost")
                        .port(5433)
                        .database("argus_history_01")
                        .username("igor")
                        .password("password123")
                        .build());

        // Configuração do Shard 2 (Porta 5434) - Caminhões Ímpares
        ConnectionFactory shard2 = new PostgresqlConnectionFactory(
                PostgresqlConnectionConfiguration.builder()
                        .host("localhost")
                        .port(5434)
                        .database("argus_history_02")
                        .username("igor")
                        .password("password123")
                        .build());

        // Mapeia os IDs dos Shards para as conexões reais
        routingFactory.setTargetConnectionFactories(Map.of(
                1, shard1,
                2, shard2
        ));

        routingFactory.setDefaultTargetConnectionFactory(shard1);
        return routingFactory;
    }
}