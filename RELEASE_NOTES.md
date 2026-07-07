# Release notes

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
