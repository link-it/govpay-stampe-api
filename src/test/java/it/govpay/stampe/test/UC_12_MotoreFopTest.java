package it.govpay.stampe.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import tools.jackson.databind.ObjectMapper;

import it.govpay.stampe.Application;
import it.govpay.stampe.beans.PaymentNotice;
import it.govpay.stampe.test.costanti.Costanti;
import it.govpay.stampe.test.serializer.ObjectMapperUtils;
import it.govpay.stampe.test.utils.AvvisiPagamentoFactory;

/**
 * Motore FOP configurato per avviso semplice e postale (issue #37): l'avviso semplice e' stampato con
 * FOP in tutti i suoi layout, quello postale solo a rata unica; i layout postali non ancora migrati
 * restano su JasperReports. Il motore usato si riconosce dal Producer del PDF.
 */
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@DisplayName("Test Motore FOP")
@ActiveProfiles("test")
@TestPropertySource(properties = { "stampe.motore.avviso-semplice=fop", "stampe.motore.avviso-postale=fop" })
class UC_12_MotoreFopTest {

	private static final String PRODUCER_FOP = "Apache FOP";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	AvvisiPagamentoFactory avvisiPagamentoFactory;

	private ObjectMapper mapper = ObjectMapperUtils.createObjectMapper();

	private String stampa(PaymentNotice avviso) throws Exception {
		avviso.setSecondLanguage(null); // avviso monolingua
		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
				.content(this.mapper.writeValueAsString(avviso))
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
	void UC_12_04_AvvisoPostaleTriplaRataConJasper() throws Exception {
		PaymentNotice avviso = this.avvisiPagamentoFactory.creaPaymentNoticeTreRate();
		avviso.setPostal(true);
		assertFalse(stampa(avviso).contains(PRODUCER_FOP));
	}
}
