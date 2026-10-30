# GovPay STAMPE API - Configurazione Docker

Containerizzazione Docker per il servizio REST API GovPay STAMPE che genera avvisi di pagamento pagoPA in formato PDF.

## Panoramica

Questa configurazione Docker fornisce:
- Servizio REST API per la generazione di avvisi di pagamento
- Supporto per avvisi standard, rate, postali, bilingue e violazioni CDS
- Controlli di salute e monitoraggio
- Configurazione memoria JVM flessibile

## Avvio Rapido

### 1. Costruire l'Immagine Docker

```bash
# Build con ultima versione
./build_image.sh

# Build con versione specifica
./build_image.sh -v 1.0.0
```

### 2. Avviare il Container

```bash
# Avviare con docker run
docker run -d \
  --name govpay-stampe-api \
  -p 10003:10003 \
  linkitaly/govpay-stampe-api:latest

# Verificare lo stato
docker logs govpay-stampe-api

# Testare l'API
curl http://localhost:10003/actuator/health
```

## Architettura

```
┌─────────────────────────────────────┐
│      GovPay STAMPE REST API         │
│                                     │
│  ┌──────────────────────────────┐  │
│  │   Spring Boot Application    │  │
│  │   - REST Controllers         │  │
│  │   - PDF Generation Service   │  │
│  │   - JasperReports Engine     │  │
│  └──────────────────────────────┘  │
│              │                      │
│              ▼                      │
│  ┌──────────────────────────────┐  │
│  │   PDF Templates (Jasper)     │  │
│  └──────────────────────────────┘  │
└─────────────────────────────────────┘
```

## Riferimento Configurazione

L'elenco completo delle variabili d'ambiente dell'immagine, con i default, è nel [README](../README.md#esecuzione-con-docker).

## Endpoint API

Una volta avviato, sono disponibili i seguenti endpoint:

### Health Check
```bash
curl http://localhost:10003/actuator/health
```

### Generazione Avviso Standard
```bash
curl -X POST http://localhost:10003/api/v1/standard \
  -H "Content-Type: application/json" \
  -d @avviso.json \
  --output avviso.pdf
```

### Generazione Avviso Violazione CDS
```bash
curl -X POST http://localhost:10003/api/v1/cds_violation \
  -H "Content-Type: application/json" \
  -d @violazione.json \
  --output violazione.pdf
```

## Ottimizzazione delle Prestazioni

### Opzioni JVM

Per deployment in produzione:

```env
JAVA_OPTS=-XX:+UseG1GC -XX:MaxGCPauseMillis=100
GOVPAY_STAMPE_JVM_MAX_RAM_PERCENTAGE=90
```

## Struttura File

```
govpay-stampe-api/
├── docker/
│   ├── build_image.sh              # Script di build
│   ├── commons/
│   │   └── entrypoint.sh           # Script di avvio container
│   ├── govpay-stampe/
│   │   ├── Dockerfile.github       # Dockerfile per release GitHub
│   │   └── Dockerfile.local        # Dockerfile per build locale
│   └── DOCKER.md                   # Questo file
├── src/                            # Codice sorgente Java
├── pom.xml                         # Configurazione Maven
└── README.md                       # Documentazione sviluppo Java
```

## Supporto

Per problemi relativi a:
- **GovPay**: https://github.com/link-it/govpay
- **Sviluppo Java**: Vedere `README.md`
- **Configurazione Docker**: Contattare il team DevOps
