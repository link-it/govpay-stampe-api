package it.govpay.stampe.fop;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamResult;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.ActiveProfiles;

import jakarta.xml.bind.JAXBElement;

import it.govpay.stampe.Application;
import it.govpay.stampe.beans.CdsViolation;
import it.govpay.stampe.beans.Instalment;
import it.govpay.stampe.beans.Languages;
import it.govpay.stampe.beans.PaymentNotice;
import it.govpay.stampe.beans.Receipt;
import it.govpay.stampe.beans.ReceiptStatus;
import it.govpay.stampe.beans.ReceiptVersion;
import it.govpay.stampe.config.LabelAvvisiConfiguration.LabelAvvisiProperties;
import it.govpay.stampe.mapper.AvvisoPagamentoBilingueMapper;
import it.govpay.stampe.mapper.AvvisoPagamentoMapper;
import it.govpay.stampe.mapper.RicevutaMapper;
import it.govpay.stampe.mapper.ViolazioneCdsMapper;
import it.govpay.stampe.model.v1.AvvisoPagamentoInput;
import it.govpay.stampe.model.v1.RicevutaTelematicaInput;
import it.govpay.stampe.test.utils.AvvisiPagamentoFactory;

/**
 * Genera gli XML di input di {@link RegressioneLayoutTest}: per ogni variante costruisce la richiesta con
 * {@link AvvisiPagamentoFactory} e la converte con i mapper, come il controller, nel modello del foglio.
 * I loghi sono sostituiti da {@value RegressioneLayoutTest#SEGNAPOSTO_LOGO}: il test li ripristina dal
 * logo di prova, cosi' gli XML restano piccoli.
 * <p>
 * Si esegue solo su richiesta, perche' la factory produce codici avviso casuali e date del giorno:
 * {@code mvn test -Dtest=GeneraCasiRegressioneTest -Dregressione.genera=true}, poi si aggiornano gli hash
 * attesi con {@code -Dtest=RegressioneLayoutTest -Dregressione.aggiorna=true}.
 */
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
class GeneraCasiRegressioneTest {

	private static final Pattern LOGO = Pattern.compile("<(logo_ente|logo_ente_secondario|logo_pagopa)>[^<]{100,}</");

	@Autowired
	AvvisiPagamentoFactory factory;

	@Autowired
	AvvisoPagamentoMapper mapper;

	@Autowired
	ViolazioneCdsMapper violazioneCdsMapper;

	@Autowired
	RicevutaMapper ricevutaMapper;

	@Autowired
	AvvisoPagamentoBilingueMapper bilingueMapper;

	@Autowired
	@Qualifier("labelAvvisiProperties")
	LabelAvvisiProperties labelAvvisiProperties;

	@Autowired
	@Qualifier("jaxb2MarshallerAvvisoPagamento")
	Jaxb2Marshaller marshaller;

	@Autowired
	@Qualifier("jaxb2MarshallerAvvisoPagamentoBilingue")
	Jaxb2Marshaller marshallerBilingue;

	/** Avviso bilingue: resta con la seconda lingua e passa dal mapper V2, come nel controller. */
	private record Bilingue(PaymentNotice avviso) {
	}

