# Release notes

## v2.0.1

Rilascio del 2026-07-15.

### 🐳 Docker

- Runtime delle immagini Docker portato a **JDK 25** (solo runtime; il target di
  compilazione resta Java 21, build e CI invariati). Base image aggiornata da
  `eclipse-temurin:21-jre-alpine` a `eclipse-temurin:25-jre-alpine` in
  `docker/govpay-stampe/Dockerfile.github` e `Dockerfile.daFile`; in
  `container/Dockerfile` aggiornato il `COPY` del JDK a `eclipse-temurin:25`.
- Verifica su **OpenJDK 25.0.3 LTS**: avvio applicazione OK (Spring Boot 4 /
  Tomcat 11, actuator health `UP`) ed esecuzione dell'intera suite di test su
  JRE 25 (185/185), inclusa la generazione dei PDF (JasperReports/Batik/AWT).

### 📈 Osservabilità

- Aggiunta la dipendenza `micrometer-registry-prometheus`: esposto l'endpoint di
  scrape Prometheus `/actuator/prometheus`.

### 📋 Artefatti rilasciati

- `govpay-stampe-api-2.0.1.jar`
- `govpay-stampe-api-2.0.1.war`
- `release-reports-2.0.1.zip` (OWASP, JaCoCo, OSV SARIF, SBOM CycloneDX, licenses)
- Docker image: `linkitaly/govpay-stampe-api:2.0.1`

### 🔧 Compatibilità

- Runtime Docker: JDK 25 (LTS) — target di compilazione: JDK 21
- Spring Boot 4.1.0 / Spring Framework 7.0.8 / Jackson 3
- Parent BOM: `org.gov4j.govpay:govpay-bom:2.0.0`

---

## v2.0.0

Rilascio del 2026-07-07.

Release maggiore: migrazione dell'intero stack a **Spring Boot 4.x / Spring
Framework 7.x / Jackson 3**. Il salto di major version riflette il cambio di
baseline delle dipendenze (nuovo parent BOM 2.0.0) e le modifiche non
retrocompatibili a livello di API Jackson e Spring.

### 🚀 Migrazione a Spring Boot 4 / Spring Framework 7 / Jackson 3

- **Jackson 2 → Jackson 3** (package `tools.jackson`): l'`ObjectMapper` è ora
  immutabile e configurato tramite builder. La personalizzazione del mapper
  primario (timezone + deserializer custom per i campi `Resource` via
  `SimpleModule`) è migrata da `Jackson2ObjectMapperBuilderCustomizer` al nuovo
  `JsonMapperBuilderCustomizer` in `Application`. Ripristinata l'inclusione dei
  campi `null` nelle risposte (`ALWAYS`), che Jackson 3 omette per default. Il
  supporto a `java.time` è ora integrato in `jackson-databind` (rimossi il modulo
  `JavaTimeModule` e la dipendenza `jackson-datatype-jsr310`). Le API di
  lettura/scrittura sollevano l'unchecked `JacksonException`; i (de)serializer
  custom (`ResourceDeserializer`, `ResourceSerializer`) non possono più ricevere
  un tipo gestito `null` (passata la classe esplicita).
- **Spring Framework 7**: `javax.annotation.Nullable` → JSpecify
  `org.jspecify.annotations.Nullable`; `javax.annotation.PostConstruct` →
  `jakarta.annotation.PostConstruct` nei service.
- **Dipendenze**: rimosso `spring-boot-starter-hateoas` (non utilizzato); rimossa
  `javax.annotation-api`; `swagger-annotations` v1 (`io.swagger`) →
  `io.swagger.core.v3` (versioni gestite dal BOM); aggiunto
  `spring-boot-webmvc-test` per lo slice `@AutoConfigureMockMvc` (in Spring Boot 4
  è in un modulo dedicato).
- **Test**: allineati a Jackson 3 e al nuovo import di `@AutoConfigureMockMvc`;
  helper di test (`ObjectMapperUtils`, `ResourceSerializer`) migrati a Jackson 3.

### 🔒 Sicurezza

Aggiunte due suppression OWASP Dependency-Check (falsi positivi / non applicabili
nel contesto di stampe-api):

