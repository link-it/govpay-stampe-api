<p align="center">
<img src="https://www.link.it/wp-content/uploads/2025/01/logo-govpay.svg" alt="GovPay Logo" width="200"/>
</p>

# GovPay - Porta di accesso al sistema pagoPA - Stampe Api

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=link-it_govpay-gde-api&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=link-it_govpay-gde-api)
[![Docker Hub](https://img.shields.io/docker/v/linkitaly/govpay-gde-api?label=Docker%20Hub&logo=docker)](https://hub.docker.com/r/linkitaly/govpay-gde-api)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://raw.githubusercontent.com/link-it/govpay-gde-api/main/LICENSE)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen.svg)](https://spring.io/projects/spring-boot)

## Sommario

Servizio per la stampa degli Avvisi pagoPA e delle ricevute di pagamento

## Istruzioni di compilazione

Il progetto utilizza librerie Spring Boot versione 3.5.9 e JDK 21.

Per la compilazione eseguire il seguente comando, verranno eseguiti anche i test.

``` bash
mvn clean install -P [jar|war]
```

Il profilo permette di selezionare il packaging dei progetti (jar o war).

Per l'avvio dell'applicativo come standalone eseguire:

``` bash
mvn spring-boot:run
```

Per sovrascrivere le proprietà definite nel file `application.properties` utilizzare il seguente sistema:

``` bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=[NUOVO_VALORE] ..."
```

# Configurazione

All'interno del file `application.properties` sono definite le seguenti proprietà:

``` bash
# ----------- SPRING SERVLET ------------

server.port=[Porta su cui esporre il servizio in caso di avvio come applicazione standalone]

spring.mvc.servlet.path=[Basepath servizi]

# Abilitazione Endpoint /actuator/health/liveness
management.endpoints.web.base-path=[Basepath dove esporre i servizi di stato applicazione]

# -------------- BUSINESS LOGIC PROPERTIES  ----------------

stampe.time-zone=[TimeZone dell'applicazione]

```

L'applicazione e' configurata per loggare su standardOutput e' possibile sovrascrivere la configurazione di default utilizzando il meccanismo di override delle properties di spring.

# Formato Logo 

Il logo dell'ente creditore deve essere fornito come stringa nel formato `data:image/png;base64,iVBORw0KGg......` codificato in base 64.

# Avvisi di Pagamento supportati

L'applicazione consente di creare i pdf per le seguenti categorie di [Avvisi PagoPA](https://developer.pagopa.it/pago-pa/guides/avviso-pagamento)

- Rata Unica
- Rate Multiple
- Avvisi con molte rate
- Violazione Cds
- Avvisi Bilingue

Per ognuna delle opzioni precedenti sono disponibili le versioni con e senza bollettino postale.

## Avviso standard

In questa sezione verranno riportati alcuni esempi di richiesta per la generazione degli avvisi standard.

Il servizio e' disponibile alla url:

``` html
POST /standard

{
	json avviso
}
```

### Avviso semplice con rata unica

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

### Avviso semplice con rate multiple

La rate si definiscono valorizzando la lista `instalments`, per ogni rata dovra' essere indicato il numero della rata all'interno del campo obbligatorio `instalment_number`, di seguito un esempio di avviso con due rate:

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

### Avviso semplice bilingue

Per creare un avviso bilingue si deve definire l'elemento `second_language` indicando la seconda lingua desiderata, e il titolo dell'avviso tradotto.

*N.B.*
Il campo obbligatorio `bilinguism` viene attualmente ignorato e l'avviso viene sempre stampato bilingue.


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
    "title": "Payment notice",
    "language": "sl",
    "bilinguism": true
  }
}
```

### Avviso Postale

Per la stampa degli avvisi postali e' necessario valorizzare il campo `postal=true` e indicare i seguenti dati aggiuntivi:

- `creditor.postal_auth_message`: autorizzazione alla stampa in proprio dei bollettini postali
- `full.iban` o `instalment.iban`: informazioni relative all'iban di accredito postale da utilizzare, si utilizzando per la generazione del data matrix: `iban_code` codice iban di accredito, `owner_business_name` nome del proprietario dell'iban di accredito se diverso da `creditor.business_name`, `postal_auth_message` se diversa da quella indicata in `creditor.postal_auth_message`.

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

### Violazione del Codice della Strada

Il servizio e' disponibile alla url:

``` html
POST /cds_violation

{
	json avviso
}
```

L'avviso per la Violazione del Codice della strada deve contenere due importi ridotto e scontato da indicare negli oggetti `discounted_amount` e `reduced_amount`, sono disponibili tutte le opzioni valide per gli avvisi standard.

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

## Configurazione logging

La configurazione del logging è gestita tramite le proprietà definite in `application.properties`:

``` bash
logging.file.name=[Path completo del file di log]
logging.level.it.govpay=[Livello di log: DEBUG, INFO, WARN, ERROR]
```

## Docker

L'immagine Docker è disponibile su Docker Hub: [linkitaly/govpay-stampe-api](https://hub.docker.com/r/linkitaly/govpay-stampe-api)

``` bash
docker pull linkitaly/govpay-stampe-api:latest
```
