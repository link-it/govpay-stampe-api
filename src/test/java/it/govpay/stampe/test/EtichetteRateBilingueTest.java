package it.govpay.stampe.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import it.govpay.stampe.Application;
import it.govpay.stampe.beans.Languages;
import it.govpay.stampe.beans.PaymentNotice;
import it.govpay.stampe.config.LabelAvvisiConfiguration.LabelAvvisiProperties;
import it.govpay.stampe.mapper.AvvisoPagamentoBilingueMapper;
import it.govpay.stampe.model.v2.AvvisoPagamentoInput;
import it.govpay.stampe.model.v2.PaginaAvvisoDoppia;
import it.govpay.stampe.model.v2.PaginaAvvisoSingola;
import it.govpay.stampe.model.v2.RataAvviso;
import it.govpay.stampe.test.utils.AvvisiPagamentoFactory;

/**
 * Etichette delle rate nell'avviso bilingue (issue #55): la rata della pagina principale ha la scadenza
 * (senza, l'intestazione dei dati per il pagamento stampava "null") e la traduzione del numero di rata usa il
 * numero e non l'etichetta italiana ("Obrok 2", non "Obrok Rata 2").
 */
@SpringBootTest(classes = Application.class)
@DisplayName("Test Etichette delle rate nell'avviso bilingue")
@ActiveProfiles("test")
class EtichetteRateBilingueTest {

	@Autowired
	AvvisiPagamentoFactory avvisiPagamentoFactory;

	@Autowired
	AvvisoPagamentoBilingueMapper bilingueMapper;

	@Autowired
	@Qualifier("labelAvvisiProperties")
	LabelAvvisiProperties labelAvvisiProperties;

	private List<RataAvviso> rate(PaymentNotice avviso) {
		AvvisoPagamentoInput input = this.bilingueMapper.toPaymentNoticeAvvisoPagamentoInput(
				LoggerFactory.getLogger(EtichetteRateBilingueTest.class), avviso, this.labelAvvisiProperties);
		List<RataAvviso> rate = new ArrayList<>();
		for (Object pagina : input.getPagine().getSingolaOrDoppia()) {
			if (pagina instanceof PaginaAvvisoSingola p) {
				rate.add(p.getRata());
			} else if (pagina instanceof PaginaAvvisoDoppia p) {
				rate.addAll(p.getRata());
			}
		}
		return rate;
	}

	private PaymentNotice bilingueSloveno(int numeroRate) {
		PaymentNotice avviso = this.avvisiPagamentoFactory.creaPaymentNoticeConRate(numeroRate, false);
		avviso.getSecondLanguage().setLanguage(Languages.SL);
		return avviso;
	}

	@Test
	void paginaPrincipaleConUnNumeroPariDiRate() {
		List<RataAvviso> rate = rate(bilingueSloveno(2));
		assertEquals("Rata 1 entro il", rate.get(0).getScadenza());
		assertEquals("Obrok 1 do", rate.get(0).getScadenzaTra());
	}

	@Test
	void numeroDiRataTradotto() {
		List<RataAvviso> rate = rate(bilingueSloveno(2));
		RataAvviso seconda = rate.get(rate.size() - 1);
		assertEquals("Rata 2", seconda.getNumeroRata());
		assertEquals("Obrok 2", seconda.getNumeroRataTra());
		assertEquals("Rata 2 entro il", seconda.getScadenza());
		assertEquals("Obrok 2 do", seconda.getScadenzaTra());
	}
}
