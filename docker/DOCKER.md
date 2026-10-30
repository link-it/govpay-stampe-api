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

Lo script si esegue dalla directory `docker/`:

```bash
cd docker

# Build con il jar dell'ultima release GitHub
./build_image.sh

# Build con il jar di una release specifica
./build_image.sh -v 1.0.0

# Build con il jar della build locale (mvn clean package -P jar), usa Dockerfile.daFile
./build_image.sh -l ../target/govpay-stampe-api.jar -t govpay-stampe-api:local
```

L'immagine costruita con `-t govpay-stampe-api:local` si avvia anche con il `docker-compose.yml` nella radice del repository.

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
curl -X POST http://localhost:10003/standard \
  -H "Content-Type: application/json" \
  -d @avviso.json \
  --output avviso.pdf
```

### Generazione Avviso Violazione CDS
```bash
curl -X POST http://localhost:10003/cds_violation \
  -H "Content-Type: application/json" \
  -d @violazione.json \
  --output violazione.pdf
```

### Generazione Ricevuta
```bash
curl -X POST http://localhost:10003/receipt \
  -H "Content-Type: application/json" \
  -d @ricevuta.json \
  --output ricevuta.pdf
```

Il formato delle richieste è descritto nel [README](../README.md#avvisi-di-pagamento).

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
│   ├── scan_image.sh               # Scansione delle vulnerabilità (vedi SCANSIONE.md)
│   ├── commons/
│   │   └── entrypoint.sh           # Script di avvio container
│   ├── govpay-stampe/
│   │   ├── Dockerfile.github       # Dockerfile per release GitHub
│   │   └── Dockerfile.daFile       # Dockerfile per build dal jar locale
│   └── DOCKER.md                   # Questo file
├── docker-compose.yml              # Avvio dell'immagine locale
├── src/                            # Codice sorgente Java
├── pom.xml                         # Configurazione Maven
└── README.md                       # Documentazione sviluppo Java
```
