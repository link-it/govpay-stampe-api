package it.govpay.stampe.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import it.govpay.stampe.Application;
import it.govpay.stampe.beans.CdsViolation;
import it.govpay.stampe.beans.PaymentNotice;
import it.govpay.stampe.mapper.AvvisoPagamentoMapper;
import it.govpay.stampe.mapper.ViolazioneCdsMapper;
import it.govpay.stampe.test.utils.AvvisiPagamentoFactory;

/**
 * Canali digitali della sezione "Dove pagare" (issue #47): "oppure sul sito di &lt;nome dell'ente&gt;, ..." solo
 * se l'avviso si puo' pagare dal sito dell'ente ({@code creditor.pay_on_website}), indipendentemente da
 * {@code postal}; altrimenti "oppure dal tuo Home Banking, ...".
 */
@SpringBootTest(classes = Application.class)
@DisplayName("Test Pagamento dal sito dell'ente")
@ActiveProfiles("test")
class PagamentoSitoEnteTest {

	@Autowired
	AvvisiPagamentoFactory avvisiPagamentoFactory;

	@Autowired
	AvvisoPagamentoMapper avvisoPagamentoMapper;

	@Autowired
	ViolazioneCdsMapper violazioneCdsMapper;

	private String delTuoEnte(PaymentNotice avviso) {
		return this.avvisoPagamentoMapper.toPaymentNoticeAvvisoPagamentoInput(avviso).getDelTuoEnte();
	}

	private String delTuoEnte(CdsViolation violazione) {
		return this.violazioneCdsMapper.toViolazioneAvvisoPagamentoInputBase(violazione).getDelTuoEnte();
	}

	@Test
	void avvisoPagabileDalSito() {
		PaymentNotice avviso = this.avvisiPagamentoFactory.creaPaymentNoticeFull();
		avviso.getCreditor().setBusinessName("Comune di Test");
		avviso.getCreditor().setPayOnWebsite(true);
		assertEquals("di Comune di Test", delTuoEnte(avviso));
	}

	@Test
	void avvisoNonPagabileDalSito() {
		PaymentNotice avviso = this.avvisiPagamentoFactory.creaPaymentNoticeFull();
		avviso.getCreditor().setPayOnWebsite(false);
		assertNull(delTuoEnte(avviso));
	}

	@Test
	void avvisoPostaleSenzaIndicazione() {
		// prima la frase sul sito dipendeva da postal: ora serve pay_on_website
		PaymentNotice avviso = this.avvisiPagamentoFactory.creaPaymentNoticeFull();
		avviso.setPostal(true);
		avviso.getCreditor().setPayOnWebsite(null);
		assertNull(delTuoEnte(avviso));
	}

	@Test
	void avvisoPagabileDalSitoSenzaNomeEnte() {
		PaymentNotice avviso = this.avvisiPagamentoFactory.creaPaymentNoticeFull();
		avviso.getCreditor().setBusinessName(" ");
		avviso.getCreditor().setPayOnWebsite(true);
		assertNull(delTuoEnte(avviso));
	}

	@Test
	void senzaEnteCreditore() {
		assertNull(this.avvisoPagamentoMapper.mapDelTuoEnte(null));
	}

	@Test
	void violazionePagabileDalSito() {
		CdsViolation violazione = this.avvisiPagamentoFactory.creaCdsViolation();
		violazione.getCreditor().setBusinessName("Comune di Test");
		violazione.getCreditor().setPayOnWebsite(true);
		assertEquals("di Comune di Test", delTuoEnte(violazione));
	}

	@Test
	void violazioneNonPagabileDalSito() {
		CdsViolation violazione = this.avvisiPagamentoFactory.creaCdsViolation();
		violazione.setPostal(true);
		assertNull(delTuoEnte(violazione));
	}
}
