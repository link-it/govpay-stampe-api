# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Coordinates
- govpay-bom groupId is `org.gov4j.govpay`, NOT `it.govpay`
- The shared library is called `govpay-common` (not govpay-commons)
- Always verify groupId and artifactId from the actual pom.xml before making changes

## Project Overview

GovPay Stampe API is a Spring Boot 3.2.5 / JDK 21 microservice that generates pagoPA payment notice PDFs. It exposes REST endpoints to create payment notices for Italian public administration payments, including standard notices, installment plans, postal notices, bilingual notices, and traffic violation notices.

## Maven/BOM Rules
- NEVER remove a dependency version from pom.xml unless you have verified that exact dependency (groupId + artifactId) is managed in the parent BOM
- When overriding transitive dependency versions, use explicit `<dependencyManagement>` entries rather than properties — Spring Boot BOM does not propagate properties to child projects
- After any pom.xml change, run `mvn dependency:tree` to verify resolution

## Build and Run Commands

```bash
# Build (includes tests)
mvn clean install -P jar    # JAR packaging (default)
mvn clean install -P war    # WAR packaging for servlet container

# Run standalone
mvn spring-boot:run

# Run with custom properties
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8080"

# Run tests
mvn test

# Run a single test class
mvn test -Dtest=UC_4_AvvisoStandardTest

# Run a single test method
mvn test -Dtest=UC_4_AvvisoStandardTest#UC_4_01_AvvisoSempliceRataUnicaOk
```

## Architecture

### API Layer
- **OpenAPI spec**: `src/main/resources/govpay-stampe.yaml` defines the REST API
- **Generated interfaces**: OpenAPI Generator creates `it.govpay.stampe.api.PaymentNoticeApi` and `ReceiptApi` at build time
- **Controllers** (endpoints at the context root, no servlet path):
  - `PaymentNoticeController` implements `PaymentNoticeApi`:
    - `POST /standard` - Standard payment notices (single/installment, postal, bilingual)
    - `POST /cds_violation` - Traffic violation notices
  - `ReceiptController` implements `ReceiptApi`: `POST /receipt` - Payment receipts

### Request Processing Flow
1. `PaymentNoticeController` / `ReceiptController` receive the JSON request
2. `SemanticValidator` validates business rules
3. MapStruct mappers (`AvvisoPagamentoMapper`, `ViolazioneCdsMapper`, `AvvisoPagamentoBilingueMapper`) convert API beans to JAXB model objects
4. Service layer generates PDF using Apache FOP (XSLT + XSL-FO)

### Service Layer
Base class `AvvisoPagamentoService` handles PDF generation through `MotoreFop`. Concrete services:
- `AvvisoSempliceService` - Standard notices without postal slip
- `AvvisoPostaleService` - Notices with postal payment slip (bollettino postale)
- `AvvisoBilingueService` - Bilingual notices (IT + second language)
- `ViolazioneCdsService` - Traffic violation notices (reduced/discounted amounts)

### Data Model
Two JAXB model versions generated from XSD schemas:
- `it.govpay.stampe.model.v1` from `xsd/govpayStampe.xsd` - Standard notices
- `it.govpay.stampe.model.v2` from `xsd/avvisiPagamento_v2.xsd` - Bilingual notices

### PDF Templates
XSL-FO layouts in `src/main/resources/fop/`, rendered by `MotoreFop` (Apache FOP, JDK XSLT processor):
- `avviso-semplice.xsl`, `avviso-postale.xsl`, `violazione-cds.xsl`, `ricevuta.xsl`, `avviso-bilingue.xsl` - Main sheets, one per print type
- `RataUnica*.xsl`, `DoppiaRata*.xsl`, `TriplaRata*.xsl`, `*Postale.xsl`, `*V2.xsl` - Layout variants
- `comune.xsl` - Shared templates (`testo-a-capo`, `logo`, ...); `icone/*.svg` - Icons; `TestoFop` - XSLT extension functions (`tj:`) for line breaks and text geometry
- The layouts were derived from the former Jasper templates and are now edited by hand; see `script/fop/README.md` for how to edit and verify them (`RegressioneLayoutTest`)

### Key Technologies
- **MapStruct**: Bean mapping with custom qualifiers in `BaseAvvisoMapper`
- **Lombok**: Used for boilerplate reduction
- **Apache FOP**: PDF generation engine (JasperReports removed, issue #37)
- **ZXing**: QR code and DataMatrix barcode generation

## Test Structure

Tests use Spring Boot Test with MockMvc. Test classes follow `UC_N_*` naming convention:
- `UC_1_*`, `UC_3_*` - Failure/validation tests
- `UC_2_*` - Traffic violation tests
- `UC_4_*` - Standard notice tests
- `UC_5_*` - Bilingual notice tests
- `UC_6_*, UC_7_*, UC_8_*` - PDF output tests

`AvvisiPagamentoFactory` creates test fixtures. Tests use `@ActiveProfiles("test")`.

## Before Implementing New Code
- Always search the existing codebase for abstract base classes, utilities, or services that already solve the problem before writing new implementations
- Specifically check `govpay-common` for shared infrastructure (e.g., AbstractGdeService, ConnettoreService, ConfigurazioneReader)

## Changelog Format
- Follow the standard GovPay changelog format (user had to correct Claude to use it)
- Always include today's date in changelog entries
- Maintain correct chronological ordering (newest first)

## Configuration

Key properties in `application.properties`:
- No `server.port` or servlet path: standalone on 8080, endpoints at the root (`/standard`); as a war, under the context (e.g. `/govpay-stampe-api/standard`)
- `stampe.time-zone` - Timezone for date formatting (default `Europe/Rome`)
- Actuator health endpoints at `/actuator`
