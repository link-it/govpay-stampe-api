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
- **Generated interfaces**: OpenAPI Generator creates `it.govpay.stampe.api.DefaultApi` at build time
- **Controller**: `StampeController` implements `DefaultApi` with two endpoints:
  - `POST /standard` - Standard payment notices (single/installment)
  - `POST /cds_violation` - Traffic violation notices

### Request Processing Flow
1. `StampeController` receives JSON request
2. `SemanticValidator` validates business rules
3. MapStruct mappers (`AvvisoPagamentoMapper`, `ViolazioneCdsMapper`, `AvvisoPagamentoBilingueMapper`) convert API beans to JAXB model objects
4. Service layer generates PDF using JasperReports

### Service Layer
Base class `AvvisoPagamentoService` handles JasperReports PDF generation. Concrete services:
- `AvvisoSempliceService` - Standard notices without postal slip
- `AvvisoPostaleService` - Notices with postal payment slip (bollettino postale)
- `AvvisoBilingueService` - Bilingual notices (IT + second language)
- `ViolazioneCdsService` - Traffic violation notices (reduced/discounted amounts)

### Data Model
Two JAXB model versions generated from XSD schemas:
- `it.govpay.stampe.model.v1` from `xsd/govpayStampe.xsd` - Standard notices
- `it.govpay.stampe.model.v2` from `xsd/avvisiPagamento_v2.xsd` - Bilingual notices

### PDF Templates
JasperReports `.jasper` files in `src/main/resources/`:
- `AvvisoPagamento*.jasper` - Main templates
- `RataUnica*.jasper`, `DoppiaRata*.jasper`, `TriplaRata*.jasper` - Layout variants by installment count
- `*Postale.jasper` - Postal slip variants
- `*V2.jasper` - Bilingual variants
- `ViolazioneCDS.jasper` - Traffic violation template

### Key Technologies
- **MapStruct**: Bean mapping with custom qualifiers in `BaseAvvisoMapper`
- **Lombok**: Used for boilerplate reduction
- **JasperReports + DynamicReports**: PDF generation engine
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
- `server.port` - Default 10003
- `spring.mvc.servlet.path` - API base path (default `/api/v1`)
- `stampe.time-zone` - Timezone for date formatting (default `Europe/Rome`)
- Actuator health endpoints at `/actuator`
