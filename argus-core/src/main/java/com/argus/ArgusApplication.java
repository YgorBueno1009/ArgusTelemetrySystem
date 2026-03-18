package com.argus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

/**
 * Ponto de entrada principal da nossa infraestrutura.
 * A anotação @SpringBootApplication ativa a varredura de componentes (Component Scan),
 * o que fará o Spring encontrar nosso servidor TCP automaticamente.
 */
@SpringBootApplication
public class ArgusApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ArgusApplication.class);

        // Força a porta 8081 para o Servidor Web (Mapa/API)
        // Deixa a 8080 livre para o seu Motor TCP (Caminhões)
        app.setDefaultProperties(Collections.singletonMap("server.port", "8081"));

        app.run(args);
    }
}