# 🛰️ Argus: High-Performance Telemetry System

O **Argus** é um ecossistema de monitoramento de frotas em tempo real, projetado para processar milhares de eventos simultâneos com baixíssima latência. O sistema utiliza uma arquitetura reativa moderna para rastreamento de veículos, detecção de infrações e visualização geoespacial.

---

## 🚀 Diferenciais Técnicos (O que há sob o capô)

Este projeto não é apenas uma aplicação Spring Boot comum. Ele foi construído utilizando conceitos de sistemas distribuídos de "Big Techs":

- **Database Sharding:** Distribuição de carga horizontal entre múltiplos bancos de dados PostgreSQL para suportar crescimento massivo de dados sem perda de performance.
    
- **Reactive Stack (WebFlux & R2DBC):** Programação não-bloqueante de ponta a ponta, permitindo que o sistema processe milhares de conexões com consumo mínimo de memória.
    
- **Dual-Layer Storage:** * **Hot Data (Redis):** Cache em memória para acesso instantâneo ao estado atual dos veículos (< 1ms).
    
    - **Cold Data (PostgreSQL):** Persistência de longo prazo para histórico de trajetórias e infrações.
        
- **Protocolo Binário TCP:** Ingestão de dados via sockets puros, simulando a comunicação real de rastreadores veiculares (mais leve e rápido que HTTP).
    

---

## 🛠️ Tecnologias Utilizadas

|**Camada**|**Tecnologia**|
|---|---|
|**Linguagem**|Java 21|
|**Framework**|Spring Boot 3.x (WebFlux, Data Redis, Data R2DBC)|
|**Mensageria/Ingestão**|Netty (TCP Server)|
|**Cache/Real-time**|Redis|
|**Banco de Dados**|PostgreSQL (com estratégia de Sharding)|
|**Containers**|Docker & Docker Compose|
|**Frontend**|Leaflet.js (Mapas Geoespaciais)|

---

## 🏗️ Arquitetura do Sistema

1. **Simulator:** Emula o comportamento de múltiplos caminhões enviando pacotes de telemetria (ID, Lat/Log, Velocidade, RPM) via TCP.
    
2. **Argus Core Engine:** * Recebe os dados binários.
    
    - Calcula infrações em tempo real (ex: excesso de velocidade > 80km/h).
        
    - Roteia os dados para o Shard correto do banco.
        
    - Atualiza o cache de visualização no Redis.
        
3. **Visualizer:** Dashboard interativo que consome a API reativa e plota o rastro e alertas visuais no mapa.
    

---

## 🚦 Como Rodar o Projeto

### Pré-requisitos

- Docker e Docker Compose instalados.
    
- JDK 21+.
    
- Maven.
    

### Passo 1: Subir a Infraestrutura

Bash

```
docker-compose up -d
```

_Isso iniciará os Shards do Postgres e o Redis._

### Passo 2: Executar o Core (Backend)

No IntelliJ ou via terminal na pasta `argus-core`:

Bash

```
mvn spring-boot:run
```

_O servidor TCP subirá na porta **8080** e a API Web na **8081**._

### Passo 3: Executar o Simulator

Na pasta `argus-simulator`:

Bash

```
mvn exec:java -Dexec.mainClass="com.argus.simulator.TruckSimulator"
```

### Passo 4: Abrir o Monitor

Basta abrir o arquivo `index.html` no seu navegador favorito.

---

## 📈 Endpoints Principais (API)

- **Estado Atual:** `GET /trucks/{truckId}` -> Retorna a última posição conhecida via Redis.
    
- **Histórico de Multas:** `GET /trucks/{truckId}/violations` -> Busca as infrações registradas nos Shards do Postgres.
    

---

## 👨‍💻 Desenvolvedor

**Ygor**

- Desenvolvedor Java Reativo e Arquitetura de Sistemas.