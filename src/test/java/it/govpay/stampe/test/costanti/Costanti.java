package it.govpay.stampe.test.costanti;

import java.time.LocalDate;

import org.springframework.core.io.ByteArrayResource;

import it.govpay.stampe.beans.Amount;
import it.govpay.stampe.beans.CdsViolation;
import it.govpay.stampe.beans.Creditor;
import it.govpay.stampe.beans.Debtor;
import it.govpay.stampe.beans.Languages;

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
}
