package it.govpay.stampe.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import tools.jackson.databind.ObjectMapper;

import it.govpay.stampe.Application;
import it.govpay.stampe.beans.CdsViolation;
import it.govpay.stampe.beans.PaymentNotice;
import it.govpay.stampe.beans.Receipt;
import it.govpay.stampe.beans.ReceiptStatus;
import it.govpay.stampe.beans.ReceiptVersion;
import it.govpay.stampe.fop.MotoreFop;
import it.govpay.stampe.test.costanti.Costanti;
import it.govpay.stampe.test.serializer.ObjectMapperUtils;
import it.govpay.stampe.test.utils.AvvisiPagamentoFactory;
import it.govpay.stampe.test.utils.LoghiDiProva;

/**
 * Loghi di avviso e ricevuta (issue #38): byte dell'immagine nei campi binari, tipo riconosciuto dal contenuto;
 * per l'avviso anche la URI data che i client mandano oggi. Un logo che non si stampa, compreso un formato non
 * riconosciuto, non blocca il documento: esce senza quel logo e con un WARN nel log (issue #67).
 */
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@DisplayName("Test Loghi")
@ActiveProfiles("test")
class LoghiTest {

	private static final String NON_STAMPABILE = "non stampabile nel documento";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	AvvisiPagamentoFactory avvisiPagamentoFactory;

	@Value("${stampe.logoEnte}")
	String logoEnte;

	private ObjectMapper mapper = ObjectMapperUtils.createObjectMapper();

	/** I WARN di MotoreFop: nei test il log di it.govpay e' spento, qui si raccolgono a parte. */
	private final ListAppender<ILoggingEvent> log = new ListAppender<>();

	private Logger loggerMotore() {
		return (Logger) org.slf4j.LoggerFactory.getLogger(MotoreFop.class);
	}

	@BeforeEach
	void raccogliLog() {
		this.log.start();
		loggerMotore().setLevel(Level.WARN);
		loggerMotore().addAppender(this.log);
	}

	@AfterEach
	void fermaLog() {
		loggerMotore().detachAppender(this.log);
		loggerMotore().setLevel(null);
		this.log.stop();
	}

	/** Il logo come lo manda un client: i byte dell'immagine, o la stringa della URI data. */
	private static ByteArrayResource contenuto(LoghiDiProva.Logo logo) {
		if (logo.valore().startsWith("data:")) {
			return new ByteArrayResource(logo.valore().getBytes(StandardCharsets.US_ASCII));
		}
		try {
			return new ByteArrayResource(Base64.getDecoder().decode(logo.valore()));
		} catch (IllegalArgumentException e) {
			return new ByteArrayResource(logo.valore().getBytes(StandardCharsets.US_ASCII));
		}
	}

	static List<LoghiDiProva.Logo> loghi() {
		return LoghiDiProva.tutti();
	}

	private void verificaLog(boolean stampabile, String campo) {
		List<String> warn = this.log.list.stream().map(ILoggingEvent::getFormattedMessage).filter(m -> m.contains(NON_STAMPABILE)).toList();
		if (stampabile) {
			assertTrue(warn.isEmpty(), warn.toString());
		} else {
			assertTrue(warn.stream().anyMatch(m -> m.startsWith("Logo [" + campo + "] dell'ente [")), warn.toString());
			// la causa nel log non riporta il logo intero
			assertFalse(warn.stream().anyMatch(m -> m.contains(";base64,")), warn.toString());
		}
		this.log.list.clear();
	}

