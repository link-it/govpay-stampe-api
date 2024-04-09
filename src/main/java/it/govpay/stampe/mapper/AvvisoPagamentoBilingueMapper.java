package it.govpay.stampe.mapper;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.slf4j.Logger;

import it.govpay.stampe.beans.Amount;
import it.govpay.stampe.beans.Creditor;
import it.govpay.stampe.beans.Iban;
import it.govpay.stampe.beans.Instalment;
import it.govpay.stampe.beans.PaymentNotice;
import it.govpay.stampe.config.LabelAvvisiConfiguration.LabelAvvisiProperties;
import it.govpay.stampe.costanti.LabelAvvisiCostanti;
import it.govpay.stampe.model.v2.AvvisoPagamentoInput;
import it.govpay.stampe.model.v2.Etichette;
import it.govpay.stampe.model.v2.PaginaAvvisoDoppia;
import it.govpay.stampe.model.v2.PaginaAvvisoSingola;
import it.govpay.stampe.model.v2.PagineAvviso;
import it.govpay.stampe.model.v2.RataAvviso;
import it.govpay.stampe.utils.AvvisoPagamentoUtils;

@Mapper(componentModel = "spring")
public interface AvvisoPagamentoBilingueMapper extends BaseAvvisoMapper{

	public default String nomePdf(PaymentNotice paymentNotice) {
		String noticeNumber = null;

		if(paymentNotice.getFull() != null) {
			noticeNumber = paymentNotice.getFull().getNoticeNumber();
		} else {
			noticeNumber = paymentNotice.getInstalments().get(0).getNoticeNumber();
		}

		return paymentNotice.getCreditor().getFiscalCode() + "_" + noticeNumber + ".pdf";
	}

	public default AvvisoPagamentoInput toPaymentNoticeAvvisoPagamentoInput(Logger logger, PaymentNotice paymentNotice, LabelAvvisiProperties labelAvvisiProperties) {
		Map<String, String> labelLinguaPrincipale = getLabelLingua(paymentNotice.getLanguage(), labelAvvisiProperties);

		AvvisoPagamentoInput avvisoPagamentoInput = toPaymentNoticeAvvisoPagamentoInput(paymentNotice);

		if(avvisoPagamentoInput != null) {
			Etichette etichette = getEtichette(labelLinguaPrincipale);

			// titolo avviso si imposta nelle etichette
			etichette.setOggettoDelPagamento(paymentNotice.getTitle());

			Etichette etichetteLinguaSecondaria = null;
			Map<String, String> labelLinguaSecondaria = null;

			if(paymentNotice.getSecondLanguage() != null) {
				labelLinguaSecondaria = getLabelLingua(paymentNotice.getSecondLanguage().getLanguage(), labelAvvisiProperties);
				etichetteLinguaSecondaria = getEtichette(labelLinguaSecondaria);
			}
			
			if(etichetteLinguaSecondaria != null) {
				// titolo avviso si imposta nelle etichette
				etichetteLinguaSecondaria.setOggettoDelPagamento(paymentNotice.getSecondLanguage().getTitle());				
			}

			// informazioni postali
			Boolean postale = paymentNotice.getPostal();
			if(postale != null && postale.booleanValue()) {
				// ho gia' caricato tutte le label, spengo il pagamento standard
				etichette.setPagaTerritorio2(getLabel(labelLinguaPrincipale, LabelAvvisiCostanti.LABEL_PAGA_TERRITORIO_POSTE));
				etichette.setPagaApp2(getLabel(labelLinguaPrincipale, LabelAvvisiCostanti.LABEL_PAGA_APP_POSTE));
				if(etichetteLinguaSecondaria != null) {
					etichetteLinguaSecondaria.setPagaTerritorio2(getLabel(labelLinguaSecondaria, LabelAvvisiCostanti.LABEL_PAGA_TERRITORIO_POSTE));
					etichetteLinguaSecondaria.setPagaApp2(getLabel(labelLinguaSecondaria, LabelAvvisiCostanti.LABEL_PAGA_APP_POSTE));
				}

				//				if(StringUtils.isBlank(postale.getIntestatario()))
				avvisoPagamentoInput.setIntestatarioContoCorrentePostale(avvisoPagamentoInput.getEnteCreditore());
				//				else 
				//					input.setIntestatarioContoCorrentePostale(postale.getIntestatario());
			} else {
				// ho gia' caricato tutte le label, spengo il pagamento poste
				etichette.setPagaTerritorio2(getLabel(labelLinguaPrincipale, LabelAvvisiCostanti.LABEL_PAGA_TERRITORIO_STANDARD));
				etichette.setPagaApp2(getLabel(labelLinguaPrincipale, LabelAvvisiCostanti.LABEL_PAGA_APP_STANDARD));
				if(etichetteLinguaSecondaria != null) {
					etichetteLinguaSecondaria.setPagaTerritorio2(getLabel(labelLinguaSecondaria, LabelAvvisiCostanti.LABEL_PAGA_TERRITORIO_STANDARD));
					etichetteLinguaSecondaria.setPagaApp2(getLabel(labelLinguaSecondaria, LabelAvvisiCostanti.LABEL_PAGA_APP_STANDARD));
				}
			}

			it.govpay.stampe.model.v2.AvvisoPagamentoInput.Etichette etichetteAvviso = new it.govpay.stampe.model.v2.AvvisoPagamentoInput.Etichette();
			etichetteAvviso.setItaliano(etichette);
			etichetteAvviso.setTraduzione(etichetteLinguaSecondaria);
			avvisoPagamentoInput.setEtichette(etichetteAvviso );

			avvisoPagamentoInput.setPagine(new PagineAvviso());

			creaRatePerAvvisoBilingue(logger, paymentNotice, avvisoPagamentoInput, labelLinguaPrincipale, labelLinguaSecondaria); 


		}

		return avvisoPagamentoInput;
	}

