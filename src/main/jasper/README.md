# Sorgenti dei template JasperReports

Questa cartella contiene i sorgenti `.jrxml` dei 28 template usati a runtime, i cui compilati
`.jasper` sono in `src/main/resources/`. I sorgenti non vengono compilati dalla build e non
finiscono nel jar: servono come riferimento per la manutenzione e come specifica per la
sostituzione di JasperReports (issue [#37](https://github.com/link-it/govpay-stampe-api/issues/37)).

Ogni `.jrxml` ha lo stesso nome del `.jasper` corrispondente. Compilati con JasperReports 6.21.5
(espressioni Groovy), producono report equivalenti ai `.jasper` in produzione: il confronto
riscrive entrambi con `JRXmlWriter` e differisce solo per l'attributo di default `whenNoDataType`.

## Provenienza

I 21 template non bilingui sono i sorgenti originali, copiati senza modifiche dall'archivio
interno "Template Jasper":

| Template | Sorgente nell'archivio |
| --- | --- |
| `AvvisoPagamento` | `Avviso_Pagamento_Bollettino_Rate_Subreport_As_Param/AvvisoSemplice/AvvisoPagamento.jrxml` |
| `AvvisoPagamentoPostale` | `Avviso_Pagamento_Bollettino_Rate_Subreport_As_Param/AvvisoPostale/AvvisoPagamento.jrxml` |
| `BollettinoRataPostale` | `Avviso_Pagamento_Bollettino_Rate_Subreport_As_Param/AvvisoPostale/BollettinoRataPostale.jrxml` |
| `BollettinoTriRataPostale` | `Avviso_Pagamento_Bollettino_Rate_Subreport_As_Param/AvvisoPostale/BollettinoTriRataPostale.jrxml` |
| `DoppiaRata` | `Avviso_Pagamento_Bollettino_Rate_Subreport_As_Param/AvvisoSemplice/DoppiaRata.jrxml` |
| `DoppiaRataPostale` | `Avviso_Pagamento_Bollettino_Rate_Subreport_As_Param/AvvisoPostale/DoppiaRataPostale.jrxml` |
| `DoppioFormato` | `Avviso_Pagamento_Bollettino_Rate_Subreport_As_Param/AvvisoSemplice/DoppioFormato.jrxml` |
| `DoppioFormatoPostale` | `Avviso_Pagamento_Bollettino_Rate_Subreport_As_Param/AvvisoPostale/DoppioFormatoPostale.jrxml` |
| `Formato` | `ViolazioneCDS/Formato.jrxml` |
| `RataMultipla` | `Avviso_Pagamento_Bollettino_Rate_Subreport_As_Param/AvvisoSemplice/RataMultipla.jrxml` |
| `RataUnica` | `Avviso_Pagamento_Bollettino_Rate_Subreport_As_Param/AvvisoSemplice/RataUnica.jrxml` |
| `RataUnicaPostale` | `Avviso_Pagamento_Bollettino_Rate_Subreport_As_Param/AvvisoPostale/RataUnicaPostale.jrxml` |
| `RicevutaMae` | `RiceutaMAE/RicevutaMae.jrxml` |
| `RicevutaMae_elenco_voci` | `RiceutaMAE/RicevutaMae_elenco_voci.jrxml` |
| `RidottoScontato` | `ViolazioneCDS/RidottoScontato.jrxml` |
| `Sanzione` | `ViolazioneCDS/Sanzione.jrxml` |
| `TriplaRata` | `Avviso_Pagamento_Bollettino_Rate_Subreport_As_Param/AvvisoSemplice/TriplaRata.jrxml` |
| `TriplaRataPostale` | `Avviso_Pagamento_Bollettino_Rate_Subreport_As_Param/AvvisoPostale/TriplaRataPostale.jrxml` |
| `TriploFormato` | `Avviso_Pagamento_Bollettino_Rate_Subreport_As_Param/AvvisoSemplice/TriploFormato.jrxml` |
| `TriploFormatoPostale` | `Avviso_Pagamento_Bollettino_Rate_Subreport_As_Param/AvvisoPostale/TriploFormatoPostale.jrxml` |
| `ViolazioneCDS` | `ViolazioneCDS/ViolazioneCDS.jrxml` |

I 7 template bilingui sono **ricostruiti** dai compilati di produzione con `JRXmlWriter`:
`AvvisoPagamentoV2`, `BollettinoRataV2`, `DoppiaRataV2`, `DoppioFormatoV2`, `DualBandV2`,
`MonoBandV2`, `RataUnicaV2`. I compilati sono del maggio 2022 e nell'archivio non c'è un sorgente
corrispondente: le cartelle `Avviso Pagamento Bollettino V2` (aprile 2022) e `V3` (agosto 2023)
contengono versioni diverse. La ricostruzione conserva elementi, posizioni, font, grafica SVG ed
espressioni; non conserva commenti e impostazioni dell'editor (zoom, guide).

## Struttura dei template

Il servizio carica il template principale; i sottoreport sono caricati dai template stessi, per
nome dal classpath, tramite il valore di default dei parametri `InputStream`
(`Thread.currentThread().getContextClassLoader().getResourceAsStream("<nome>.jasper")`).

| Template principale | Servizio | Sottoreport |
| --- | --- | --- |
| `AvvisoPagamento` | `AvvisoSempliceService` | `RataUnica`, `DoppiaRata` → `DoppioFormato`, `TriplaRata` → `TriploFormato`, `RataMultipla` → `DoppioFormato`, `TriploFormato` |
| `AvvisoPagamentoPostale` | `AvvisoPostaleService` | `RataUnicaPostale`, `DoppiaRataPostale` → `BollettinoRataPostale`, `DoppioFormatoPostale`; `TriplaRataPostale` → `BollettinoTriRataPostale`, `TriploFormatoPostale` |
| `AvvisoPagamentoV2` | `AvvisoBilingueService` | `MonoBandV2`, `DualBandV2` → `RataUnicaV2`, `DoppiaRataV2` → `BollettinoRataV2`, `DoppioFormatoV2` |
| `ViolazioneCDS` | `ViolazioneCdsService` | `Formato` → `RidottoScontato`, `Sanzione` |
| `RicevutaMae` | `RicevutaService` | `RicevutaMae_elenco_voci` |

## Modificare un template

1. Modificare il `.jrxml` in questa cartella (Jaspersoft Studio compatibile con JasperReports 6.21).
2. Compilarlo con JasperReports 6.21.5 e copiare il `.jasper` prodotto in `src/main/resources/`
   con lo stesso nome.
3. Versionare insieme sorgente e compilato.