	private void stampa(String percorso, Object richiesta, ResultMatcher esito) throws Exception {
		this.mockMvc.perform(post(percorso)
				.content(this.mapper.writeValueAsString(richiesta))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(esito);
	}

	private PaymentNotice avviso() {
		PaymentNotice avviso = this.avvisiPagamentoFactory.creaPaymentNoticeFull();
		avviso.setSecondLanguage(null);
		return avviso;
	}

	@Test
	void avvisoConImmagine() throws Exception {
		PaymentNotice avviso = avviso();
		avviso.setFirstLogo(AvvisiPagamentoFactory.immagine(this.logoEnte));
		avviso.setSecondLogo(AvvisiPagamentoFactory.immagine(this.logoEnte));
		stampa(Costanti.STANDARD_PATH, avviso, status().isCreated());
	}

	@Test
	void avvisoConUriDataPrecedente() throws Exception {
		PaymentNotice avviso = avviso();
		avviso.setFirstLogo(new ByteArrayResource(this.logoEnte.getBytes(StandardCharsets.US_ASCII)));
		stampa(Costanti.STANDARD_PATH, avviso, status().isCreated());
	}

	@ParameterizedTest
	@MethodSource("loghi")
	void avvisoConLogoDiProva(LoghiDiProva.Logo logo) throws Exception {
		PaymentNotice avviso = avviso();
		avviso.setFirstLogo(contenuto(logo));
		stampa(Costanti.STANDARD_PATH, avviso, status().isCreated());
		verificaLog(logo.stampabile(), "first_logo");
	}

	@Test
	void avvisoConUnLogoNonStampabileSuDue() throws Exception {
		PaymentNotice avviso = avviso();
		avviso.setFirstLogo(AvvisiPagamentoFactory.immagine(this.logoEnte));
		avviso.setSecondLogo(new ByteArrayResource("non e' un'immagine".getBytes(StandardCharsets.UTF_8)));
		stampa(Costanti.STANDARD_PATH, avviso, status().isCreated());
		assertFalse(this.log.list.stream().anyMatch(e -> e.getFormattedMessage().startsWith("Logo [first_logo]")), "segnalato anche first_logo");
		verificaLog(false, "second_logo");
	}

	@Test
	void altriAvvisiConLogoNonStampabile() throws Exception {
		ByteArrayResource svgEsterno = new ByteArrayResource(("<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" width=\"240\" height=\"120\">"
				+ "<image width=\"240\" height=\"120\" xlink:href=\"https://example.org/logo.png\"/></svg>").getBytes(StandardCharsets.UTF_8));

		PaymentNotice postale = this.avvisiPagamentoFactory.creaPaymentNoticeConSoglie(2, true, true, false);
		postale.setFirstLogo(svgEsterno);
		stampa(Costanti.STANDARD_PATH, postale, status().isCreated());
		verificaLog(false, "first_logo");

		PaymentNotice bilingue = this.avvisiPagamentoFactory.creaPaymentNoticeFull();
		bilingue.setFirstLogo(svgEsterno);
		stampa(Costanti.STANDARD_PATH, bilingue, status().isCreated());
		verificaLog(false, "first_logo");

		CdsViolation violazione = this.avvisiPagamentoFactory.creaCdsViolation();
		violazione.setFirstLogo(svgEsterno);
		stampa(Costanti.CDS_VIOLATION_PATH, violazione, status().isCreated());
		verificaLog(false, "first_logo");
	}

	@Test
	void ricevutaConImmagine() throws Exception {
		Receipt ricevuta = this.avvisiPagamentoFactory.creaReceipt(ReceiptVersion.SANP_240, ReceiptStatus.EXECUTED, 2, false);
		assertTrue(ricevuta.getCreditorLogo() != null);
		stampa(Costanti.RECEIPT_PATH, ricevuta, status().isCreated());
	}

	@ParameterizedTest
	@MethodSource("loghi")
	void ricevutaConLogoDiProva(LoghiDiProva.Logo logo) throws Exception {
		Receipt ricevuta = this.avvisiPagamentoFactory.creaReceipt(ReceiptVersion.SANP_240, ReceiptStatus.EXECUTED, 2, false);
		ricevuta.setCreditorLogo(contenuto(logo));
		stampa(Costanti.RECEIPT_PATH, ricevuta, status().isCreated());
		verificaLog(logo.stampabile(), "creditor_logo");
	}

	@Test
	void ricevutaConLogoPagopaNonStampabile() throws Exception {
		Receipt ricevuta = this.avvisiPagamentoFactory.creaReceipt(ReceiptVersion.SANP_240, ReceiptStatus.EXECUTED, 2, false);
		ricevuta.setPagopaLogo(new ByteArrayResource("non e' un'immagine".getBytes(StandardCharsets.UTF_8)));
		stampa(Costanti.RECEIPT_PATH, ricevuta, status().isCreated());
		verificaLog(false, "pagopa_logo");
	}
}
