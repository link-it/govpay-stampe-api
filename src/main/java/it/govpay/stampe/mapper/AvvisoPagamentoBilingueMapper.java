package it.govpay.stampe.mapper;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
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
import it.govpay.stampe.beans.ThresholdPayment;
import it.govpay.stampe.beans.ThresholdType;
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
		} else if(paymentNotice.getInstalments() != null && !paymentNotice.getInstalments().isEmpty()) {
			noticeNumber = paymentNotice.getInstalments().get(0).getNoticeNumber();
		} else if(paymentNotice.getReducedPayments() != null && !paymentNotice.getReducedPayments().isEmpty()) {
			noticeNumber = paymentNotice.getReducedPayments().get(0).getNoticeNumber();
		}

		return paymentNotice.getCreditor().getFiscalCode() + "_" + noticeNumber + ".pdf";
	}

	public default AvvisoPagamentoInput toPaymentNoticeAvvisoPagamentoInput(Logger logger, PaymentNotice paymentNotice, LabelAvvisiProperties labelAvvisiProperties) {
		Map<String, String> labelLinguaPrincipale = getLabelLingua(paymentNotice.getLanguage(), labelAvvisiProperties);

		AvvisoPagamentoInput avvisoPagamentoInput = toPaymentNoticeAvvisoPagamentoInput(paymentNotice);

		if(avvisoPagamentoInput == null) {
			return null;
		}

		Etichette etichette = getEtichette(labelLinguaPrincipale);

		// titolo avviso si imposta nelle etichette
		etichette.setOggettoDelPagamento(paymentNotice.getTitle());

		// le label della lingua secondaria sono valorizzate solo per gli avvisi bilingue
		Map<String, String> labelLinguaSecondaria = null;
		if(paymentNotice.getSecondLanguage() != null) {
			labelLinguaSecondaria = getLabelLingua(paymentNotice.getSecondLanguage().getLanguage(), labelAvvisiProperties);
		}

		Etichette etichetteLinguaSecondaria = creaEtichetteLinguaSecondaria(paymentNotice, labelLinguaSecondaria);

		// informazioni postali
		impostaLabelsCanaliDiPagamento(paymentNotice.getPostal(), etichette, labelLinguaPrincipale, etichetteLinguaSecondaria, labelLinguaSecondaria);

		it.govpay.stampe.model.v2.AvvisoPagamentoInput.Etichette etichetteAvviso = new it.govpay.stampe.model.v2.AvvisoPagamentoInput.Etichette();
		etichetteAvviso.setItaliano(etichette);
		etichetteAvviso.setTraduzione(etichetteLinguaSecondaria);
		avvisoPagamentoInput.setEtichette(etichetteAvviso );

		avvisoPagamentoInput.setPagine(new PagineAvviso());

		List<ThresholdPayment> reducedPayments = paymentNotice.getReducedPayments();
		boolean hasReducedPayments = reducedPayments != null && !reducedPayments.isEmpty();

		if(hasReducedPayments) {
			creaRateRidottePerAvvisoBilingue(logger, paymentNotice, avvisoPagamentoInput, labelLinguaPrincipale, labelLinguaSecondaria);
		} else {
			creaRatePerAvvisoBilingue(logger, paymentNotice, avvisoPagamentoInput, labelLinguaPrincipale, labelLinguaSecondaria);
		}

		return avvisoPagamentoInput;
	}

	/***
	 * Crea le etichette della lingua secondaria: servono sia la seconda lingua dell'avviso sia
	 * le label configurate per quella lingua, altrimenti non c'e' traduzione e viene
	 * restituito null.
	 *
	 */
	public default Etichette creaEtichetteLinguaSecondaria(PaymentNotice paymentNotice, Map<String, String> labelLinguaSecondaria) {
		if(paymentNotice.getSecondLanguage() == null || labelLinguaSecondaria == null) {
			return null;
		}

		Etichette etichetteLinguaSecondaria = getEtichette(labelLinguaSecondaria);

		// titolo avviso si imposta nelle etichette
		etichetteLinguaSecondaria.setOggettoDelPagamento(paymentNotice.getSecondLanguage().getTitle());

		return etichetteLinguaSecondaria;
	}

	/***
	 * Imposta le label dei canali di pagamento: per l'avviso postale si spengono le label del
	 * pagamento standard, negli altri casi si spengono quelle del pagamento presso le poste.
	 *
	 */
	public default void impostaLabelsCanaliDiPagamento(Boolean postale, Etichette etichette, Map<String, String> labelLinguaPrincipale,
			Etichette etichetteLinguaSecondaria, Map<String, String> labelLinguaSecondaria) {
		String labelPagaTerritorio = LabelAvvisiCostanti.LABEL_PAGA_TERRITORIO_STANDARD;
		String labelPagaApp = LabelAvvisiCostanti.LABEL_PAGA_APP_STANDARD;

		if(Boolean.TRUE.equals(postale)) {
			labelPagaTerritorio = LabelAvvisiCostanti.LABEL_PAGA_TERRITORIO_POSTE;
			labelPagaApp = LabelAvvisiCostanti.LABEL_PAGA_APP_POSTE;
		}

		etichette.setPagaTerritorio2(getLabel(labelLinguaPrincipale, labelPagaTerritorio));
		etichette.setPagaApp2(getLabel(labelLinguaPrincipale, labelPagaApp));

		if(etichetteLinguaSecondaria != null) {
			etichetteLinguaSecondaria.setPagaTerritorio2(getLabel(labelLinguaSecondaria, labelPagaTerritorio));
			etichetteLinguaSecondaria.setPagaApp2(getLabel(labelLinguaSecondaria, labelPagaApp));
		}
	}

	public default void creaRatePerAvvisoBilingue(Logger logger, PaymentNotice paymentNotice, AvvisoPagamentoInput avvisoPagamentoInput, Map<String, String> labelLinguaPrincipale, Map<String, String> labelLinguaSecondaria) {
		// nota importo viene letta dalle properties e poi viene inserita in un punto diverso a seconda della presenza o meno della rata unica
		String labelNotaImportoTra = null;
		String labelNotaImporto = getLabelNotaImporto(labelLinguaPrincipale, labelLinguaSecondaria);

		// rata unica
		RataAvviso rataUnica = creaPaginaRataUnica(logger, paymentNotice, avvisoPagamentoInput, labelLinguaPrincipale, labelLinguaSecondaria);

		// Per la rata unica si utilizza la nota 1, per le rate la nota 2
		boolean addNota1 = rataUnica != null;

		// rate
		List<Instalment> instalments = paymentNotice.getInstalments();

		if(instalments != null) {
			// calcolo il numero delle rate
			int numeroRate = contaRate(instalments);

			// controllo se sono tutte rate
			boolean soloRate = rataUnica == null && numeroRate == instalments.size();

			if(!instalments.isEmpty() && soloRate) {
				creaPaginaPrincipaleConSoleRate(logger, paymentNotice, avvisoPagamentoInput, labelLinguaPrincipale, labelLinguaSecondaria);
			}

			if(numeroRate > 0) {
				impostaNotaPrimaRata(avvisoPagamentoInput, labelLinguaPrincipale, labelLinguaSecondaria, numeroRate);
				addNota1 = false;
			}

			creaPagineRate(logger, paymentNotice, avvisoPagamentoInput, labelLinguaPrincipale, labelLinguaSecondaria);
		}

		impostaNotaImporto(avvisoPagamentoInput, labelLinguaSecondaria, labelNotaImporto, labelNotaImportoTra, addNota1);
	}

	/***
	 * Restituisce la nota sull'importo: quando e' presente la lingua secondaria viene utilizzata
	 * la label della lingua secondaria.
	 *
	 */
	public default String getLabelNotaImporto(Map<String, String> labelLinguaPrincipale, Map<String, String> labelLinguaSecondaria) {
		if(labelLinguaSecondaria != null) {
			return labelLinguaSecondaria.get(LabelAvvisiCostanti.LABEL_NOTA_IMPORTO);
		}

		return labelLinguaPrincipale.get(LabelAvvisiCostanti.LABEL_NOTA_IMPORTO);
	}

	/***
	 * Crea la pagina principale con la rata unica, se presente, e restituisce la rata creata.
	 *
	 */
	public default RataAvviso creaPaginaRataUnica(Logger logger, PaymentNotice paymentNotice, AvvisoPagamentoInput avvisoPagamentoInput,
			Map<String, String> labelLinguaPrincipale, Map<String, String> labelLinguaSecondaria) {
		if(paymentNotice.getFull() == null) {
			return null;
		}

		RataAvviso rataUnica = amountToRataWithLabels(logger, paymentNotice.getFull(), paymentNotice.getPostal(), labelLinguaPrincipale, labelLinguaSecondaria, avvisoPagamentoInput, paymentNotice.getCreditor());

		PaginaAvvisoSingola pagina = new PaginaAvvisoSingola();
		pagina.setRata(rataUnica);

		avvisoPagamentoInput.getPagine().getSingolaOrDoppia().add(pagina);

		return rataUnica;
	}

	/***
	 * Restituisce il numero di versamenti che sono rate di un piano rateale.
	 *
	 */
	public default int contaRate(List<Instalment> instalments) {
		int numeroRate = 0;

		for (Instalment instalment : instalments) {
			if(instalment.getInstalmentNumber() != null) {
				numeroRate ++;
			}
		}

		return numeroRate;
	}

	/***
	 * Crea la pagina principale quando l'avviso contiene solo rate: con un numero di rate pari
	 * la pagina principale riporta i dati della prima rata e l'importo totale, con un numero
	 * dispari la pagina principale coincide con la prima rata.
	 *
	 */
	public default void creaPaginaPrincipaleConSoleRate(Logger logger, PaymentNotice paymentNotice, AvvisoPagamentoInput avvisoPagamentoInput,
			Map<String, String> labelLinguaPrincipale, Map<String, String> labelLinguaSecondaria) {
		List<Instalment> instalments = paymentNotice.getInstalments();

		if(instalments.size() % 2 == 0) {
			// numero di versamenti pari devo creare la pagina principale con i dati della prima rata
			logger.debug("Numero di versamenti con rate e' pari, riporto i dati della prima rata anche nella pagina principale.");
			creaPaginaPrincipaleConImportoTotale(avvisoPagamentoInput, labelLinguaPrincipale, labelLinguaSecondaria, instalments);
		} else {
			// versamenti dispari la prima pagina e la prima rata coincidono
			logger.debug("Numero di versamenti con rate e' dispari, la prima pagina coincide con la prima rata.");
			creaPaginaPrincipaleConPrimaRata(logger, paymentNotice, avvisoPagamentoInput, labelLinguaPrincipale, labelLinguaSecondaria);
		}
	}

	public default void creaPaginaPrincipaleConImportoTotale(AvvisoPagamentoInput avvisoPagamentoInput, Map<String, String> labelLinguaPrincipale,
			Map<String, String> labelLinguaSecondaria, List<Instalment> instalments) {
		PaginaAvvisoSingola pagina = new PaginaAvvisoSingola();
		Instalment versamento = instalments.get(0); // leggo alcuni dati dalla prima rata

		avvisoPagamentoInput.getEtichette().getItaliano().setNota1(getLabel(labelLinguaPrincipale, LabelAvvisiCostanti.LABEL_PRIMA_RATA));

		if(labelLinguaSecondaria != null ) {
			avvisoPagamentoInput.getEtichette().getTraduzione().setNota1(getLabel(labelLinguaSecondaria, LabelAvvisiCostanti.LABEL_PRIMA_RATA));
		}

		RataAvviso rata = instalmentToRata(versamento);
		rata.setImporto(calcolaImportoTotale(instalments));

		pagina.setRata(rata);

		avvisoPagamentoInput.getPagine().getSingolaOrDoppia().add(pagina);
	}

	/***
	 * Calcola l'importo totale del piano rateale.
	 *
	 */
	public default double calcolaImportoTotale(List<Instalment> instalments) {
		BigDecimal importoTotale = BigDecimal.ZERO;

		for (Instalment instalment : instalments) {
			importoTotale = importoTotale.add(BigDecimal.valueOf(instalment.getAmount()));
		}

		return importoTotale.doubleValue();
	}

	public default void creaPaginaPrincipaleConPrimaRata(Logger logger, PaymentNotice paymentNotice, AvvisoPagamentoInput avvisoPagamentoInput,
			Map<String, String> labelLinguaPrincipale, Map<String, String> labelLinguaSecondaria) {
		Instalment instalment = paymentNotice.getInstalments().remove(0);
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

	public default void impostaNotaPrimaRata(AvvisoPagamentoInput avvisoPagamentoInput, Map<String, String> labelLinguaPrincipale,
			Map<String, String> labelLinguaSecondaria, int numeroRate) {
		avvisoPagamentoInput.getEtichette().getItaliano().setNota1(getLabel(labelLinguaPrincipale, LabelAvvisiCostanti.LABEL_NOTA_PRIMA_RATA, numeroRate));

		if(labelLinguaSecondaria != null ) {
			avvisoPagamentoInput.getEtichette().getTraduzione().setNota1(getLabel(labelLinguaSecondaria, LabelAvvisiCostanti.LABEL_NOTA_PRIMA_RATA, numeroRate));
		}
	}

	/***
	 * Crea le pagine delle rate: due rate per pagina e l'eventuale rata residua in una pagina.
	 *
	 */
	public default void creaPagineRate(Logger logger, PaymentNotice paymentNotice, AvvisoPagamentoInput avvisoPagamentoInput,
			Map<String, String> labelLinguaPrincipale, Map<String, String> labelLinguaSecondaria) {
		List<Instalment> instalments = paymentNotice.getInstalments();

		logger.debug("Inserisco i versamenti due per pagina");
		// 2 rate per pagina
		while(instalments.size() > 1) {
			creaPaginaConDueRate(logger, paymentNotice, avvisoPagamentoInput, labelLinguaPrincipale, labelLinguaSecondaria);
		}

		logger.debug("Inserisco i versamenti residui uno per pagina");
		// rata rimasta
		if(instalments.size() == 1) {
			creaPaginaConUnaRata(logger, paymentNotice, avvisoPagamentoInput, labelLinguaPrincipale, labelLinguaSecondaria);
		}
	}

	public default void creaPaginaConDueRate(Logger logger, PaymentNotice paymentNotice, AvvisoPagamentoInput avvisoPagamentoInput,
			Map<String, String> labelLinguaPrincipale, Map<String, String> labelLinguaSecondaria) {
		List<Instalment> instalments = paymentNotice.getInstalments();

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

	public default void creaPaginaConUnaRata(Logger logger, PaymentNotice paymentNotice, AvvisoPagamentoInput avvisoPagamentoInput,
			Map<String, String> labelLinguaPrincipale, Map<String, String> labelLinguaSecondaria) {
		Instalment v1 = paymentNotice.getInstalments().remove(0);
		PaginaAvvisoDoppia pagina = new PaginaAvvisoDoppia();
		RataAvviso rataSx = instalmentToRataWithLabels(logger, v1, paymentNotice.getPostal(), labelLinguaPrincipale, labelLinguaSecondaria, avvisoPagamentoInput, paymentNotice.getCreditor());

		if(v1.getInstalmentNumber() != null) {
			// Titolo della pagina con 1 Rata
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

	/***
	 * Imposta la nota sull'importo: per la rata unica si utilizza la nota 1, per le rate la nota 2.
	 *
	 */
	public default void impostaNotaImporto(AvvisoPagamentoInput avvisoPagamentoInput, Map<String, String> labelLinguaSecondaria,
			String labelNotaImporto, String labelNotaImportoTra, boolean addNota1) {
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

	@Mapping(target = "importo", source="amount")
	@Mapping(target = "data", source="dueDate", qualifiedByName = "mapData")
	@Mapping(target = "codiceAvviso", source="noticeNumber", qualifiedByName = "mapNumeroAvviso")
	@Mapping(target = "qrCode", source="qrcode")
	public RataAvviso thresholdPaymentToRataV2(ThresholdPayment thresholdPayment);

	public default RataAvviso thresholdPaymentToRataWithLabels(Logger logger, ThresholdPayment thresholdPayment, Boolean postale,
			Map<String, String> labelLinguaPrincipale, Map<String, String> labelLinguaSecondaria,
			AvvisoPagamentoInput avvisoPagamentoInput, Creditor creditor) {
		RataAvviso rataAvviso = thresholdPaymentToRataV2(thresholdPayment);

		impostaLabelsSogliaNellaRataAvviso(rataAvviso, thresholdPayment, labelLinguaPrincipale, labelLinguaSecondaria);

		// NON impostare il campo data: la label contiene gia' l'informazione temporale

		impostaLabelsPostaliNellaRataAvviso(rataAvviso, postale, thresholdPayment.getNoticeNumber(), thresholdPayment.getIban(),
				thresholdPayment.getAmount(), avvisoPagamentoInput, creditor);

		return rataAvviso;
	}

	/***
	 * Imposta nella rata le label della soglia: entro/oltre il numero di giorni indicato.
	 *
	 */
	public default void impostaLabelsSogliaNellaRataAvviso(RataAvviso rataAvviso, ThresholdPayment thresholdPayment,
			Map<String, String> labelLinguaPrincipale, Map<String, String> labelLinguaSecondaria) {
		int giorni = thresholdPayment.getThresholdDays();

		String labelScadenza = LabelAvvisiCostanti.LABEL_OLTRE;
		String labelScadenzaUnica = LabelAvvisiCostanti.LABEL_SOLUZIONE_UNICA_OLTRE_GIORNI;

		if(thresholdPayment.getThresholdType() == ThresholdType.ENTRO) {
			labelScadenza = LabelAvvisiCostanti.LABEL_ENTRO;
			labelScadenzaUnica = LabelAvvisiCostanti.LABEL_SOLUZIONE_UNICA_ENTRO_GIORNI;
		}

		rataAvviso.setScadenza(getLabel(labelLinguaPrincipale, labelScadenza, giorni));
		rataAvviso.setScadenzaUnica(getLabel(labelLinguaPrincipale, labelScadenzaUnica, giorni));

		if(labelLinguaSecondaria != null) {
			rataAvviso.setScadenzaTra(getLabel(labelLinguaSecondaria, labelScadenza, giorni));
			rataAvviso.setScadenzaUnicaTra(getLabel(labelLinguaSecondaria, labelScadenzaUnica, giorni));
		}
	}

	public default void creaRateRidottePerAvvisoBilingue(Logger logger, PaymentNotice paymentNotice, AvvisoPagamentoInput avvisoPagamentoInput,
			Map<String, String> labelLinguaPrincipale, Map<String, String> labelLinguaSecondaria) {

		// rata unica
		RataAvviso rataUnica = creaPaginaRataUnica(logger, paymentNotice, avvisoPagamentoInput, labelLinguaPrincipale, labelLinguaSecondaria);

		// soglie ridotte: 2 per pagina (PaginaAvvisoDoppia)
		List<ThresholdPayment> reducedPayments = new ArrayList<>(paymentNotice.getReducedPayments());

		logger.debug("Inserisco le soglie ridotte due per pagina");
		while(reducedPayments.size() > 1) {
			ThresholdPayment v1 = reducedPayments.remove(0);
			ThresholdPayment v2 = reducedPayments.remove(0);
			PaginaAvvisoDoppia pagina = new PaginaAvvisoDoppia();
			RataAvviso rataSx = thresholdPaymentToRataWithLabels(logger, v1, paymentNotice.getPostal(), labelLinguaPrincipale, labelLinguaSecondaria, avvisoPagamentoInput, paymentNotice.getCreditor());
			RataAvviso rataDx = thresholdPaymentToRataWithLabels(logger, v2, paymentNotice.getPostal(), labelLinguaPrincipale, labelLinguaSecondaria, avvisoPagamentoInput, paymentNotice.getCreditor());

			pagina.getRata().add(rataSx);
			pagina.getRata().add(rataDx);
			avvisoPagamentoInput.getPagine().getSingolaOrDoppia().add(pagina);
		}

		// soglia rimasta
		if(reducedPayments.size() == 1) {
			ThresholdPayment v1 = reducedPayments.remove(0);
			PaginaAvvisoDoppia pagina = new PaginaAvvisoDoppia();
			RataAvviso rataSx = thresholdPaymentToRataWithLabels(logger, v1, paymentNotice.getPostal(), labelLinguaPrincipale, labelLinguaSecondaria, avvisoPagamentoInput, paymentNotice.getCreditor());
			pagina.getRata().add(rataSx);
			avvisoPagamentoInput.getPagine().getSingolaOrDoppia().add(pagina);
		}

		// nota importo
		String labelNotaImporto = labelLinguaPrincipale.get(LabelAvvisiCostanti.LABEL_NOTA_IMPORTO);
		String labelNotaImportoTra = null;
		if(labelLinguaSecondaria != null) {
			labelNotaImportoTra = labelLinguaSecondaria.get(LabelAvvisiCostanti.LABEL_NOTA_IMPORTO);
		}

		impostaNotaImporto(avvisoPagamentoInput, labelLinguaSecondaria, labelNotaImporto, labelNotaImportoTra, rataUnica != null);
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

		impostaLabelsNellaRataAvviso(rataAvviso, labelLinguaPrincipale, labelLinguaSecondaria);

		impostaLabelsPostaliNellaRataAvviso(rataAvviso, postale, instalment.getNoticeNumber(), instalment.getIban(), instalment.getAmount(), avvisoPagamentoInput, creditor);

		return rataAvviso;
	}

	@Mapping(target = "importo", source="amount")
	@Mapping(target = "data", source="dueDate", qualifiedByName = "mapData")
	@Mapping(target = "codiceAvviso", source="noticeNumber", qualifiedByName = "mapNumeroAvviso")
	@Mapping(target = "qrCode", source="qrcode")
	public RataAvviso amountToRataV2(Amount amount);

	public default RataAvviso amountToRataWithLabels(Logger logger, Amount amount, Boolean postale,  Map<String, String> labelLinguaPrincipale, Map<String, String> labelLinguaSecondaria, AvvisoPagamentoInput avvisoPagamentoInput, Creditor creditor) {
		RataAvviso rataAvviso = amountToRataV2(amount);

		impostaLabelsNellaRataAvviso(rataAvviso, labelLinguaPrincipale, labelLinguaSecondaria);

		impostaLabelsPostaliNellaRataAvviso(rataAvviso, postale, amount.getNoticeNumber(), amount.getIban(), amount.getAmount(), avvisoPagamentoInput, creditor);

		return rataAvviso;

	}

	public default void impostaLabelsNellaRataAvviso(RataAvviso rata, Map<String, String> labelLinguaPrincipale, Map<String, String> labelLinguaSecondaria) {
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

	public default void impostaLabelsPostaliNellaRataAvviso(RataAvviso rataAvviso, Boolean postale,
			String noticeNumber, Iban iban, Double importo, AvvisoPagamentoInput avvisoPagamentoInput, Creditor creditor) {
		if(Boolean.TRUE.equals(postale)) {

			String numeroCC = AvvisoPagamentoUtils.getNumeroCCDaIban(iban.getIbanCode());
			rataAvviso.setDataMatrix(AvvisoPagamentoUtils.creaDataMatrix(noticeNumber, numeroCC, importo,
					avvisoPagamentoInput.getCfEnte(),
					avvisoPagamentoInput.getCfDestinatario(),
					avvisoPagamentoInput.getNomeCognomeDestinatario(),
					avvisoPagamentoInput.getEtichette().getItaliano().getOggettoDelPagamento()));
			rataAvviso.setNumeroCcPostale(numeroCC);
			// codice avviso gia' diviso in gruppi di 4
			rataAvviso.setCodiceAvvisoPostale(rataAvviso.getCodiceAvviso());
			rataAvviso.setAutorizzazione(getAutorizzazionePostale(creditor, iban));
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
