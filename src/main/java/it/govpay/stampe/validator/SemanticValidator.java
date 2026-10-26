package it.govpay.stampe.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import it.govpay.stampe.beans.Amount;
import it.govpay.stampe.beans.CdsViolation;
import it.govpay.stampe.beans.Creditor;
import it.govpay.stampe.beans.Iban;
import it.govpay.stampe.beans.Instalment;
import it.govpay.stampe.beans.PaymentNotice;
import it.govpay.stampe.beans.PaymentNoticeOption;
import it.govpay.stampe.beans.ThresholdPayment;
import it.govpay.stampe.exception.UnprocessableEntityException;

@Component
public class SemanticValidator {

	public void validazioneSemanticaViolazioneCds(CdsViolation cdsViolation) {
		this.validazioneSemanticaAmount(cdsViolation.getDiscountedAmount(), cdsViolation.getPostal());
		this.validazioneSemanticaAmount(cdsViolation.getReducedAmount(), cdsViolation.getPostal());
	}

	public void validazioneSemanticaPaymentNotice(PaymentNotice paymentNotice) {
		List<PaymentNoticeOption> options = paymentNotice.getOptions();
		if(options != null && !options.isEmpty()) {
			this.validazioneSemanticaOptions(paymentNotice, options);
			return;
		}

		// rata unica o rate o soglie obbligatorie
		List<Instalment> instalments = paymentNotice.getInstalments();
		Amount full = paymentNotice.getFull();
		List<ThresholdPayment> reducedPayments = paymentNotice.getReducedPayments();

		boolean hasInstalments = instalments != null && !instalments.isEmpty();
		boolean hasReducedPayments = reducedPayments != null && !reducedPayments.isEmpty();

		if(full == null && !hasInstalments && !hasReducedPayments) {
			throw new UnprocessableEntityException("Indicare la rata unica, almeno una rata o almeno un pagamento in forma ridotta");
		}

		// mutua esclusivita' tra rate e soglie
		if(hasInstalments && hasReducedPayments) {
			throw new UnprocessableEntityException("Non e' possibile indicare sia rate che pagamenti in forma ridotta");
		}

		// validazione semantica coppia iban/postale
		if(full != null) {
			this.validazioneSemanticaAmount(full, paymentNotice.getPostal());
		}

		if(hasInstalments) {
			for (Instalment instalment : instalments) {
				this.validazioneSemanticaInstalment(instalment, paymentNotice.getPostal());
			}
		}

		if(hasReducedPayments) {
			for (ThresholdPayment thresholdPayment : reducedPayments) {
				this.validazioneSemanticaThresholdPayment(thresholdPayment, paymentNotice.getPostal());
			}
		}
	}


	/**
	 * {@code options} e i campi a livello di documento ({@code full}/{@code instalments}/
	 * {@code reducedPayments}) sono mutuamente esclusivi, e {@code options} non e' ancora
	 * supportato insieme all'avviso bilingue ({@code secondLanguage}) - il mapper bilingue non
	 * conosce ancora {@code options}, produrrebbe un avviso silenziosamente incompleto invece
	 * di un errore esplicito.
	 */
	private void validazioneSemanticaOptions(PaymentNotice paymentNotice, List<PaymentNoticeOption> options) {
		if(paymentNotice.getSecondLanguage() != null) {
			throw new UnprocessableEntityException("'options' non e' ancora supportato in combinazione con l'avviso bilingue (secondLanguage)");
		}

		boolean hasFull = paymentNotice.getFull() != null;
		boolean hasInstalments = paymentNotice.getInstalments() != null && !paymentNotice.getInstalments().isEmpty();
		boolean hasReducedPayments = paymentNotice.getReducedPayments() != null && !paymentNotice.getReducedPayments().isEmpty();
		if(hasFull || hasInstalments || hasReducedPayments) {
			throw new UnprocessableEntityException("Non e' possibile indicare sia 'options' che 'full'/'instalments'/'reducedPayments' a livello di documento");
		}

		for (PaymentNoticeOption option : options) {
			if(option == null) {
				throw new UnprocessableEntityException("Un elemento di 'options' non puo' essere null");
			}
			this.validazioneSemanticaOption(option, paymentNotice.getPostal());
		}

		if(Boolean.TRUE.equals(paymentNotice.getPostal())) {
			this.validazioneSemanticaIntestatarioPostaleUnico(options, paymentNotice.getCreditor());
		}
	}

