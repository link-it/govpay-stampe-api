package it.govpay.stampe.validator;

import java.util.List;

import jakarta.validation.constraints.NotNull;

import org.springframework.stereotype.Component;

import it.govpay.stampe.beans.Amount;
import it.govpay.stampe.beans.CdsViolation;
import it.govpay.stampe.beans.Instalment;
import it.govpay.stampe.beans.PaymentNotice;
import it.govpay.stampe.beans.ThresholdPayment;
import it.govpay.stampe.exception.UnprocessableEntityException;

@Component
public class SemanticValidator {

	public void validazioneSemanticaViolazioneCds(CdsViolation cdsViolation) {
		this.validazioneSemanticaAmount(cdsViolation.getDiscountedAmount(), cdsViolation.getPostal());
		this.validazioneSemanticaAmount(cdsViolation.getReducedAmount(), cdsViolation.getPostal());
	}

	public void validazioneSemanticaPaymentNotice(PaymentNotice paymentNotice) {
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
