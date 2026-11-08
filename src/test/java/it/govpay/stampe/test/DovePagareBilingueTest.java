package it.govpay.stampe.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import it.govpay.stampe.model.v2.Etichette;
import it.govpay.stampe.test.utils.AvvisiPagamentoFactory;

/**
 * Testi della specifica pagoPA 3.5 nell'avviso bilingue, variante "Avviso con seconda lingua" (issue #56):
 * "Dove pagare" con la variante dei canali digitali comune alla coppia di lingue, sito dell'ente con
 * {@code pay_on_website}, nota sulle rate con o senza rata unica.
 */
@SpringBootTest(classes = Application.class)
@DisplayName("Test Dove pagare nell'avviso bilingue")
@ActiveProfiles("test")
class DovePagareBilingueTest {

	@Autowired
	AvvisiPagamentoFactory avvisiPagamentoFactory;

	@Autowired
	AvvisoPagamentoBilingueMapper bilingueMapper;

	@Autowired
	@Qualifier("labelAvvisiProperties")
	LabelAvvisiProperties labelAvvisiProperties;

	private AvvisoPagamentoInput mappa(PaymentNotice avviso) {
		return this.bilingueMapper.toPaymentNoticeAvvisoPagamentoInput(LoggerFactory.getLogger(DovePagareBilingueTest.class), avviso,
				this.labelAvvisiProperties);
	}

	private PaymentNotice rataUnica(Languages lingua) {
		PaymentNotice avviso = this.avvisiPagamentoFactory.creaPaymentNoticeFull();
		avviso.getSecondLanguage().setLanguage(lingua);
		return avviso;
	}

	@Test
	void sloveno() {
		AvvisoPagamentoInput input = mappa(rataUnica(Languages.SL));
		Etichette it = input.getEtichette().getItaliano();
		Etichette sl = input.getEtichette().getTraduzione();
		assertEquals("DOVE PAGARE?", it.getCome());
		assertEquals("Kje plačati?", sl.getCome());
		assertEquals("PAGA CON L’APP IO", it.getPagaApp());
		assertEquals("Plačajte z aplikacijo IO", sl.getPagaApp());
		assertEquals("oppure dal tuo Home Banking, con la tua app di pagamento o con gli altri canali abilitati.", it.getPagaApp2());
		assertEquals("ali prek svoje spletne banke, z aplikacijo za plačevanje ali prek drugih razpoložljivih kanalov.", sl.getPagaApp2());
		assertTrue(it.getPagaTerritorio2().startsWith("presso Banche e Sportelli ATM"));
		assertTrue(sl.getPagaTerritorio2().startsWith("na bankah in okencih bankomatov"));
		assertEquals("DATI PER IL PAGAMENTO", it.getCanali());
		assertEquals("Podatki za plačilo", sl.getCanali());
	}

	@Test
	void tedesco() {
		// le stringhe tedesche hanno solo la variante "Online": anche l'italiano usa "Paga online"
		AvvisoPagamentoInput input = mappa(rataUnica(Languages.DE));
		Etichette it = input.getEtichette().getItaliano();
		Etichette de = input.getEtichette().getTraduzione();
		assertEquals("PAGA ONLINE", it.getPagaApp());
		assertEquals("Online", de.getPagaApp());
		assertEquals("con l’app IO, dal tuo Home Banking, con la tua app di pagamento o con gli altri canali abilitati.", it.getPagaApp2());
		assertEquals("mit der App IO, mittels Home Banking, einer Zahlungsapp oder anderer zugelassener Zahlungsdienstleister.", de.getPagaApp2());
		assertEquals("Wo bezahlen?", de.getCome());
		assertTrue(de.getPagaTerritorio2().startsWith("bei Banken und ATM-Schaltern"));
	}

	@Test
	void sitoDellEnte() {
		PaymentNotice avviso = rataUnica(Languages.SL);
		avviso.getCreditor().setBusinessName("Comune di Test");
		avviso.getCreditor().setPayOnWebsite(true);
		Etichette it = mappa(avviso).getEtichette().getItaliano();
		Etichette sl = mappa(avviso).getEtichette().getTraduzione();
		assertTrue(it.getPagaApp2().startsWith("oppure sul sito di Comune di Test, dal tuo Home Banking"));
		assertTrue(sl.getPagaApp2().startsWith("ali na spletnem mestu Comune di Test, prek svoje spletne banke"));

		avviso = rataUnica(Languages.DE);
		avviso.getCreditor().setBusinessName("Comune di Test");
		avviso.getCreditor().setPayOnWebsite(true);
		assertTrue(mappa(avviso).getEtichette().getTraduzione().getPagaApp2().startsWith("mit der App IO, auf Comune di Test, mittels Home Banking"));
	}

	@Test
	void notaSulleRate() {
		PaymentNotice soloRate = this.avvisiPagamentoFactory.creaPaymentNoticeConRate(3, false);
		soloRate.getSecondLanguage().setLanguage(Languages.SL);
		AvvisoPagamentoInput input = mappa(soloRate);
		assertEquals("Puoi pagare in 3 rate", input.getEtichette().getItaliano().getNota1());
		assertEquals("Plačate lahko v 3 obrokih", input.getEtichette().getTraduzione().getNota1());

		PaymentNotice conUnica = this.avvisiPagamentoFactory.creaPaymentNoticeConRate(3, true);
		conUnica.getSecondLanguage().setLanguage(Languages.DE);
		input = mappa(conUnica);
		assertEquals("Puoi pagare per intero o in 3 rate", input.getEtichette().getItaliano().getNota1());
		assertEquals("Es ist möglich, den Betrag vollständig oder in 3 Raten zu zahlen", input.getEtichette().getTraduzione().getNota1());
	}

	@Test
	void nessunaEtichettaMancante() {
		for (Languages lingua : new Languages[] { Languages.DE, Languages.SL }) {
			for (boolean postale : new boolean[] { false, true }) {
				PaymentNotice avviso = rataUnica(lingua);
				avviso.setPostal(postale);
				for (Etichette e : new Etichette[] { mappa(avviso).getEtichette().getItaliano(), mappa(avviso).getEtichette().getTraduzione() }) {
					for (String valore : new String[] { e.getCome(), e.getPagaApp(), e.getPagaApp2(), e.getPagaTerritorio(), e.getPagaTerritorio2(),
							e.getCanali(), e.getNota(), e.getQuantoQuando(), e.getDestinatarioAvviso() }) {
						assertNotNull(valore, lingua + " postale=" + postale);
						assertFalse(valore.isBlank(), lingua + " postale=" + postale);
					}
				}
			}
		}
	}
}
