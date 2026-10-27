package it.govpay.stampe.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import tools.jackson.databind.ObjectMapper;

import it.govpay.stampe.Application;
import it.govpay.stampe.beans.CdsViolation;
import it.govpay.stampe.beans.PaymentNotice;
import it.govpay.stampe.beans.ReceiptStatus;
import it.govpay.stampe.beans.ReceiptVersion;
import it.govpay.stampe.test.costanti.Costanti;
import it.govpay.stampe.test.serializer.ObjectMapperUtils;
import it.govpay.stampe.test.utils.AvvisiPagamentoFactory;

/**
 * Avviso semplice, postale, bilingue, violazione del codice della strada e ricevuta sono stampati con
 * FOP in tutti i loro layout (issue #37). Il motore usato si riconosce dal Producer del PDF.
 */
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@DisplayName("Test Motore FOP")
@ActiveProfiles("test")
class UC_12_MotoreFopTest {

	private static final String PRODUCER_FOP = "Apache FOP";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	AvvisiPagamentoFactory avvisiPagamentoFactory;

	private ObjectMapper mapper = ObjectMapperUtils.createObjectMapper();

	private String stampa(PaymentNotice avviso) throws Exception {
		avviso.setSecondLanguage(null); // avviso monolingua
		return stampa(Costanti.STANDARD_PATH, avviso);
	}

	private String stampa(String percorso, Object richiesta) throws Exception {
		MvcResult result = this.mockMvc.perform(post(percorso)
				.content(this.mapper.writeValueAsString(richiesta))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andReturn();
		assertEquals(MediaType.APPLICATION_PDF_VALUE, result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE));
		String pdf = new String(result.getResponse().getContentAsByteArray(), StandardCharsets.ISO_8859_1);
		assertTrue(pdf.startsWith("%PDF-"));
		return pdf;
	}

	@Test
	void UC_12_01_AvvisoSempliceRataUnicaConFop() throws Exception {
		assertTrue(stampa(this.avvisiPagamentoFactory.creaPaymentNoticeFull()).contains(PRODUCER_FOP));
	}

	@Test
	void UC_12_02_AvvisoPostaleRataUnicaConFop() throws Exception {
		PaymentNotice avviso = this.avvisiPagamentoFactory.creaPaymentNoticeFull();
		avviso.setPostal(true);
		assertTrue(stampa(avviso).contains(PRODUCER_FOP));
	}

	@Test
	void UC_12_03_AvvisoSempliceDoppiaRataConFop() throws Exception {
		assertTrue(stampa(this.avvisiPagamentoFactory.creaPaymentNoticeDueRate()).contains(PRODUCER_FOP));
	}

	@Test
	void UC_12_05_AvvisoSempliceRateMultipleConFop() throws Exception {
		assertTrue(stampa(this.avvisiPagamentoFactory.creaPaymentNoticeConRate(13, true)).contains(PRODUCER_FOP));
	}

	@Test
	void UC_12_06_AvvisoSempliceSoglieConFop() throws Exception {
		assertTrue(stampa(this.avvisiPagamentoFactory.creaPaymentNoticeConSoglie(3, true, false, false)).contains(PRODUCER_FOP));
	}

	@Test
	void UC_12_04_AvvisoPostaleTriplaRataConFop() throws Exception {
		PaymentNotice avviso = this.avvisiPagamentoFactory.creaPaymentNoticeTreRate();
		avviso.setPostal(true);
		assertTrue(stampa(avviso).contains(PRODUCER_FOP));
	}

	@Test
	void UC_12_08_ViolazioneCdsConFop() throws Exception {
		assertTrue(stampa(Costanti.CDS_VIOLATION_PATH, this.avvisiPagamentoFactory.creaCdsViolation()).contains(PRODUCER_FOP));
	}

	@Test
	void UC_12_09_ViolazioneCdsPostaleConFop() throws Exception {
		CdsViolation violazione = this.avvisiPagamentoFactory.creaCdsViolation();
		violazione.setPostal(true);
		assertTrue(stampa(Costanti.CDS_VIOLATION_PATH, violazione).contains(PRODUCER_FOP));
	}

	@Test
	void UC_12_07_AvvisoPostaleRateConUnicaConFop() throws Exception {
		PaymentNotice avviso = this.avvisiPagamentoFactory.creaPaymentNoticeConRate(7, true);
		avviso.setPostal(true);
		assertTrue(stampa(avviso).contains(PRODUCER_FOP));
	}

	@Test
	void UC_12_10_RicevutaConFop() throws Exception {
		assertTrue(stampa(Costanti.RECEIPT_PATH, this.avvisiPagamentoFactory.creaReceipt(ReceiptVersion._230, ReceiptStatus.EXECUTED, 2, false)).contains(PRODUCER_FOP));
	}

	@Test
	void UC_12_11_RicevutaSuPiuPagineConFop() throws Exception {
		String pdf = stampa(Costanti.RECEIPT_PATH, this.avvisiPagamentoFactory.creaReceipt(ReceiptVersion._230, ReceiptStatus.EXECUTED, 30, true));
		// come il caso di riferimento Jasper ricevuta-230-molte-voci-testi-lunghi: le voci vanno su tre pagine
		assertTrue(pdf.contains(PRODUCER_FOP));
		assertTrue(pdf.contains("/Count 3"));
	}

	@Test
	void UC_12_12_AvvisoBilingueConFop() throws Exception {
		// la factory imposta lo sloveno come seconda lingua: pagina singola e pagina doppia
		assertTrue(stampa(Costanti.STANDARD_PATH, this.avvisiPagamentoFactory.creaPaymentNoticeConRate(2, true)).contains(PRODUCER_FOP));
	}

	@Test
	void UC_12_13_AvvisoBilinguePostaleSoglieConFop() throws Exception {
		// soglie con bollettino e senza rata unica: solo pagine doppie, che Jasper sovrappone
		String pdf = stampa(Costanti.STANDARD_PATH, this.avvisiPagamentoFactory.creaPaymentNoticeConSoglie(3, false, true, true));
		assertTrue(pdf.contains(PRODUCER_FOP));
		assertTrue(pdf.contains("/Count 2"));
	}
}