	@Test
	void generaCasi() throws Exception {
		Assumptions.assumeTrue(Boolean.getBoolean("regressione.genera"), "regressione.genera non indicata");
		Path dir = Path.of("src", "test", "resources", RegressioneLayoutTest.CARTELLA);
		for (Map.Entry<String, Supplier<Object>> caso : casi().entrySet()) {
			StringWriter xml = new StringWriter();
			String foglio;
			Object richiesta = caso.getValue().get();
			if (richiesta instanceof Receipt ricevuta) {
				foglio = "ricevuta";
				this.marshaller.createMarshaller().marshal(new JAXBElement<>(new QName("", "root"), RicevutaTelematicaInput.class,
						this.ricevutaMapper.toRicevutaTelematicaInput(ricevuta)), new StreamResult(xml));
			} else if (richiesta instanceof Bilingue bilingue) {
				foglio = "avviso-bilingue";
				this.marshallerBilingue.createMarshaller().marshal(new JAXBElement<>(new QName("", "AvvisoPagamentoInput"),
						it.govpay.stampe.model.v2.AvvisoPagamentoInput.class, this.bilingueMapper.toPaymentNoticeAvvisoPagamentoInput(
								LoggerFactory.getLogger(GeneraCasiRegressioneTest.class), bilingue.avviso(), this.labelAvvisiProperties)),
						new StreamResult(xml));
			} else if (richiesta instanceof CdsViolation violazione) {
				// come il controller: con il bollettino la violazione usa il foglio dell'avviso postale
				boolean postale = Boolean.TRUE.equals(violazione.getPostal());
				foglio = postale ? "avviso-postale" : "violazione-cds";
				this.marshaller.createMarshaller().marshal(new JAXBElement<>(new QName("", postale ? "AvvisoPagamentoInput" : "ViolazioneInput"),
						AvvisoPagamentoInput.class, this.violazioneCdsMapper.toViolazioneAvvisoPagamentoInput(violazione, this.labelAvvisiProperties)),
						new StreamResult(xml));
			} else {
				PaymentNotice avviso = (PaymentNotice) richiesta;
				avviso.setSecondLanguage(null);
				AvvisoPagamentoInput input = this.mapper.toPaymentNoticeAvvisoPagamentoInput(
						LoggerFactory.getLogger(GeneraCasiRegressioneTest.class), avviso, this.labelAvvisiProperties);
				foglio = input.getDiPoste() != null ? "avviso-postale" : "avviso-semplice";
				this.marshaller.createMarshaller().marshal(new JAXBElement<>(new QName("", "AvvisoPagamentoInput"), AvvisoPagamentoInput.class, input),
						new StreamResult(xml));
			}
			Path file = dir.resolve(foglio).resolve(caso.getKey() + ".xml");
			Files.createDirectories(file.getParent());
			Files.writeString(file, LOGO.matcher(xml.toString()).replaceAll("<$1>" + RegressioneLayoutTest.SEGNAPOSTO_LOGO + "</"), StandardCharsets.UTF_8);
			System.out.printf("caso di regressione: %s%n", file);
		}
	}

