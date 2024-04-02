package it.govpay.stampe.test.utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.core.io.ByteArrayResource;

import it.govpay.stampe.beans.Amount;
import it.govpay.stampe.beans.CdsViolation;
import it.govpay.stampe.beans.Creditor;
import it.govpay.stampe.beans.Debtor;
import it.govpay.stampe.beans.Instalment;
import it.govpay.stampe.beans.Languages;
import it.govpay.stampe.beans.NoticeMetadataSecondLanguage;
import it.govpay.stampe.beans.PaymentNotice;
import it.govpay.stampe.test.costanti.Costanti;

public class Utils {

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
	
	public static CdsViolation creaCdsViolation() {
		CdsViolation cdsViolation = new CdsViolation();
		cdsViolation.setLanguage(Languages.IT);
		Creditor creditor = new Creditor();
		creditor.setCbillCode("ABC12");
		creditor.setFiscalCode(Costanti.ID_DOMINIO_1);
		creditor.setBusinessName("Ente Creditore Test");
		cdsViolation.setCreditor(creditor);
		
		Debtor debtor = new Debtor();
		debtor.setFiscalCode("RSSMRA50A01A110X");
		debtor.setFullName("Mario Rossi");
		cdsViolation.setDebtor(debtor );
		
		cdsViolation.setPostal(false);
		Amount ridotto = new Amount();
		ridotto.setAmount(Double.valueOf(50.00));
		ridotto.setDueDate(LocalDate.now());
		ridotto.setNoticeNumber("001340000013900808");
		ridotto.setQrcode("PAGOPA|002|001340000013900808|12345678901|5000");
		cdsViolation.setReducedAmount(ridotto );
		
		Amount scontato = new Amount();
		scontato.setAmount(Double.valueOf(150.00));
		scontato.setDueDate(LocalDate.now());
		scontato.setNoticeNumber("001340000013900909");
		scontato.setQrcode("PAGOPA|002|001340000013900909|12345678901|15000");
		cdsViolation.setDiscountedAmount(scontato );
		
		cdsViolation.setFirstLogo(new ByteArrayResource(Costanti.STRING_256.getBytes()));
		
		return cdsViolation;
	}
	
	public static PaymentNotice creaPaymentNoticeFull() {
		return Utils.creaPaymentNoticeConRate(0, true);
	}
	
	public static PaymentNotice creaPaymentNoticeDueRate() {
		return Utils.creaPaymentNoticeConRate(2, false);
	}
	
	public static PaymentNotice creaPaymentNoticeTreRate() {
		return Utils.creaPaymentNoticeConRate(3, false);
	}
	
	public static PaymentNotice creaPaymentNoticeConRate(int numeroRate, boolean rataUnica) {
		PaymentNotice paymentNotice = new PaymentNotice();
		paymentNotice.setLanguage(Languages.IT);
		Creditor creditor = new Creditor();
		creditor.setCbillCode("ABC12");
		creditor.setFiscalCode(Costanti.ID_DOMINIO_1);
		creditor.setBusinessName("Ente Creditore Test");
		paymentNotice.setCreditor(creditor);
		
		Debtor debtor = new Debtor();
		debtor.setFiscalCode("RSSMRA50A01A110X");
		debtor.setFullName("Mario Rossi");
		paymentNotice.setDebtor(debtor );
		
		paymentNotice.setPostal(false);
		
		if(rataUnica) {
			String numeroAvviso = Utils.generaNumeroAvviso();
			Amount full = new Amount();
			full.setAmount(Double.valueOf(50.00));
			full.setDueDate(LocalDate.now());
			full.setNoticeNumber(numeroAvviso);
			full.setQrcode("PAGOPA|002|"+numeroAvviso+"|"+Costanti.ID_DOMINIO_1+"|5000");
			paymentNotice.setFull(full );
		}
		
		List<Instalment> rate = new ArrayList<>();
		
		if(numeroRate > 0) {
			for (int i = 0; i < numeroRate; i++) {
				String numeroAvviso = Utils.generaNumeroAvviso();
				
				Instalment rata1 = new Instalment();
				rata1.setAmount(Double.valueOf(50.00));
				rata1.setDueDate(LocalDate.now());
				rata1.setNoticeNumber(numeroAvviso);
				rata1.setQrcode("PAGOPA|002|"+numeroAvviso+"|"+Costanti.ID_DOMINIO_1+"|5000");
				rata1.setInstalmentNumber((i +1));
				rate.add(rata1 );	
			}
		}
		
		paymentNotice.setInstalments(rate);
		
		paymentNotice.setFirstLogo(new ByteArrayResource(Costanti.STRING_256.getBytes()));
		
		NoticeMetadataSecondLanguage secondLanguage = new NoticeMetadataSecondLanguage();
		secondLanguage.setBilinguism(true);
		secondLanguage.setLanguage(Languages.EN);
		secondLanguage.setTitle("Titolo");
		paymentNotice.setSecondLanguage(secondLanguage );
		
		return paymentNotice;
	}
	
	public static String generaNumeroAvviso() {
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
