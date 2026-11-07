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
 * Rate nell'avviso bilingue: la rata della pagina principale ha la scadenza (issue #55; senza, l'intestazione dei
 * dati per il pagamento stampava "null") e, con un numero pari di rate, l'importo della rata del suo QR code, con il
 * totale a parte (issue #59); la traduzione del numero di rata usa il numero e non l'etichetta italiana ("Obrok 2",
 * non "Obrok Rata 2").
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
	void importiDellaPaginaPrincipaleConUnNumeroPariDiRate() {
		// la banda di pagamento ha l'importo della rata del QR code, il totale va in "Quanto e quando pagare" (#59)
		PaymentNotice avviso = bilingueSloveno(2);
		double primaRata = avviso.getInstalments().get(0).getAmount();
		double totale = avviso.getInstalments().stream().mapToDouble(r -> r.getAmount()).sum();
		RataAvviso principale = rate(avviso).get(0);
		assertEquals(primaRata, principale.getImporto());
		assertEquals(totale, principale.getImportoTotale(), 0.001);
	}

	@Test
	void bollettinoDellaPaginaPrincipaleConUnNumeroPariDiRate() {
		// con il bollettino la rata della pagina principale ha il conto e il DataMatrix della rata 1, come la sua pagina
		PaymentNotice avviso = bilingueSloveno(2);
		avviso.setPostal(true);
		List<RataAvviso> rate = rate(avviso);
		RataAvviso principale = rate.get(0);
		RataAvviso primaRata = rate.get(1);
		assertEquals(primaRata.getNumeroCcPostale(), principale.getNumeroCcPostale());
		assertEquals(primaRata.getDataMatrix(), principale.getDataMatrix());
		assertEquals(primaRata.getImporto(), principale.getImporto());
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