	public default void creaRatePerAvvisoBilingue(Logger logger, PaymentNotice paymentNotice, AvvisoPagamentoInput avvisoPagamentoInput, Map<String, String> labelLinguaPrincipale, Map<String, String> labelLinguaSecondaria) {
		// nota importo viene letta dalle properties e poi viene inserita in un punto diverso a seconda della presenza o meno della rata unica
		String labelNotaImportoTra = null;
		String labelNotaImporto = labelLinguaPrincipale.get(LabelAvvisiCostanti.LABEL_NOTA_IMPORTO);

		if(labelLinguaSecondaria != null) {
			labelNotaImporto = labelLinguaSecondaria.get(LabelAvvisiCostanti.LABEL_NOTA_IMPORTO);
		}

		boolean addNota1 = true;

		// rata unica
		RataAvviso rataUnica = null;
		if(paymentNotice.getFull() != null) {
			rataUnica = amountToRataWithLabels(logger, paymentNotice.getFull(), paymentNotice.getPostal(), labelLinguaPrincipale, labelLinguaSecondaria, avvisoPagamentoInput, paymentNotice.getCreditor());

			PaginaAvvisoSingola pagina = new PaginaAvvisoSingola();
			pagina.setRata(rataUnica);

			avvisoPagamentoInput.getPagine().getSingolaOrDoppia().add(pagina);
		} else {
			addNota1 = false;
		}

		// rate
		List<Instalment> instalments = paymentNotice.getInstalments();

		if(instalments != null) {
			// calcolo il numero delle rate
			int numeroRate = 0;
			for (Instalment instalment : instalments) {
				if(instalment.getInstalmentNumber() != null) {
					numeroRate ++;
				}
			}
			// controllo se sono tutte rate
			boolean soloRate = rataUnica == null && numeroRate == instalments.size();

			if(!instalments.isEmpty() && soloRate) {
				// numero di versamenti pari devo creara la pagina principale con i dati della prima rata
				if(instalments.size() % 2 == 0) {
					logger.debug("Numero di versamenti con rate e' pari, riporto i dati della prima rata anche nella pagina principale.");	
					PaginaAvvisoSingola pagina = new PaginaAvvisoSingola();
					Instalment versamento = instalments.get(0); // leggo alcuni dati dalla prima rata

					avvisoPagamentoInput.getEtichette().getItaliano().setNota1(getLabel(labelLinguaPrincipale, LabelAvvisiCostanti.LABEL_PRIMA_RATA));

					if(labelLinguaSecondaria != null ) {
						avvisoPagamentoInput.getEtichette().getTraduzione().setNota1(getLabel(labelLinguaSecondaria, LabelAvvisiCostanti.LABEL_PRIMA_RATA));
					}

					addNota1 = false;

					RataAvviso rata = instalmentToRata(versamento);

					// calcolo dell'importo totale
					BigDecimal importoTotale = BigDecimal.ZERO;
					for (Instalment instalment : instalments) {
						importoTotale = importoTotale.add(BigDecimal.valueOf(instalment.getAmount()));
					}
					rata.setImporto(importoTotale.doubleValue());

					pagina.setRata(rata);

					avvisoPagamentoInput.getPagine().getSingolaOrDoppia().add(pagina);
				} else { // versamenti dispari la prima pagina e la prima rata coincidono
					logger.debug("Numero di versamenti con rate e' dispari, la prima pagina coincide con la prima rata.");	
					Instalment instalment = instalments.remove(0);
					PaginaAvvisoSingola pagina = new PaginaAvvisoSingola();

					RataAvviso rata = instalmentToRataWithLabels(logger, instalment, paymentNotice.getPostal(), labelLinguaPrincipale, labelLinguaSecondaria, avvisoPagamentoInput, paymentNotice.getCreditor());

					rata.setScadenza(getLabel(labelLinguaPrincipale, LabelAvvisiCostanti.LABEL_PRIMA_RATA));
					if(labelLinguaSecondaria != null)
						rata.setScadenzaTra(getLabel(labelLinguaSecondaria, LabelAvvisiCostanti.LABEL_PRIMA_RATA));

					avvisoPagamentoInput.getEtichette().getItaliano().setEntro(getLabel(labelLinguaPrincipale, LabelAvvisiCostanti.LABEL_ENTRO_IL));

					if(labelLinguaSecondaria != null ) {
						avvisoPagamentoInput.getEtichette().getTraduzione().setEntro(getLabel(labelLinguaSecondaria, LabelAvvisiCostanti.LABEL_ENTRO_IL));
					}

					pagina.setRata(rata);
					avvisoPagamentoInput.getPagine().getSingolaOrDoppia().add(pagina);
				}
			}
			
			if(numeroRate > 0) {
				avvisoPagamentoInput.getEtichette().getItaliano().setNota1(getLabel(labelLinguaPrincipale, LabelAvvisiCostanti.LABEL_NOTA_PRIMA_RATA, numeroRate));
				
				if(labelLinguaSecondaria != null ) {
					avvisoPagamentoInput.getEtichette().getTraduzione().setNota1(getLabel(labelLinguaSecondaria, LabelAvvisiCostanti.LABEL_NOTA_PRIMA_RATA, numeroRate));
				}
				addNota1 = false;
			}
			
			logger.debug("Inserisco i versamenti due per pagina");	
			// 2 rate per pagina
			while(instalments.size() > 1) {
				Instalment v1 = instalments.remove(0);
				Instalment v2 = instalments.remove(0);
				PaginaAvvisoDoppia pagina = new PaginaAvvisoDoppia();
				RataAvviso rataSx = instalmentToRataWithLabels(logger, v1, paymentNotice.getPostal(), labelLinguaPrincipale, labelLinguaSecondaria, avvisoPagamentoInput, paymentNotice.getCreditor());
				RataAvviso rataDx = instalmentToRataWithLabels(logger, v2, paymentNotice.getPostal(), labelLinguaPrincipale, labelLinguaSecondaria, avvisoPagamentoInput, paymentNotice.getCreditor());
				
				if(v1.getInstalmentNumber() != null && v2.getInstalmentNumber() != null) {
					// Titolo della pagina con 2 Rate
					String titoloRateIta = getLabel(labelLinguaPrincipale, LabelAvvisiCostanti.LABEL_ELENCO_RATE_2, v1.getInstalmentNumber(), v2.getInstalmentNumber());
					rataSx.setElencoRate(titoloRateIta);
					rataDx.setElencoRate(titoloRateIta);
					if(labelLinguaSecondaria != null) {
						String titoloRateSL = getLabel(labelLinguaSecondaria, LabelAvvisiCostanti.LABEL_ELENCO_RATE_2, v1.getInstalmentNumber(), v2.getInstalmentNumber());
						rataSx.setElencoRateTra(titoloRateSL);
						rataDx.setElencoRateTra(titoloRateSL);
					}
				}
				
				pagina.getRata().add(rataSx);
				pagina.getRata().add(rataDx);
				avvisoPagamentoInput.getPagine().getSingolaOrDoppia().add(pagina);
			}

			logger.debug("Inserisco i versamenti residui uno per pagina");	
			// rata rimasta
			if(instalments.size() == 1) {
				Instalment v1 = instalments.remove(0);
				PaginaAvvisoDoppia pagina = new PaginaAvvisoDoppia();
				RataAvviso rataSx = instalmentToRataWithLabels(logger, v1, paymentNotice.getPostal(), labelLinguaPrincipale, labelLinguaSecondaria, avvisoPagamentoInput, paymentNotice.getCreditor());
				
				if(v1.getInstalmentNumber() != null) {
					// Titolo della pagina con 2 Rate
					String titoloRateIta = getLabel(labelLinguaPrincipale, LabelAvvisiCostanti.LABEL_ELENCO_RATE_1, v1.getInstalmentNumber());
					rataSx.setElencoRate(titoloRateIta);
					if(labelLinguaSecondaria != null) {
						String titoloRateSL = getLabel(labelLinguaSecondaria, LabelAvvisiCostanti.LABEL_ELENCO_RATE_1, v1.getInstalmentNumber());
						rataSx.setElencoRateTra(titoloRateSL);
					}
				}
				
				pagina.getRata().add(rataSx);
				
				avvisoPagamentoInput.getPagine().getSingolaOrDoppia().add(pagina);
			}
		}



		// Per la rata unica si utilizza la nota 1, per le rate la nota 2 
		if(addNota1) {
			avvisoPagamentoInput.getEtichette().getItaliano().setNota1(labelNotaImporto);

			if(labelLinguaSecondaria != null) {
				avvisoPagamentoInput.getEtichette().getTraduzione().setNota1(labelNotaImportoTra);
			}
		} else {
			avvisoPagamentoInput.getEtichette().getItaliano().setNota2(labelNotaImporto);

			if(labelLinguaSecondaria != null) {
				avvisoPagamentoInput.getEtichette().getTraduzione().setNota2(labelNotaImportoTra);
			}
		}
	}

