package it.govpay.stampe.test.utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import it.govpay.stampe.beans.Amount;
import it.govpay.stampe.beans.CdsViolation;
import it.govpay.stampe.beans.Creditor;
import it.govpay.stampe.beans.Debtor;
import it.govpay.stampe.beans.Iban;
import it.govpay.stampe.beans.Instalment;
import it.govpay.stampe.beans.Languages;
import it.govpay.stampe.beans.NoticeMetadataSecondLanguage;
import it.govpay.stampe.beans.PaymentNotice;
import it.govpay.stampe.test.costanti.Costanti;

@Component
public class AvvisiPagamentoFactory {
	
	@Value("${stampe.logoEnte}")
	String logoEnte;

	public static String extractFilename(String headerValue) {
        String[] parts = headerValue.split("; ");
        String filenamePart = null;
        for (String part : parts) {
            if (part.startsWith("filename=")) {
                filenamePart = part;
                break;
            }
        }
        if (filenamePart != null) {
            String[] filenameParts = filenamePart.split("=");
            if (filenameParts.length == 2) {
                return filenameParts[1].replaceAll("\"", "");
            }
        }
        return null;
    }
	
	public CdsViolation creaCdsViolation() {
		CdsViolation cdsViolation = new CdsViolation();
		cdsViolation.setLanguage(Languages.IT);
		cdsViolation.setTitle("Violazione del codice della strada"); 
		Creditor creditor = new Creditor();
		creditor.setCbillCode("ABC12");
		creditor.setFiscalCode(Costanti.ID_DOMINIO_1);
		creditor.setBusinessName("Ente Creditore Test");
		creditor.setInfoLine1("Tel: 00 1234 5678 - Fax: 00 1234 5678");
		creditor.setInfoLine2("pec: protocollo.generale@pec.entecreditore.it");
		cdsViolation.setCreditor(creditor);
		
		Debtor debtor = new Debtor();
		debtor.setFiscalCode("RSSMRA50A01A110X");
		debtor.setFullName("Mario Rossi");
		debtor.setAddressLine1("Viale Monterosa 11Bis");
		debtor.setAddressLine2("340 Roma (RM)");
		cdsViolation.setDebtor(debtor );
		
		cdsViolation.setPostal(false);
		
		String numeroAvviso = this.generaNumeroAvviso();
		Amount ridotto = new Amount();
		ridotto.setAmount(Double.valueOf(50.00));
		ridotto.setDueDate(LocalDate.now());
		ridotto.setNoticeNumber(numeroAvviso);
		ridotto.setQrcode("PAGOPA|002|"+numeroAvviso+"|"+Costanti.ID_DOMINIO_1+"|5000");
		ridotto.setIban(this.creaIban());
		cdsViolation.setReducedAmount(ridotto );
		
		numeroAvviso = this.generaNumeroAvviso();
		Amount scontato = new Amount();
		scontato.setAmount(Double.valueOf(150.00));
		scontato.setDueDate(LocalDate.now());
		scontato.setNoticeNumber(numeroAvviso);
		scontato.setQrcode("PAGOPA|002|"+numeroAvviso+"|"+Costanti.ID_DOMINIO_1+"|15000");
		scontato.setIban(this.creaIban());
		cdsViolation.setDiscountedAmount(scontato );
		
		cdsViolation.setFirstLogo(new ByteArrayResource(this.logoEnte.getBytes()));
		
		return cdsViolation;
	}
	
	public Iban creaIban() {
		Iban iban = new Iban();
		iban.setIbanCode("IT60X0542811101000000123456");
		iban.setOwnerBusinessName("Ente Creditore Test");
		iban.setPostalAuthMessage("Autorizzazione al pagamento con IBAN");
		return iban;
	}
		
	public PaymentNotice creaPaymentNoticeFull() {
		return this.creaPaymentNoticeConRate(0, true);
	}
	
	public PaymentNotice creaPaymentNoticeDueRate() {
		return this.creaPaymentNoticeConRate(2, false);
	}
	
	public PaymentNotice creaPaymentNoticeTreRate() {
		return this.creaPaymentNoticeConRate(3, false);
	}
	
	public PaymentNotice creaPaymentNoticeConRate(int numeroRate, boolean rataUnica) {
		PaymentNotice paymentNotice = new PaymentNotice();
		paymentNotice.setLanguage(Languages.IT);
		StringBuilder sb = new StringBuilder();
		
		sb.append("Avviso di pagamento con "+numeroRate+" rate");
		
		if(rataUnica)
			sb.append(" e rata unica");
		
		paymentNotice.setTitle(sb.toString());
		
		Creditor creditor = new Creditor();
		creditor.setCbillCode("ABC12");
		creditor.setFiscalCode(Costanti.ID_DOMINIO_1);
		creditor.setBusinessName("Ente Creditore Test");
		creditor.setInfoLine1("Tel: 00 1234 5678 - Fax: 00 1234 5678");
		creditor.setInfoLine2("pec: protocollo.generale@pec.entecreditore.it");
		paymentNotice.setCreditor(creditor);
		
		Debtor debtor = new Debtor();
		debtor.setFiscalCode("RSSMRA50A01A110X");
		debtor.setFullName("Mario Rossi");
		debtor.setAddressLine1("Viale Monterosa 11Bis");
		debtor.setAddressLine2("340 Roma (RM)");
		paymentNotice.setDebtor(debtor );
		
		paymentNotice.setPostal(false);
		
		if(rataUnica) {
			String numeroAvviso = this.generaNumeroAvviso();
			Amount full = new Amount();
			full.setAmount(Double.valueOf(50.00));
			full.setDueDate(LocalDate.now());
			full.setNoticeNumber(numeroAvviso);
			full.setQrcode("PAGOPA|002|"+numeroAvviso+"|"+Costanti.ID_DOMINIO_1+"|5000");
			full.setIban(this.creaIban());
			paymentNotice.setFull(full );
		}
		
		List<Instalment> rate = new ArrayList<>();
		
		if(numeroRate > 0) {
			for (int i = 0; i < numeroRate; i++) {
				String numeroAvviso = this.generaNumeroAvviso();
				
				Instalment rata1 = new Instalment();
				rata1.setAmount(Double.valueOf(50.00));
				rata1.setDueDate(LocalDate.now());
				rata1.setNoticeNumber(numeroAvviso);
				rata1.setQrcode("PAGOPA|002|"+numeroAvviso+"|"+Costanti.ID_DOMINIO_1+"|5000");
				rata1.setInstalmentNumber((i +1));
				rata1.setIban(this.creaIban());
				rate.add(rata1 );	
			}
		}
		
		paymentNotice.setInstalments(rate);
		
		paymentNotice.setFirstLogo(new ByteArrayResource(this.logoEnte.getBytes()));
		
		NoticeMetadataSecondLanguage secondLanguage = new NoticeMetadataSecondLanguage();
		secondLanguage.setBilinguism(true);
		secondLanguage.setLanguage(Languages.SL);
		secondLanguage.setTitle(sb.toString() + "ma nella seconda lingua");
		paymentNotice.setSecondLanguage(secondLanguage );
		
		return paymentNotice;
	}
	
	public String generaNumeroAvviso() {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();

        // Iniziamo con un numero casuale tra 0 e 3
        int firstDigit = random.nextInt(4);
        stringBuilder.append(firstDigit);

        // Aggiungiamo 17 cifre casuali
        for (int i = 0; i < 17; i++) {
            int digit = random.nextInt(10); // numeri casuali da 0 a 9
            stringBuilder.append(digit);
        }

        return stringBuilder.toString();
    }
}