- **CVE-2026-6009** (JasperReports, deserializzazione Java → RCE, CVSS 8.7):
  non applicabile. L'applicazione usa esclusivamente template `.jasper` bundlati
  nel JAR e l'API REST accetta solo dati JSON, senza caricamento di template o
  oggetti serializzati esterni (stesso vettore già analizzato per CVE-2025-10492).
  Nessun fix disponibile nella serie 6.x (ultima 6.21.5); il fix è solo nella 7.x,
  incompatibile con `dynamicreports-core 6.20.1` e i template attuali.
- **CVE-2026-54515** (jackson-databind, CVSS 6.9): falso positivo. L'advisory
  ufficiale (GHSA-5jmj-h7xm-6q6v) circoscrive la vulnerabilità a Jackson 3
  `>= 3.1.0, < 3.1.4` (corretta in 3.1.4, versione già in uso via BOM); OSS Index
  la associa erroneamente all'artefatto Jackson 2 transitivo (`jackson-databind
  2.21.4`, presente solo per la generazione della doc OpenAPI), non interessato.

### 🛠️ Pipeline CI

- Cache OWASP Dependency-Check **version-aware**: la chiave include
  `owasp.plugin.version` letta dal pom, così il bump del plugin invalida la cache
  evitando incompatibilità di schema del DB NVD (allineamento a `govpay-common`).
- `refresh-owasp-db.yml`: versione della CLI dependency-check derivata dal pom
  (non più hardcoded) e URL di download aggiornato a `github.com/dependency-check`.

### 📋 Artefatti rilasciati

- `govpay-stampe-api-2.0.0.jar`
- `govpay-stampe-api-2.0.0.war`
- `release-reports-2.0.0.zip` (OWASP, JaCoCo, OSV SARIF, SBOM CycloneDX, licenses)
- Docker image: `linkitaly/govpay-stampe-api:2.0.0`

### 🔧 Compatibilità

- JDK 21
- Spring Boot 4.1.0 / Spring Framework 7.0.8 / Jackson 3
- Parent BOM: `org.gov4j.govpay:govpay-bom:2.0.0`

---

## v1.2.4

Rilascio del 2026-05-12.

### 🔒 Sicurezza / Qualità del codice

Risoluzione segnalazioni SonarCloud `java:S4507` ("Debug features should be
disabled in production"):

- **`BaseAvvisoMapper.java`**: sostituita la chiamata `e.printStackTrace()` nel
  catch del metodo `mapLogo` con `logger.error("Errore durante la lettura del
  logo", e)` via SLF4J, coerente con il pattern già adottato negli altri
  service del progetto (es. `AvvisoPagamentoService`). Dichiarato il `Logger`
  nell'interfaccia, aggiunti i relativi import e rimossi i commenti di debug
  residui.
- **`RestResponseEntityExceptionHandler.java`**: rimossa la chiamata
  `ex.printStackTrace()` nel metodo `handleAllInternalExceptions`. Era
  ridondante perché la riga precedente già esegue `restLogger.error("...", ex)`
  che logga lo stack trace via SLF4J.

### 📋 Artefatti rilasciati

- `govpay-stampe-api-1.2.4.jar`
- `govpay-stampe-api-1.2.4.war`
- `release-reports-1.2.4.zip` (OWASP, JaCoCo, OSV SARIF, SBOM CycloneDX, licenses)
- Docker image: `linkitaly/govpay-stampe-api:1.2.4`

### 🔧 Compatibilità

- JDK 21
- Spring Boot 3.2.5
- Parent BOM: `org.gov4j.govpay:govpay-bom:1.1.3`

---

## v1.2.3

Rilascio del 2026-05-06.

### 🔒 Sicurezza

- **Risoluzione vulnerabilità GHSA-2rwm-xv5j-777p**: sostituita la dipendenza di test
  `org.glassfish:javax.json:1.1.4` (non più mantenuta, segnalata da OSV Scanner tramite
  alias CPE come `org.eclipse.parsson:parsson:1.0.0`) con `org.eclipse.parsson:parsson:1.1.7`.
  Allineati gli import dei test (`UC_1_ViolazioneCdsFailTest`, `UC_3_AvvisoStandardFailTest`,
  `UC_7_RicevutaFailTest`, `UC_11_AvvisoRidottoFailTest`) da `javax.json.*` a `jakarta.json.*`.
- **GHSA-7c3f-cg9x-f3gr** (jasperreports 6.21.5, deserializzazione Java RCE,
  CVSS 9.8): classificata come falso positivo nel contesto di stampe-api perché
  i template `.jasper` sono bundlati nel JAR e l'input proviene da REST API
  validata via `SemanticValidator`, non da fonti esterne non fidate. Eccezione
  registrata in `src/main/resources/osv/falsePositives/osv-scanner.toml` e
  caricata dal job `osv-scan` tramite il flag `--config` (path corretto
  ripristinato).

### 📦 Aggiornamento dipendenze

- Aggiornato parent POM `org.gov4j.govpay:govpay-bom` da **1.1.2** a **1.1.3**.

### 🛠️ Pipeline CI

#### Nuove integrazioni
- **OSV Scanner**: aggiunto job `osv-scan` basato sul reusable workflow
  `google/osv-scanner-action@v2.3.5` per la scansione delle vulnerabilità sulle
  dipendenze dichiarate in `pom.xml`. Esecuzione su push a `main` e tag (override
  via `FORCE_OSV_JOB`); il job blocca `release`.
- **SBOM CycloneDX**: nuovo job `sbom` che genera la SBOM (json + xml) tramite
  `cyclonedx-maven-plugin` (schema 1.6) e pubblica l'artifact `sbom-report`.
  Gated su push a `main` / tag, con override `FORCE_SBOM_JOB` e disabilitazione
  via `DISABLE_SBOM_JOB`.
- **Refresh DB OWASP**: nuovo workflow `refresh-owasp-db.yml` che aggiorna il
  DB NVD ogni notte alle 03:00 UTC (o on-demand) per garantire una cache calda
  alla pipeline principale.

#### Ottimizzazioni e refactoring
- Cache DB OWASP Dependency-Check basata sulla data corrente (invece che su
  `github.sha`), `actions/cache@v5`, flag `-DautoUpdate=false` su cache-hit
  giornaliero per evitare il download completo del DB NVD.
- GitHub Actions allineate a `govpay-bom`: `actions/checkout@v6`,
  `actions/setup-java@v5`, `actions/upload-artifact@v7`,
  `actions/download-artifact@v7`.
- Job `release` ristrutturato: tutti i report (OWASP, JaCoCo, OSV SARIF, SBOM,
  third-party-licenses) raccolti nell'unico archivio `release-reports-<tag>.zip`
  insieme al file `<tag>_actions_run.txt` con il link alla pipeline run. La
  GitHub release allega WAR, JAR e questo ZIP.
- Pipeline allineata strutturalmente a `govpay-fdr-batch`: cleanup header e
  commenti inline, banner di sezione, formattazione uniforme con righe vuote tra
  step, log NVD_API_KEY semplificato.
- Rimosso dal job `osv-scan` il flag `--allow-no-lockfiles` (non necessario
  con `--lockfile` esplicito); corretto il path di `--config` per puntare al
  file effettivo `./src/main/resources/osv/falsePositives/osv-scanner.toml`.

#### Analisi licenze
- Aggiornato `analyze_licenses.py` (allineato a `govpay-fdr-batch`).
- Ripristinate le voci LICENSE_DB necessarie alle dipendenze di stampe-api:
  `BSD License 2.0` (jaxen), `BSD 3-clause License w/nuclear disclaimer`
  (jai-imageio-core), `GNU Lesser General Public Licence` UK spelling
  (jfree jcommon). Reintegrato `net.sourceforge.barbecue:barbecue` nel dict
  di artifact noti.

### 📋 Artefatti rilasciati

- `govpay-stampe-api-1.2.3.jar`
- `govpay-stampe-api-1.2.3.war`
- `release-reports-1.2.3.zip` (OWASP, JaCoCo, OSV SARIF, SBOM CycloneDX, licenses)
- Docker image: `linkitaly/govpay-stampe-api:1.2.3`

### 🔧 Compatibilità

- JDK 21
- Spring Boot 3.2.5
- Parent BOM: `org.gov4j.govpay:govpay-bom:1.1.3`
