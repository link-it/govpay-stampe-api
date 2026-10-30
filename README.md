<p align="center">
<img src="https://www.link.it/wp-content/uploads/2025/01/logo-govpay.svg" alt="GovPay Logo" width="200"/>
</p>

# GovPay - Porta di accesso al sistema pagoPA - Stampe Api

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=link-it_govpay-stampe-api&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=link-it_govpay-stampe-api)
[![Docker Hub](https://img.shields.io/docker/v/linkitaly/govpay-stampe-api?label=Docker%20Hub&logo=docker)](https://hub.docker.com/r/linkitaly/govpay-stampe-api)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://raw.githubusercontent.com/link-it/govpay-stampe-api/main/LICENSE)
[![Spring Boot](https://img.shields.io/badge/dynamic/xml?url=https%3A%2F%2Fraw.githubusercontent.com%2Flink-it%2Fgovpay-bom%2Fmain%2Fpom.xml&query=%2F%2F*%5Blocal-name()%3D'spring-boot.version'%5D&label=Spring%20Boot&color=brightgreen)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)

API REST che genera in PDF gli avvisi di pagamento pagoPA e le ricevute di pagamento, a partire dai dati ricevuti nella richiesta.

## Sommario

Il servizio riceve in JSON i dati di un avviso o di una ricevuta e restituisce il PDF prodotto con i modelli JasperReports inclusi nel jar. Non ha base dati e non conserva nulla: ogni richiesta è indipendente.

Operazioni esposte (specifica in [src/main/resources/govpay-stampe.yaml](src/main/resources/govpay-stampe.yaml)):

| Operazione | Metodo e path | Descrizione |
|------------|---------------|-------------|
| `createPaymentNotice` | `POST /standard` | Avviso di pagamento: rata unica, rate multiple, con o senza bollettino postale, monolingue o bilingue |
| `createCdsViolationNotice` | `POST /cds_violation` | Avviso per violazione del Codice della Strada, con importo ridotto e scontato |
| `createReceipt` | `POST /receipt` | Ricevuta di pagamento pagoPA |

Le operazioni rispondono `201` con il PDF (`application/pdf`, nome del file in `Content-Disposition`) oppure con un errore in formato `application/problem+json`: `400` per dati non validi, `404` per un path inesistente, `405` per un metodo diverso da `POST`, `415` per un `Content-Type` diverso da `application/json`, `503` per un errore interno nella generazione del PDF. La specifica OpenAPI generata dal servizio è pubblicata su `/v3/api-docs`, con la Swagger UI su `/swagger-ui/index.html`.

Il servizio non applica autenticazione: va esposto solo sulla rete interna dei componenti GovPay.

## Integrazione con GovPay

| Componente | Quando serve | Obbligatorio | Ruolo |
|------------|--------------|--------------|-------|
| [govpay-bom](https://github.com/link-it/govpay-bom) | build | sì | Parent Maven: versioni delle dipendenze e dei plugin |
| [govpay-common](https://github.com/link-it/govpay-common) | build | sì | Libreria comune: transaction id e correlation id nei log e negli header di risposta. Le autoconfigurazioni di DataSource e JPA che la libreria porta con sé sono escluse: il servizio non usa il database di GovPay |
| [govpay-console-api](https://github.com/link-it/govpay-console-api) | runtime | no | Chiama il servizio per i PDF di avvisi e ricevute mostrati nella console (`app.stampe.base-url`) |
| [govpay-pendenze-api](https://github.com/link-it/govpay-pendenze-api) | runtime | no | Chiama il servizio per la stampa degli avvisi delle pendenze (`app.stampe.base-url`) |
| [govpay-portal-api](https://github.com/link-it/govpay-portal-api) | runtime | no | Chiama il servizio per i PDF di avvisi e ricevute del portale di pagamento (`govpay.stampe.base-url`) |
| [govpay-tracciati-batch](https://github.com/link-it/govpay-tracciati-batch) | runtime | no | Chiama il servizio (`POST /standard`) per gli avvisi dei tracciati di caricamento pendenze (`govpay.tracciati.stampe.base-url`) |

I componenti chiamanti contengono una copia di `govpay-stampe.yaml` da cui generano il client: una modifica incompatibile del contratto va riportata anche lì.

## Compilazione

Requisiti: JDK 21 e Maven 3.8.1 o successivo.

```bash
mvn clean install -P jar     # fat jar, per esecuzione standalone e container
mvn clean install -P war     # war, per il deploy su application server
```

Il profilo sceglie il packaging (`jar` è quello di default); il risultato è in `target/govpay-stampe-api.jar` o `target/govpay-stampe-api.war`. La build genera le interfacce delle API dalla specifica OpenAPI e i modelli JAXB dagli XSD in `src/main/resources/xsd`, esegue i test e l'analisi OWASP Dependency-Check delle dipendenze, configurata in `govpay-bom` (i falsi positivi del progetto sono in `src/main/resources/owasp/falsePositives`). Per escludere l'analisi, per esempio in sviluppo:

```bash
mvn clean install -P jar -Dowasp.phase=none
```

## Configurazione

Le proprietà si impostano in `application.properties`, da riga di comando (`-D<proprietà>=<valore>`) o con le variabili d'ambiente equivalenti di Spring Boot (es. `stampe.time-zone` → `STAMPE_TIMEZONE` o `STAMPE_TIME_ZONE`). Con l'immagine Docker si usano le variabili descritte in [Esecuzione con Docker](#esecuzione-con-docker).

### Server e API

| Proprietà | Default | Descrizione |
|-----------|---------|-------------|
| `server.port` | `8080` (`10003` nel container) | Porta HTTP del servizio |
| `spring.mvc.servlet.path` | `/` | Base path delle API |
| `stampe.time-zone` | `Europe/Rome` | Fuso orario usato per le date nel JSON |
| `stampe.debugInputJasper` | `false` | Scrive nel log, a livello `DEBUG`, l'XML passato ai modelli JasperReports: contiene i dati personali del debitore, da usare solo in diagnosi |
| `spring.mvc.date-format` | `yyyy-MM-dd` | Formato delle date nei parametri |
| `spring.mvc.datetime-format` | `yyyy-MM-dd'T'HH:mm:ssZZZ` | Formato di data e ora nei parametri |
| `spring.mvc.throw-exception-if-no-handler-found` | `true` | Risponde con un errore applicativo sui path inesistenti |
| `server.error.whitelabel.enabled` | `true` | Pagina di errore di default di Spring Boot |
| `spring.main.banner-mode` | `off` | Banner di Spring Boot all'avvio |
| `springdoc.api-docs.enabled` | `true` | Pubblica la specifica OpenAPI su `/v3/api-docs` e la Swagger UI |

Le risposte JSON (gli errori) includono anche i campi nulli: lo stabilisce il mapper configurato in `Application`, non `spring.jackson.default-property-inclusion`.

Le etichette stampate negli avvisi, per lingua, sono in [src/main/resources/avvisi.properties](src/main/resources/avvisi.properties).

### Logging

| Proprietà | Default | Descrizione |
|-----------|---------|-------------|
| `logging.file.name` | - (solo console) | File di log; nel container `/var/log/govpay-stampe/govpay-stampe-api.log` |
| `logging.level.it.govpay` | `INFO` | Livello di log dei package GovPay |
| `logging.pattern.level` | `%5p [%X{transactionId:-},%X{correlationId:-}]` | Aggiunge a ogni riga transaction id e correlation id |
| `govpay.logging.tracciatura.enabled` | `true` | Filtro di govpay-common che genera il transaction id, legge il correlation id dagli header `X-Correlation-ID` o `X-Request-ID` (o lo genera) e li restituisce in risposta. Le altre proprietà `govpay.logging.tracciatura.*` sono descritte nel [README di govpay-common](https://github.com/link-it/govpay-common#tracciatura-e-livelli-di-log) |
| `govpay.logging.livelli-dinamici.enabled` | `false` | Livelli di log letti da database: non attivi, il servizio non usa la tabella di configurazione di GovPay |

### Monitoraggio

| Proprietà | Default | Descrizione |
|-----------|---------|-------------|
| `management.server.port` | porta del servizio | Porta dedicata agli endpoint actuator |
| `management.endpoints.web.base-path` | `/actuator` | Base path degli endpoint actuator |
| `management.endpoints.web.exposure.include` | `health,prometheus` (variabile `MANAGEMENT_ENDPOINTS`) | Endpoint actuator esposti |
| `management.endpoint.health.probes.enabled` | `true` | Gruppi `liveness` e `readiness` dell'health check |
| `management.health.livenessState.enabled` | `true` | Stato di liveness |
| `management.health.readinessState.enabled` | `true` | Stato di readiness |
| `management.metrics.distribution.percentiles-histogram.http.server.requests` | `true` | Istogrammi delle latenze, per aggregare i percentili tra più istanze |
| `management.metrics.distribution.slo.http.server.requests` | `100ms,500ms,1s,2s,5s` | Soglie esplicite di latenza, compresa quella SLA pagoPA di 2s |
| `management.metrics.tags.application` | `govpay-stampe-api` | Tag che identifica il servizio nelle metriche |

## Esecuzione

### Jar standalone

Il servizio non usa database e non richiede driver JDBC:

```bash
java -Dserver.port=10003 -jar target/govpay-stampe-api.jar
```

### War su application server

Il war si installa su un servlet container Jakarta EE 11 (es. Tomcat 11) e risponde sotto il context path del deploy, es. `/govpay-stampe-api/standard`. Dove trovarlo:

| Origine | Dove si trova |
|---------|---------------|
| Release (tag) | Allegato alla [release GitHub](https://github.com/link-it/govpay-stampe-api/releases) come `govpay-stampe-api-<tag>.war`, insieme al jar |
| Qualsiasi build | Artifact `govpay-stampe-api` della run di GitHub Actions (retention 90 giorni) |
| Build locale | `mvn clean install -P war`, in `target/govpay-stampe-api.war` |

L'immagine di sviluppo `linkitaly/govpay-stampe-api-dev` contiene solo il jar.

## Esecuzione con Docker

L'immagine è pubblicata su Docker Hub come [linkitaly/govpay-stampe-api](https://hub.docker.com/r/linkitaly/govpay-stampe-api) (release) e `linkitaly/govpay-stampe-api-dev` (sviluppo, a ogni push su `main`). Il servizio risponde sulla porta `10003`; il log è scritto anche in `/var/log/govpay-stampe/govpay-stampe-api.log`.

```bash
docker run -d --name govpay-stampe-api -p 10003:10003 linkitaly/govpay-stampe-api
```

Variabili d'ambiente dell'immagine, oltre alle proprietà Spring Boot in forma di variabile (es. `LOGGING_LEVEL_IT_GOVPAY=DEBUG`):

| Variabile | Obbligatoria | Default | Descrizione |
|-----------|--------------|---------|-------------|
| `SERVER_PORT` | no | `10003` | Porta HTTP del servizio |
| `STAMPE_TIME_ZONE` | no | `Europe/Rome` | Fuso orario delle date nel JSON (`stampe.time-zone`) |
| `MANAGEMENT_SERVER_PORT` | no | porta del servizio | Porta dedicata agli endpoint actuator |
| `MANAGEMENT_ENDPOINTS` | no | `health,prometheus` | Endpoint actuator esposti |
| `GOVPAY_STAMPE_JVM_MAX_RAM_PERCENTAGE` | no | `80` | `-XX:MaxRAMPercentage` della JVM |
| `GOVPAY_STAMPE_JVM_INITIAL_RAM_PERCENTAGE` | no | - | `-XX:InitialRAMPercentage` della JVM |
| `GOVPAY_STAMPE_JVM_MIN_RAM_PERCENTAGE` | no | - | `-XX:MinRAMPercentage` della JVM |
| `GOVPAY_STAMPE_JVM_MAX_METASPACE_SIZE` | no | - | `-XX:MaxMetaspaceSize` della JVM |
| `GOVPAY_STAMPE_JVM_MAX_DIRECT_MEMORY_SIZE` | no | - | `-XX:MaxDirectMemorySize` della JVM |
| `JAVA_OPTS` | no | - | Opzioni aggiuntive della JVM |
| `TZ` | no | `Europe/Rome` | Fuso orario del sistema operativo del container |

Costruzione dell'immagine in locale, scansione delle vulnerabilità e pacchetti inclusi sono descritti in [docker/DOCKER.md](docker/DOCKER.md) e [docker/SCANSIONE.md](docker/SCANSIONE.md).

## Monitoraggio

Gli endpoint actuator rispondono di default sulla porta del servizio:

```bash
curl http://localhost:10003/actuator/health       # stato, con i gruppi liveness e readiness per le probe
curl http://localhost:10003/actuator/prometheus   # metriche in formato Prometheus
```

Per spostarli su una porta dedicata impostare `management.server.port` (nel container `MANAGEMENT_SERVER_PORT`). L'endpoint `loggers`, che permette di cambiare i livelli di log a caldo, non è esposto di default perché il servizio non ha autenticazione: abilitarlo solo insieme a una porta dedicata e non raggiungibile dall'esterno, per esempio `MANAGEMENT_SERVER_PORT=9090` e `MANAGEMENT_ENDPOINTS=health,prometheus,loggers`.

## Avvisi di pagamento

Il servizio produce le seguenti categorie di [avvisi pagoPA](https://developer.pagopa.it/pago-pa/guides/avviso-pagamento), ciascuna con e senza bollettino postale:

- rata unica;
- rate multiple, anche in numero elevato;
- violazione del Codice della Strada;
- avvisi bilingue.

### Logo dell'ente

I campi dei loghi (`first_logo`, `second_logo`, e nelle ricevute `creditor_logo`, `pagopa_logo`) sono di tipo binario: il valore JSON è la codifica base64 della stringa data URI dell'immagine (`data:image/png;base64,iVBORw0KGg...`), cioè la data URI codificata una seconda volta in base64. Negli esempi che seguono il valore è abbreviato in `base64encodedimage`.

### Lingue

Il campo `language` accetta `it`, `en`, `de`, `fr`, `sl`, ma le etichette sono presenti in [avvisi.properties](src/main/resources/avvisi.properties) solo per `it`, `de` e `sl`: con `en` o `fr` gli avvisi bilingue e quelli per violazione del Codice della Strada rispondono `400` (`Label non disponibili per la lingua`). L'avviso standard monolingue non usa le etichette di `avvisi.properties`.

### Avviso standard

```
POST /standard
Content-Type: application/json
```

#### Avviso semplice con rata unica

Gli avvisi semplici vengono creati senza la sezione bollettino postale indicando `postal=false`.
La rata unica si definisce valorizzando l'oggetto `full`:

``` json
{
  "language": "it",
  "first_logo": "base64encodedimage",
  "title": "Avviso di pagamento",
  "creditor": {
    "fiscal_code": "01234567890",
    "business_name": "Comune di Test",
    "department_name": "Ufficio dimostrativo",
    "info_line_1": "Tel 000-000 0000 · Fax 000-000 0000",
    "info_line_2": "Mail info@comune.test.it",
    "cbill_code": "ABCDE"
  },
  "debtor": {
    "fiscal_code": "AAAAAA00A00A000A",
    "full_name": "Nomen Nescio",
    "address_line_1": "Viale dei Giardini, 00",
    "address_line_2": "00000 Roma (RM)"
  },
  "postal": false,
  "full": {
    "amount": 100.50,
    "due_date": "2022-12-31",
    "notice_number": "123456789012345678",
    "qrcode": "PAGOPA|002|123456789012345678|01234567890|10050"
  }
}
```

#### Avviso semplice con rate multiple

Le rate si definiscono valorizzando la lista `instalments`; per ogni rata va indicato il numero nel campo obbligatorio `instalment_number`. Esempio con due rate:

``` json
{
  "language": "it",
  "first_logo": "base64encodedimage",
  "title": "Avviso di pagamento",
  "creditor": {
    "fiscal_code": "01234567890",
    "business_name": "Comune di Test",
    "department_name": "Ufficio dimostrativo",
    "info_line_1": "Tel 000-000 0000 · Fax 000-000 0000",
    "info_line_2": "Mail info@comune.test.it",
    "cbill_code": "ABCDE"
  },
  "debtor": {
    "fiscal_code": "AAAAAA00A00A000A",
    "full_name": "Nomen Nescio",
    "address_line_1": "Viale dei Giardini, 00",
    "address_line_2": "00000 Roma (RM)"
  },
  "postal": false,
  "instalments": [
    {
      "amount": 50.25,
      "due_date": "2022-06-30",
      "notice_number": "987654321098765432",
      "qrcode": "PAGOPA|002|987654321098765432|01234567890|5025",
      "instalment_number": 1
    },
    {
      "amount": 50.25,
      "due_date": "2022-09-30",
      "notice_number": "543216789054321678",
      "qrcode": "PAGOPA|002|543216789054321678|01234567890|5025",
      "instalment_number": 2
    }
  ]
}
```

#### Avviso semplice bilingue

Per creare un avviso bilingue si valorizza l'elemento `second_language` con la seconda lingua (`de` o `sl`, vedi [Lingue](#lingue)) e il titolo tradotto. Il campo obbligatorio `bilinguism` è ignorato: con `second_language` l'avviso è sempre bilingue.

``` json
{
  "language": "it",
  "first_logo": "base64encodedimage",
  "title": "Avviso di pagamento",
  "creditor": {
    "fiscal_code": "01234567890",
    "business_name": "Comune di Test",
    "department_name": "Ufficio dimostrativo",
    "info_line_1": "Tel 000-000 0000 · Fax 000-000 0000",
    "info_line_2": "Mail info@comune.test.it",
    "cbill_code": "ABCDE"
  },
  "debtor": {
    "fiscal_code": "AAAAAA00A00A000A",
    "full_name": "Nomen Nescio",
    "address_line_1": "Viale dei Giardini, 00",
    "address_line_2": "00000 Roma (RM)"
  },
  "postal": false,
  "full": {
    "amount": 100.50,
    "due_date": "2022-12-31",
    "notice_number": "123456789012345678",
    "qrcode": "PAGOPA|002|123456789012345678|01234567890|10050"
  },
  "second_language": {
    "title": "Plačilni nalog",
    "language": "sl",
    "bilinguism": true
  }
}
```

#### Avviso postale

Per gli avvisi con bollettino postale si imposta `postal=true` e si indicano i dati aggiuntivi:

- `creditor.postal_auth_message`: autorizzazione alla stampa in proprio dei bollettini postali;
- `full.iban` o `instalments[].iban`: conto di accredito postale, usato per il data matrix: `iban_code` (IBAN di accredito), `owner_business_name` (intestatario, se diverso da `creditor.business_name`), `postal_auth_message` (se diverso da quello in `creditor.postal_auth_message`).

``` json
{
  "language": "it",
  "first_logo": "base64encodedimage",
  "title": "Avviso di pagamento",
  "creditor": {
    "fiscal_code": "01234567890",
    "business_name": "Comune di Test",
    "department_name": "Ufficio dimostrativo",
    "info_line_1": "Tel 000-000 0000 · Fax 000-000 0000",
    "info_line_2": "Mail info@comune.test.it",
    "cbill_code": "ABCDE",
    "postal_auth_message": "Autorizzazione n. 0000 del 00/00/0000"
  },
  "debtor": {
    "fiscal_code": "AAAAAA00A00A000A",
    "full_name": "Nomen Nescio",
    "address_line_1": "Viale dei Giardini, 00",
    "address_line_2": "00000 Roma (RM)"
  },
  "postal": true,
  "full": {
    "amount": 100.50,
    "due_date": "2022-12-31",
    "notice_number": "123456789012345678",
    "qrcode": "PAGOPA|002|123456789012345678|01234567890|10050",
    "iban": {
      "iban_code": "IT60X0542811101000000123456",
      "owner_business_name": "Comune di Test 2",
      "postal_auth_message": "Autorizzazione n. 0000 del 00/00/0000"
    }
  },
  "instalments": [
    {
      "amount": 50.25,
      "due_date": "2022-06-30",
      "notice_number": "987654321098765432",
      "qrcode": "PAGOPA|002|987654321098765432|01234567890|5025",
      "iban": {
        "iban_code": "IT60X0542811101000000123456",
        "owner_business_name": "Comune di Test 2",
        "postal_auth_message": "Autorizzazione n. 0000 del 00/00/0000"
      }
    },
    {
      "amount": 50.25,
      "due_date": "2022-09-30",
      "notice_number": "543216789054321678",
      "qrcode": "PAGOPA|002|543216789054321678|01234567890|5025",
      "iban": {
        "iban_code": "IT60X0542811101000000123456",
        "owner_business_name": "Comune di Test 2",
        "postal_auth_message": "Autorizzazione n. 0000 del 00/00/0000"
      }
    }
  ]
}
```

#### Altri campi dell'avviso standard

- `informativa_importo`: sostituisce il testo standard della nota sull'importo; con stringa vuota la nota viene omessa. Per gli avvisi bilingue la traduzione va in `second_language.informativa_importo`.
- `reduced_payments`: importi in forma ridotta legati a una soglia temporale (`threshold_type` `ENTRO` o `OLTRE`, `threshold_days`), oltre ai campi di `full`.

### Violazione del Codice della Strada

```
POST /cds_violation
Content-Type: application/json
```

L'avviso contiene due importi, scontato e ridotto, negli oggetti `discounted_amount` e `reduced_amount`; valgono le altre opzioni degli avvisi standard, compreso il bollettino postale.

``` json
{
  "language": "it",
  "first_logo": "base64encodedimage",
  "title": "Avviso di pagamento",
  "creditor": {
    "fiscal_code": "01234567890",
    "business_name": "Comune di Test",
    "department_name": "Ufficio dimostrativo",
    "info_line_1": "Tel 000-000 0000 · Fax 000-000 0000",
    "info_line_2": "Mail info@comune.test.it",
    "cbill_code": "ABCDE",
    "postal_auth_message": "Autorizzazione n. 0000 del 00/00/0000"
  },
  "debtor": {
    "fiscal_code": "AAAAAA00A00A000A",
    "full_name": "Nomen Nescio",
    "address_line_1": "Viale dei Giardini, 00",
    "address_line_2": "00000 Roma (RM)"
  },
  "postal": true,
  "discounted_amount": {
    "amount": 50.25,
    "due_date": "2022-12-31",
    "notice_number": "123456789012345678",
    "qrcode": "PAGOPA|002|123456789012345678|01234567890|5025",
    "iban": {
      "iban_code": "IT60X0542811101000000123456",
      "owner_business_name": "Comune di Test 2",
      "postal_auth_message": "Autorizzazione n. 0000 del 00/00/0000"
    }
  },
  "reduced_amount": {
    "amount": 25.00,
    "due_date": "2022-12-31",
    "notice_number": "987654321098765432",
    "qrcode": "PAGOPA|002|987654321098765432|01234567890|2500",
    "iban": {
      "iban_code": "IT60X0542811101000000123456",
      "owner_business_name": "Comune di Test 2",
      "postal_auth_message": "Autorizzazione n. 0000 del 00/00/0000"
    }
  }
}
```

## Ricevute di pagamento

```
POST /receipt
Content-Type: application/json
```

Esempio di richiesta:

``` json
{
  "creditor_logo": "base64encodedimage",
  "pagopa_logo": "base64encodedimage",
  "organization": {
    "fiscal_code": "01234567890",
    "business_name": "Comune di Test",
    "address": "Via Roma, 1",
    "location": "00100 Roma (RM)"
  },
  "payer": {
    "fiscal_code": "AAAAAA00A00A000A",
    "full_name": "Nomen Nescio",
    "address": "Viale dei Giardini, 00",
    "location": "00000 Roma (RM)"
  },
  "payment_subject": "Pagamento TARI 2024",
  "psp": "Banca di Test S.p.A.",
  "amount": 150.00,
  "operation_date": "15/01/2024 10:30:00",
  "application_date": "15/01/2024",
  "status": "EXECUTED",
  "creditor_reference_id": "01234567890123456",
  "receipt_id": "0123456789012345678901234567890",
  "object_version": "SANP_230",
  "items": [
    {
      "description": "TARI 2024 - Quota fissa",
      "iur": "RF01234567890123456789012345",
      "amount": 100.00,
      "status": "EXECUTED"
    },
    {
      "description": "TARI 2024 - Quota variabile",
      "iur": "RF01234567890123456789012346",
      "amount": 50.00,
      "status": "EXECUTED"
    }
  ]
}
```

### Campi della ricevuta

| Campo | Descrizione | Obbligatorio |
|-------|-------------|--------------|
| `creditor_logo` | Logo dell'ente creditore (vedi [Logo dell'ente](#logo-dellente)) | sì |
| `pagopa_logo` | Logo pagoPA (vedi [Logo dell'ente](#logo-dellente)) | sì |
| `organization` | Dati dell'ente creditore | sì |
| `organization.fiscal_code` | Codice fiscale dell'ente | sì |
| `organization.business_name` | Denominazione dell'ente | sì |
| `organization.address` | Indirizzo dell'ente | sì |
| `organization.location` | Località dell'ente | sì |
| `payer` | Dati del soggetto pagatore | sì |
| `payer.fiscal_code` | Codice fiscale del pagatore | sì |
| `payer.full_name` | Nome completo del pagatore | sì |
| `payer.address` | Indirizzo del pagatore | sì |
| `payer.location` | Località del pagatore | sì |
| `payment_subject` | Causale del pagamento | sì |
| `psp` | Nome del PSP che ha elaborato il pagamento | sì |
| `amount` | Importo totale pagato | sì |
| `operation_date` | Data e ora dell'operazione | sì |
| `application_date` | Data di applicazione | sì |
| `status` | Stato della ricevuta | sì |
| `creditor_reference_id` | Identificativo Univoco Versamento (IUV) | sì |
| `receipt_id` | Codice Contesto Pagamento (CCP) | sì |
| `object_version` | Versione del tracciato pagoPA | sì |
| `items` | Lista delle voci di pagamento | sì |
| `items[].description` | Descrizione della voce | sì |
| `items[].iur` | Identificativo Univoco Riscossione | sì |
| `items[].amount` | Importo della voce | sì |
| `items[].status` | Stato della voce | sì |

Valori ammessi:

- `status` (stato della ricevuta): `EXECUTED` (eseguito), `NOT_EXECUTED` (non eseguito), `PARTIALLY_EXECUTED` (parzialmente eseguito), `EXPIRED` (decorrenza termini), `PARTIALLY_EXPIRED` (decorrenza termini parziale);
- `items[].status` (stato della voce): `EXECUTED`, `NOT_EXECUTED`;
- `object_version` (versione del tracciato): `SANP_240_V2`, `SANP_240`, `SANP_230`.

## Licenza

GovPay Stampe Api è distribuito con licenza [GNU General Public License v3.0](LICENSE).