	@Mapping(target = "logoEnte", source="firstLogo", qualifiedByName = "mapLogo")
	@Mapping(target = "logoEnteSecondario", source="secondLogo", qualifiedByName = "mapLogo")
	@Mapping(target = "cfEnte", source="paymentNotice.creditor.fiscalCode")
	@Mapping(target = "enteCreditore", source="paymentNotice.creditor.businessName")
	@Mapping(target = "settoreEnte", source="paymentNotice.creditor.departmentName")
	@Mapping(target = "infoEnte", source="paymentNotice.creditor", qualifiedByName = "mapInfoEnte")
	@Mapping(target = "cbill", source="paymentNotice.creditor.cbillCode")
	@Mapping(target = "cfDestinatario", source="paymentNotice.debtor.fiscalCode")
	@Mapping(target = "nomeCognomeDestinatario", source="paymentNotice.debtor.fullName")
	@Mapping(target = "indirizzoDestinatario1", source="paymentNotice.debtor.addressLine1")
	@Mapping(target = "indirizzoDestinatario2", source="paymentNotice.debtor.addressLine2")
	public AvvisoPagamentoInput toPaymentNoticeAvvisoPagamentoInput(PaymentNotice paymentNotice);

	@Mapping(target = "importo", source="amount")
	@Mapping(target = "data", source="dueDate", qualifiedByName = "mapData")
	@Mapping(target = "codiceAvviso", source="noticeNumber", qualifiedByName = "mapNumeroAvviso")
	@Mapping(target = "qrCode", source="qrcode")
	@Mapping(target = "numeroRata", source="instalmentNumber")
	public RataAvviso instalmentToRata(Instalment instalment);