	private Map<String, Supplier<Object>> casi() {
		Map<String, Supplier<Object>> casi = new LinkedHashMap<>();
		// avviso semplice: rata unica, pagine doppia e tripla, soglie, pagine multiple a 2 e 3 colonne
		casi.put("unica", () -> rate(0, true, false));
		casi.put("doppia-unica", () -> rate(2, true, false));
		casi.put("tripla", () -> rate(3, false, false));
		casi.put("soglie-2-unica", () -> this.factory.creaPaymentNoticeConSoglie(2, true, false, false));
		casi.put("soglie-3", () -> this.factory.creaPaymentNoticeConSoglie(3, false, false, false));
		casi.put("doppia-senza-indirizzo", () -> senzaIndirizzo(rate(2, true, false)));
		casi.put("tripla-due-loghi-oggetto-lungo", () -> dueLoghiOggettoLungo(rate(3, true, false)));
		casi.put("multipla-4", () -> rate(4, false, false));
		casi.put("multipla-5-unica", () -> rate(5, true, false));
		casi.put("multipla-13-unica", () -> rate(13, true, false));
		// avviso postale: rata unica, pagine doppia e tripla, da sole e insieme alla singola
		casi.put("postale-unica", () -> rate(0, true, true));
		casi.put("postale-2", () -> rate(2, false, true));
		casi.put("postale-3-unica", () -> rate(3, true, true));
		casi.put("postale-5", () -> rate(5, false, true));
		// violazione del codice della strada: importo ridotto e scontato, con e senza bollettino
		casi.put("cds", this.factory::creaCdsViolation);
		casi.put("cds-due-loghi-oggetto-lungo", () -> {
			CdsViolation v = this.factory.creaCdsViolation();
			v.setSecondLogo(new ByteArrayResource(v.getFirstLogo() instanceof ByteArrayResource b ? b.getByteArray() : new byte[0]));
			v.setTitle("Verbale n. 12345/2026 per violazione dell'art. 142 comma 8 del Codice della Strada, eccesso di velocita' rilevato in via Roma");
			return v;
		});
		casi.put("cds-postale", () -> {
			CdsViolation v = this.factory.creaCdsViolation();
			v.setPostal(true);
			return v;
		});
		// ricevuta: versioni SANP, stati, voci su una e piu' pagine, testi lunghi
		casi.put("ricevuta-230", () -> this.factory.creaReceipt(ReceiptVersion._230, ReceiptStatus.EXECUTED, 2, false));
		casi.put("ricevuta-240-v2-non-eseguita", () -> this.factory.creaReceipt(ReceiptVersion._240_V2, ReceiptStatus.NOT_EXECUTED, 1, false));
		casi.put("ricevuta-240-sei-voci", () -> this.factory.creaReceipt(ReceiptVersion._240, ReceiptStatus.EXECUTED, 6, false));
		casi.put("ricevuta-230-molte-voci-testi-lunghi", () -> this.factory.creaReceipt(ReceiptVersion._230, ReceiptStatus.EXECUTED, 30, true));
		// avviso bilingue in tedesco e sloveno: rata unica, rate, soglie, bollettino, due loghi
		casi.put("bilingue-unica-de", () -> bilingue(rate(0, true, false), Languages.DE));
		casi.put("bilingue-doppia-sl", () -> bilingue(rate(2, false, false), Languages.SL));
		casi.put("bilingue-5-rate-unica-sl", () -> bilingue(rate(5, true, false), Languages.SL));
		casi.put("bilingue-soglie-2-unica-de", () -> bilingue(this.factory.creaPaymentNoticeConSoglie(2, true, false, true), Languages.DE));
		casi.put("bilingue-postale-unica-sl", () -> bilingue(rate(0, true, true), Languages.SL));
		casi.put("bilingue-postale-3-unica-sl", () -> bilingue(rate(3, true, true), Languages.SL));
		casi.put("bilingue-due-loghi-oggetto-lungo-de", () -> {
			Bilingue b = bilingue(dueLoghiOggettoLungo(rate(2, true, false)), Languages.DE);
			b.avviso().getSecondLanguage().setTitle("Abfallgebuehr (TARI) Jahr 2026, Saldo und Vorauszahlung fuer die Immobilie in der Via Roma 1, Blatt 12, Parzelle 345");
			return b;
		});
		casi.put("bilingue-senza-indirizzo-sl", () -> bilingue(senzaIndirizzo(rate(2, true, false)), Languages.SL));
		return casi;
	}

	private static PaymentNotice senzaIndirizzo(PaymentNotice p) {
		p.getDebtor().setAddressLine1(null);
		p.getDebtor().setAddressLine2(null);
		return p;
	}

	private static PaymentNotice dueLoghiOggettoLungo(PaymentNotice p) {
		p.setSecondLogo(new ByteArrayResource(p.getFirstLogo() instanceof ByteArrayResource b ? b.getByteArray() : new byte[0]));
		p.setTitle("Tassa sui rifiuti (TARI) anno 2026, saldo e acconto per l'immobile di via Roma 1, foglio 12, particella 345, subalterno 6");
		return p;
	}

	/** Avviso con la seconda lingua indicata, titolo compreso. */
	private static Bilingue bilingue(PaymentNotice p, Languages lingua) {
		p.getSecondLanguage().setLanguage(lingua);
		p.getSecondLanguage().setTitle(p.getTitle() + " (" + lingua + ")");
		return new Bilingue(p);
	}

	/** Avviso con N rate di importi e scadenze diversi, con o senza rata unica. */
	private PaymentNotice rate(int numero, boolean rataUnica, boolean postale) {
		PaymentNotice p = this.factory.creaPaymentNoticeConRate(numero, rataUnica);
		p.setPostal(postale);
		LocalDate inizio = LocalDate.of(2026, 11, 30);
		int i = 0;
		for (Instalment rata : p.getInstalments()) {
			rata.setDueDate(inizio.plusMonths(i));
			rata.setAmount(Double.valueOf(37.5 + 12.25 * i));
			i++;
		}
		if (p.getFull() != null) {
			p.getFull().setDueDate(inizio);
			// senza rate (solo rata unica) un importo qualsiasi, diverso da zero
			p.getFull().setAmount(numero == 0 ? Double.valueOf(122.5) : Double.valueOf(37.5 * numero + 12.25 * numero * (numero - 1) / 2));
		}
		return p;
	}
}