	private void validazioneSemanticaOption(PaymentNoticeOption option, Boolean postal) {
		List<Instalment> instalments = option.getInstalments();
		Amount full = option.getFull();
		List<ThresholdPayment> reducedPayments = option.getReducedPayments();

		boolean hasInstalments = instalments != null && !instalments.isEmpty();
		boolean hasReducedPayments = reducedPayments != null && !reducedPayments.isEmpty();

		if(full == null && !hasInstalments && !hasReducedPayments) {
			throw new UnprocessableEntityException("Ogni elemento di 'options' deve indicare la rata unica, almeno una rata o almeno un pagamento in forma ridotta");
		}

		if(hasInstalments && hasReducedPayments) {
			throw new UnprocessableEntityException("Non e' possibile indicare sia rate che pagamenti in forma ridotta nello stesso elemento di 'options'");
		}

		// AvvisoPagamentoMapper#creaRatePerAvvisoSemplice, riusato per ciascuna opzione, perde
		// 'full' quando e' presente insieme a esattamente una rata in formato non postale (stampa
		// solo la rata): combinazione da rifiutare qui, non da lasciare passare e perdere in
		// silenzio. Non postale, perche' in formato postale la rata unica e la rata vengono
		// comunque stampate su due pagine singole separate (creaRatePerAvvisoPostale), nessuna
		// perdita.
		if(full != null && hasInstalments && instalments.size() == 1 && !Boolean.TRUE.equals(postal)) {
			throw new UnprocessableEntityException("In un elemento di 'options' non postale, 'full' insieme a esattamente una rata non e' stampabile: la stampa mostrerebbe solo la rata, perdendo 'full' - indicare solo uno dei due, oppure piu' di una rata");
		}

		if(full != null) {
			this.validazioneSemanticaAmount(full, postal);
		}

		if(hasInstalments) {
			for (Instalment instalment : instalments) {
				this.validazioneSemanticaInstalment(instalment, postal);
			}
		}

		if(hasReducedPayments) {
			for (ThresholdPayment thresholdPayment : reducedPayments) {
				this.validazioneSemanticaThresholdPayment(thresholdPayment, postal);
			}
		}
	}

	/**
	 * {@code AvvisoPagamentoInput.intestatarioContoCorrentePostale} e' un campo UNICO per tutto
	 * il documento ({@code BaseAvvisoMapper#impostaDatiPostaliNellaRata} lo sovrascrive a ogni
	 * importo processato, mai un dato per pagina/rata): con piu' opzioni che risolvono IBAN con
	 * intestatari diversi, le pagine delle opzioni precedenti mostrerebbero in stampa
	 * l'intestatario dell'ultima opzione processata, non il proprio. Soluzione temporanea (non
	 * il dato per pagina/rata, che richiederebbe toccare il modello/template Jasper): rifiutare
	 * esplicitamente intestatari diversi tra le opzioni, invece di stampare in silenzio quello
	 * sbagliato.
	 */
	private void validazioneSemanticaIntestatarioPostaleUnico(List<PaymentNoticeOption> options, Creditor creditor) {
		String intestatarioEnte = creditor != null ? creditor.getBusinessName() : null;
		List<String> intestatari = new ArrayList<>();

		for (PaymentNoticeOption option : options) {
			if(option.getFull() != null) {
				intestatari.add(this.risolviIntestatarioPostale(option.getFull().getIban(), intestatarioEnte));
			}
			if(option.getInstalments() != null) {
				for (Instalment instalment : option.getInstalments()) {
					intestatari.add(this.risolviIntestatarioPostale(instalment.getIban(), intestatarioEnte));
				}
			}
			if(option.getReducedPayments() != null) {
				for (ThresholdPayment thresholdPayment : option.getReducedPayments()) {
					intestatari.add(this.risolviIntestatarioPostale(thresholdPayment.getIban(), intestatarioEnte));
				}
			}
		}

		String primoIntestatario = intestatari.isEmpty() ? null : intestatari.get(0);
		for (String intestatario : intestatari) {
			if(!Objects.equals(primoIntestatario, intestatario)) {
				throw new UnprocessableEntityException("Con 'options' e avviso postale, tutti gli IBAN delle opzioni "
						+ "devono risolvere allo stesso intestatario di conto corrente: la stampa usa un solo "
						+ "campo per l'intero documento, non uno per pagina");
			}
		}
	}

	private String risolviIntestatarioPostale(Iban iban, String intestatarioEnte) {
		if(iban == null || StringUtils.isBlank(iban.getOwnerBusinessName())) {
			return intestatarioEnte;
		}
		return iban.getOwnerBusinessName();
	}

	private void validazioneSemanticaInstalment(Instalment instalment, @NotNull Boolean postal) {
		if(postal.booleanValue() && (instalment.getIban() == null))
			throw new UnprocessableEntityException("Iban obbligatorio in caso di avviso postale");
	}

	private void validazioneSemanticaAmount(Amount amount, Boolean postal) {
		if(postal.booleanValue() && (amount.getIban() == null))
			throw new UnprocessableEntityException("Iban obbligatorio in caso di avviso postale");
	}

	private void validazioneSemanticaThresholdPayment(ThresholdPayment thresholdPayment, @NotNull Boolean postal) {
		if(postal.booleanValue() && (thresholdPayment.getIban() == null))
			throw new UnprocessableEntityException("Iban obbligatorio in caso di avviso postale");
	}
}