	public default RataAvviso instalmentToRataWithLabels(Logger logger, Instalment instalment, Boolean postale,  Map<String, String> labelLinguaPrincipale, Map<String, String> labelLinguaSecondaria,  AvvisoPagamentoInput avvisoPagamentoInput, Creditor creditor) {
		RataAvviso rataAvviso = instalmentToRata(instalment);

		impostaLabelsNellaRataAvviso(logger, rataAvviso, postale, labelLinguaPrincipale, labelLinguaSecondaria);
		
		impostaLabelsPostaliNellaRataAvviso(logger, rataAvviso, postale, labelLinguaPrincipale, labelLinguaSecondaria, instalment.getNoticeNumber(), instalment.getIban(), instalment.getAmount(), avvisoPagamentoInput, creditor);

		return rataAvviso;
	}

	@Mapping(target = "importo", source="amount")
	@Mapping(target = "data", source="dueDate", qualifiedByName = "mapData")
	@Mapping(target = "codiceAvviso", source="noticeNumber", qualifiedByName = "mapNumeroAvviso")
	@Mapping(target = "qrCode", source="qrcode")
	public RataAvviso amountToRataV2(Amount amount);

	public default RataAvviso amountToRataWithLabels(Logger logger, Amount amount, Boolean postale,  Map<String, String> labelLinguaPrincipale, Map<String, String> labelLinguaSecondaria, AvvisoPagamentoInput avvisoPagamentoInput, Creditor creditor) {
		RataAvviso rataAvviso = amountToRataV2(amount);

		impostaLabelsNellaRataAvviso(logger, rataAvviso, postale, labelLinguaPrincipale, labelLinguaSecondaria);
		
		impostaLabelsPostaliNellaRataAvviso(logger, rataAvviso, postale, labelLinguaPrincipale, labelLinguaSecondaria, amount.getNoticeNumber(), amount.getIban(), amount.getAmount(), avvisoPagamentoInput, creditor);

		return rataAvviso;

	}

