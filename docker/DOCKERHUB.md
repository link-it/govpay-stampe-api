<p align="center">
<img src="https://www.link.it/wp-content/uploads/2025/01/logo-govpay.svg" alt="GovPay Logo" width="200"/>
</p>

# GovPay STAMPE API

[![GitHub](https://img.shields.io/badge/GitHub-link--it%2Fgovpay--stampe--api-blue?logo=github)](https://github.com/link-it/govpay-stampe-api)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

API REST Spring Boot per la generazione di **avvisi di pagamento pagoPA** in formato PDF.

## Cos'e GovPay STAMPE API

GovPay STAMPE API e un componente del progetto [GovPay](https://github.com/link-it/govpay) che fornisce API REST per la generazione di avvisi di pagamento pagoPA in formato PDF.

### Funzionalita principali

- Generazione avvisi di pagamento standard (rata unica, rate multiple)
- Generazione avvisi con bollettino postale
- Generazione avvisi bilingue (italiano + seconda lingua)
- Generazione avvisi per violazioni CDS (importo ridotto e scontato)
- Health check e monitoraggio tramite Spring Boot Actuator
- Documentazione OpenAPI/Swagger integrata

## Versioni disponibili

- `latest` - ultima versione stabile
- `1.0.0`

Storico completo delle modifiche consultabile nel [ChangeLog](https://github.com/link-it/govpay-stampe-api/blob/main/ChangeLog) del progetto.

## Quick Start

```bash
docker pull linkitaly/govpay-stampe-api:latest
```

## Documentazione

- [README e istruzioni di configurazione](https://github.com/link-it/govpay-stampe-api/blob/main/README.md)
- [Documentazione Docker](https://github.com/link-it/govpay-stampe-api/blob/main/docker/DOCKER.md)
- [Dockerfile](https://github.com/link-it/govpay-stampe-api/blob/main/docker/govpay-stampe/Dockerfile.github)

## Licenza

GovPay STAMPE API e rilasciato con licenza [GPL v3](https://www.gnu.org/licenses/gpl-3.0).

## Supporto

- **Issues**: [GitHub Issues](https://github.com/link-it/govpay-stampe-api/issues)
- **GovPay**: [govpay.readthedocs.io](https://govpay.readthedocs.io/)

---

Sviluppato da [Link.it s.r.l.](https://www.link.it)
