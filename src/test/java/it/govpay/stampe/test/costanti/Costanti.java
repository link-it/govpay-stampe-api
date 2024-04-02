package it.govpay.stampe.test.costanti;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.ByteArrayResource;

import it.govpay.stampe.beans.Amount;
import it.govpay.stampe.beans.CdsViolation;
import it.govpay.stampe.beans.Creditor;
import it.govpay.stampe.beans.Debtor;
import it.govpay.stampe.beans.Instalment;
import it.govpay.stampe.beans.Languages;
import it.govpay.stampe.beans.NoticeMetadataSecondLanguage;
import it.govpay.stampe.beans.PaymentNotice;

public class Costanti {

	public static final String API_BASE_PATH = "";
	public static final String CDS_VIOLATION_PATH = API_BASE_PATH + "/cds_violation";
	public static final String STANDARD_PATH = API_BASE_PATH + "/standard";

	public static final String ID_DOMINIO_1 = "12345678901";
	
	public static final String STRING_256= "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnop";

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
		
		cdsViolation.setFirstLogo(new ByteArrayResource(STRING_256.getBytes()));
		
		return cdsViolation;
	}
	
	public static PaymentNotice creaPaymentNoticeFull() {
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
		Amount rataUnica = new Amount();
		rataUnica.setAmount(Double.valueOf(50.00));
		rataUnica.setDueDate(LocalDate.now());
		rataUnica.setNoticeNumber("001340000013900808");
		rataUnica.setQrcode("PAGOPA|002|001340000013900808|12345678901|5000");
		paymentNotice.setFull(rataUnica );
		
		paymentNotice.setFirstLogo(new ByteArrayResource(STRING_256.getBytes()));
		
		NoticeMetadataSecondLanguage secondLanguage = new NoticeMetadataSecondLanguage();
		secondLanguage.setBilinguism(true);
		secondLanguage.setLanguage(Languages.EN);
		secondLanguage.setTitle("Titolo");
		paymentNotice.setSecondLanguage(secondLanguage );
		
		return paymentNotice;
	}
	
	public static PaymentNotice creaPaymentNoticeDueRate() {
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
		
		List<Instalment> rate = new ArrayList<>();
		
		Instalment rata1 = new Instalment();
		rata1.setAmount(Double.valueOf(50.00));
		rata1.setDueDate(LocalDate.now());
		rata1.setNoticeNumber("001340000013900808");
		rata1.setQrcode("PAGOPA|002|001340000013900808|12345678901|5000");
		rate.add(rata1 );
		
		Instalment rata2 = new Instalment();
		rata2.setAmount(Double.valueOf(150.00));
		rata2.setDueDate(LocalDate.now());
		rata2.setNoticeNumber("001340000013900909");
		rata2.setQrcode("PAGOPA|002|001340000013900909|12345678901|15000");
		rate.add(rata2 );
		
		paymentNotice.setInstalments(rate);
		
		paymentNotice.setFirstLogo(new ByteArrayResource(STRING_256.getBytes()));
		
		return paymentNotice;
	}
}