	public default void impostaLabelsNellaRataAvviso(Logger logger, RataAvviso rata, Boolean postale,  Map<String, String> labelLinguaPrincipale, Map<String, String> labelLinguaSecondaria) {
		rata.setScadenza(getLabel(labelLinguaPrincipale, LabelAvvisiCostanti.LABEL_RATA_UNICA_ENTRO_IL));
		if(labelLinguaSecondaria != null)
			rata.setScadenzaTra(getLabel(labelLinguaSecondaria, LabelAvvisiCostanti.LABEL_RATA_UNICA_ENTRO_IL));


		if(rata.getNumeroRata() != null) {
			rata.setNumeroRata(getLabel(labelLinguaPrincipale, LabelAvvisiCostanti.LABEL_NUMERO_RATA, rata.getNumeroRata()));
			if(labelLinguaSecondaria != null)
				rata.setNumeroRataTra(getLabel(labelLinguaSecondaria, LabelAvvisiCostanti.LABEL_NUMERO_RATA, rata.getNumeroRata()));

			rata.setScadenza(getLabel(labelLinguaPrincipale, LabelAvvisiCostanti.LABEL_RATA_ENTRO_IL, rata.getNumeroRata()));
			if(labelLinguaSecondaria != null)
				rata.setScadenzaTra(getLabel(labelLinguaSecondaria, LabelAvvisiCostanti.LABEL_RATA_ENTRO_IL, rata.getNumeroRata()));

			rata.setScadenzaUnica(getLabel(labelLinguaPrincipale, LabelAvvisiCostanti.LABEL_RATA_UNICA_ENTRO_IL));
			if(labelLinguaSecondaria != null)
				rata.setScadenzaTra(getLabel(labelLinguaSecondaria, LabelAvvisiCostanti.LABEL_RATA_UNICA_ENTRO_IL));
		}
	}

	public default void impostaLabelsPostaliNellaRataAvviso(Logger logger, RataAvviso rataAvviso, Boolean postale,  Map<String, String> labelLinguaPrincipale, Map<String, String> labelLinguaSecondaria,
			String noticeNumber, Iban iban, Double importo, AvvisoPagamentoInput avvisoPagamentoInput, Creditor creditor) {
		if(postale != null && postale.booleanValue()) {
			
			String numeroCC = AvvisoPagamentoUtils.getNumeroCCDaIban(iban.getIbanCode());
			rataAvviso.setDataMatrix(AvvisoPagamentoUtils.creaDataMatrix(noticeNumber, numeroCC, importo,
					avvisoPagamentoInput.getCfEnte(),
					avvisoPagamentoInput.getCfDestinatario(),
					avvisoPagamentoInput.getNomeCognomeDestinatario(),
					avvisoPagamentoInput.getEtichette().getItaliano().getOggettoDelPagamento()));
			rataAvviso.setNumeroCcPostale(numeroCC);
			// codice avviso gia' diviso in gruppi di 4
			rataAvviso.setCodiceAvvisoPostale(rataAvviso.getCodiceAvviso()); 
			rataAvviso.setAutorizzazione(AvvisoPagamentoUtils.getAutorizzazionePoste(creditor.getPostalAuthMessage(), iban.getPostalAuthMessage()));
			if(StringUtils.isBlank(iban.getOwnerBusinessName()))
				avvisoPagamentoInput.setIntestatarioContoCorrentePostale(avvisoPagamentoInput.getEnteCreditore());
			else 
				avvisoPagamentoInput.setIntestatarioContoCorrentePostale(iban.getOwnerBusinessName());
		}
	}

