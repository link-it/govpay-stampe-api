package it.govpay.stampe.validator;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Component;

import it.govpay.stampe.beans.Amount;
import it.govpay.stampe.beans.CdsViolation;
import it.govpay.stampe.beans.Instalment;
import it.govpay.stampe.beans.PaymentNotice;
import it.govpay.stampe.exception.UnprocessableEntityException;

@Component
public class SemanticValidator {

	public void validazioneSemanticaViolazioneCds(CdsViolation cdsViolation) {
		this.validazioneSemanticaAmount(cdsViolation.getDiscountedAmount(), cdsViolation.getPostal());
		this.validazioneSemanticaAmount(cdsViolation.getReducedAmount(), cdsViolation.getPostal());
	}
	
	public void validazioneSemanticaPaymentNotice(PaymentNotice paymentNotice) {
		// rata unica o rate obbligatorie
		List<Instalment> instalments = paymentNotice.getInstalments();
		Amount full = paymentNotice.getFull();
		
		if(full == null && (instalments == null || instalments.isEmpty())) {
			throw new UnprocessableEntityException("Indicare la rata unica o almeno una rata");
		}
		
		// validazione semantica coppia iban/postale
		if(full != null) {
			this.validazioneSemanticaAmount(full, paymentNotice.getPostal());
		}
		
		if(instalments != null) {
			for (Instalment instalment : instalments) {
				this.validazioneSemanticaInstalment(instalment, paymentNotice.getPostal());
			}
		}
	}
	
	
	private void validazioneSemanticaInstalment(Instalment instalment, @NotNull Boolean postal) {
		if(postal.booleanValue() && instalment.getIbanCode() == null) 
			throw new UnprocessableEntityException("Iban obbligatorio in caso di avviso postale");		
	}

	private void validazioneSemanticaAmount(Amount amount, Boolean postal) {
		if(postal.booleanValue() && amount.getIbanCode() == null) 
			throw new UnprocessableEntityException("Iban obbligatorio in caso di avviso postale");
	}
}