	public default Etichette getEtichette(Map<String, String> labelsLingua) {
		if(labelsLingua == null) {
			return null;
		}

		Etichette etichette = new Etichette();

		etichette.setAvvisoPagamento(labelsLingua.get(LabelAvvisiCostanti.LABEL_AVVISO_PAGAMENTO));
		etichette.setCanali(labelsLingua.get(LabelAvvisiCostanti.LABEL_CANALI));
		etichette.setCodiceAvviso(labelsLingua.get(LabelAvvisiCostanti.LABEL_CODICE_AVVISO));
		etichette.setCodiceCbill(labelsLingua.get(LabelAvvisiCostanti.LABEL_CODICE_CBILL));
		etichette.setCodiceFiscaleEnte(labelsLingua.get(LabelAvvisiCostanti.LABEL_CODICE_FISCALE_ENTE));
		etichette.setCome(labelsLingua.get(LabelAvvisiCostanti.LABEL_COME));
		etichette.setDove(labelsLingua.get(LabelAvvisiCostanti.LABEL_DOVE));
		etichette.setDescrizione(labelsLingua.get(LabelAvvisiCostanti.LABEL_DESCRIZIONE));
		etichette.setDestinatario(labelsLingua.get(LabelAvvisiCostanti.LABEL_DESTINATARIO));
		etichette.setDestinatarioAvviso(labelsLingua.get(LabelAvvisiCostanti.LABEL_DESTINATARIO_AVVISO));
		etichette.setEnteCreditore(labelsLingua.get(LabelAvvisiCostanti.LABEL_ENTE_CREDITORE));
		etichette.setEntro(labelsLingua.get(LabelAvvisiCostanti.LABEL_ENTRO_IL));
		etichette.setImporto(labelsLingua.get(LabelAvvisiCostanti.LABEL_IMPORTO));
		etichette.setIntestatario(labelsLingua.get(LabelAvvisiCostanti.LABEL_INTESTATARIO));
		etichette.setNota(labelsLingua.get(LabelAvvisiCostanti.LABEL_NOTA));
		//		etichette.setNota2(labelsLingua.get(LabelAvvisiCostanti.LABEL_NOTA_IMPORTO))
		//etichette.setNotaPrimaRata(labelsLingua.get(LabelAvvisiCostanti.LABEL_NOTA_PRIMA_RATA))
		//		etichette.setNotaRataUnica(labelsLingua.get(LabelAvvisiCostanti.LABEL_NOTA_RATA_UNICA))
		etichette.setOggetto(labelsLingua.get(LabelAvvisiCostanti.LABEL_OGGETTO));
		etichette.setPagaApp(labelsLingua.get(LabelAvvisiCostanti.LABEL_PAGA_APP));
		etichette.setPagaTerritorio(labelsLingua.get(LabelAvvisiCostanti.LABEL_PAGA_TERRITORIO));
		etichette.setPrimaRata(labelsLingua.get(LabelAvvisiCostanti.LABEL_PRIMA_RATA));
		etichette.setQuantoQuando(labelsLingua.get(LabelAvvisiCostanti.LABEL_QUANTO_QUANDO));
		etichette.setTipo(labelsLingua.get(LabelAvvisiCostanti.LABEL_TIPO));

		return etichette;
	}

	public default String getLabel(Map<String, String> labelsLingua, String nomeLabel, Object ... parameter) {
		String propertyValue = labelsLingua.get(nomeLabel);

		if(parameter != null && parameter.length > 0) {
			return MessageFormat.format(propertyValue, parameter);
		}

		return propertyValue;
	}
}
