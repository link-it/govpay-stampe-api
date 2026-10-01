package it.govpay.stampe.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import tools.jackson.databind.ObjectMapper;

import it.govpay.stampe.Application;
import it.govpay.stampe.beans.PaymentNotice;
import it.govpay.stampe.config.LabelAvvisiConfiguration.LabelAvvisiProperties;
import it.govpay.stampe.mapper.AvvisoPagamentoBilingueMapper;
import it.govpay.stampe.model.v2.AvvisoPagamentoInput;
import it.govpay.stampe.model.v2.PaginaAvvisoDoppia;
import it.govpay.stampe.model.v2.RataAvviso;
import it.govpay.stampe.test.costanti.Costanti;
import it.govpay.stampe.test.serializer.ObjectMapperUtils;
import it.govpay.stampe.test.utils.AvvisiPagamentoFactory;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@DisplayName("Test Avvisi Bilingue")
@ActiveProfiles("test")
class UC_5_AvvisoBilingueTest {

	private static final Logger logger = LoggerFactory.getLogger(UC_5_AvvisoBilingueTest.class);

	@Autowired
	private MockMvc mockMvc;

	private ObjectMapper mapper = ObjectMapperUtils.createObjectMapper();

	@Autowired
	AvvisoPagamentoBilingueMapper avvisoPagamentoBilingueMapper;

	@Autowired
	@Qualifier("labelAvvisiProperties")
	LabelAvvisiProperties labelAvvisiProperties;

	@Autowired
	AvvisiPagamentoFactory avvisiPagamentoFactory;

	@Test
	void UC_5_01_AvvisoBilingueRataUnicaOk() throws Exception {
		PaymentNotice avvisoRataUnica = this.avvisiPagamentoFactory.creaPaymentNoticeFull();
		

		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andReturn();

		String headerContentType = result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE);
		assertNotNull(headerContentType);
		assertEquals(MediaType.APPLICATION_PDF_VALUE, headerContentType);
		String headerContentDisposition = result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION);
		assertNotNull(headerContentDisposition);
		assertEquals(avvisoPagamentoBilingueMapper.nomePdf(avvisoRataUnica), AvvisiPagamentoFactory.extractFilename(headerContentDisposition));
	}

	@Test
	void UC_5_02_AvvisoBilinguePostaleRataUnicaOk() throws Exception {
		PaymentNotice avvisoRataUnica = this.avvisiPagamentoFactory.creaPaymentNoticeFull();
		avvisoRataUnica.setPostal(true);
		

		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andReturn();

		String headerContentType = result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE);
		assertNotNull(headerContentType);
		assertEquals(MediaType.APPLICATION_PDF_VALUE, headerContentType);
		String headerContentDisposition = result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION);
		assertNotNull(headerContentDisposition);
		assertEquals(avvisoPagamentoBilingueMapper.nomePdf(avvisoRataUnica), AvvisiPagamentoFactory.extractFilename(headerContentDisposition));
	}

	@Test
	void UC_5_03_AvvisoBilingueDoppiaRataOk() throws Exception {
		PaymentNotice avvisoRataUnica = this.avvisiPagamentoFactory.creaPaymentNoticeDueRate();
		

		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andReturn();

		String headerContentType = result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE);
		assertNotNull(headerContentType);
		assertEquals(MediaType.APPLICATION_PDF_VALUE, headerContentType);
		String headerContentDisposition = result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION);
		assertNotNull(headerContentDisposition);
		assertEquals(avvisoPagamentoBilingueMapper.nomePdf(avvisoRataUnica), AvvisiPagamentoFactory.extractFilename(headerContentDisposition));
	}

	@Test
	void UC_5_04_AvvisoBilinguePostaleDoppiaRataOk() throws Exception {
		PaymentNotice avvisoRataUnica = this.avvisiPagamentoFactory.creaPaymentNoticeDueRate();
		avvisoRataUnica.setPostal(true);
		

		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andReturn();

		String headerContentType = result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE);
		assertNotNull(headerContentType);
		assertEquals(MediaType.APPLICATION_PDF_VALUE, headerContentType);
		String headerContentDisposition = result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION);
		assertNotNull(headerContentDisposition);
		assertEquals(avvisoPagamentoBilingueMapper.nomePdf(avvisoRataUnica), AvvisiPagamentoFactory.extractFilename(headerContentDisposition));
	}

	@Test
	void UC_5_05_AvvisoBilingueTriplaRataOk() throws Exception {
		PaymentNotice avvisoRataUnica = this.avvisiPagamentoFactory.creaPaymentNoticeTreRate();
		

		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andReturn();

		String headerContentType = result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE);
		assertNotNull(headerContentType);
		assertEquals(MediaType.APPLICATION_PDF_VALUE, headerContentType);
		String headerContentDisposition = result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION);
		assertNotNull(headerContentDisposition);
		assertEquals(avvisoPagamentoBilingueMapper.nomePdf(avvisoRataUnica), AvvisiPagamentoFactory.extractFilename(headerContentDisposition));
	}

	@Test
	void UC_5_06_AvvisoBilinguePostaleTripleRataOk() throws Exception {
		PaymentNotice avvisoRataUnica = this.avvisiPagamentoFactory.creaPaymentNoticeTreRate();
		avvisoRataUnica.setPostal(true);
		

		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andReturn();

		String headerContentType = result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE);
		assertNotNull(headerContentType);
		assertEquals(MediaType.APPLICATION_PDF_VALUE, headerContentType);
		String headerContentDisposition = result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION);
		assertNotNull(headerContentDisposition);
		assertEquals(avvisoPagamentoBilingueMapper.nomePdf(avvisoRataUnica), AvvisiPagamentoFactory.extractFilename(headerContentDisposition));
	}

	@Test
	void UC_5_07_AvvisoBilingueRateMultipleOk() throws Exception {

		for (int i = 0; i < 15; i++) {
			PaymentNotice avvisoRataUnica = this.avvisiPagamentoFactory.creaPaymentNoticeConRate((i+1), false);
			

			String body = mapper.writeValueAsString(avvisoRataUnica);

			MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
					.content(body)
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isCreated())
					.andReturn();

			String headerContentType = result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE);
			assertNotNull(headerContentType);
			assertEquals(MediaType.APPLICATION_PDF_VALUE, headerContentType);
			String headerContentDisposition = result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION);
			assertNotNull(headerContentDisposition);
			assertEquals(avvisoPagamentoBilingueMapper.nomePdf(avvisoRataUnica), AvvisiPagamentoFactory.extractFilename(headerContentDisposition));
		}
	}

	@Test
	void UC_5_08_AvvisoBilinguePostaleRateMultipleOk() throws Exception {
		for (int i = 0; i < 15; i++) {
			PaymentNotice avvisoRataUnica = this.avvisiPagamentoFactory.creaPaymentNoticeConRate((i+1), false);
			avvisoRataUnica.setPostal(true);
			

			String body = mapper.writeValueAsString(avvisoRataUnica);

			MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
					.content(body)
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isCreated())
					.andReturn();

			String headerContentType = result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE);
			assertNotNull(headerContentType);
			assertEquals(MediaType.APPLICATION_PDF_VALUE, headerContentType);
			String headerContentDisposition = result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION);
			assertNotNull(headerContentDisposition);
			assertEquals(avvisoPagamentoBilingueMapper.nomePdf(avvisoRataUnica), AvvisiPagamentoFactory.extractFilename(headerContentDisposition));
		}
	}
	
	@Test
	void UC_5_09_AvvisoBilingueRateMultipleConRataUnicaOk() throws Exception {

		for (int i = 0; i < 16; i++) {
			PaymentNotice avvisoRataUnica = this.avvisiPagamentoFactory.creaPaymentNoticeConRate(i, true);
			

			String body = mapper.writeValueAsString(avvisoRataUnica);

			MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
					.content(body)
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isCreated())
					.andReturn();

			String headerContentType = result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE);
			assertNotNull(headerContentType);
			assertEquals(MediaType.APPLICATION_PDF_VALUE, headerContentType);
			String headerContentDisposition = result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION);
			assertNotNull(headerContentDisposition);
			assertEquals(avvisoPagamentoBilingueMapper.nomePdf(avvisoRataUnica), AvvisiPagamentoFactory.extractFilename(headerContentDisposition));
		}
	}

	@Test
	void UC_5_10_AvvisoBilinguePostaleRateMultipleConRataUnicaOk() throws Exception {
		for (int i = 0; i < 16; i++) {
			PaymentNotice avvisoRataUnica = this.avvisiPagamentoFactory.creaPaymentNoticeConRate(i, true);
			avvisoRataUnica.setPostal(true);
			

			String body = mapper.writeValueAsString(avvisoRataUnica);

			MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
					.content(body)
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isCreated())
					.andReturn();

			String headerContentType = result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE);
			assertNotNull(headerContentType);
			assertEquals(MediaType.APPLICATION_PDF_VALUE, headerContentType);
			String headerContentDisposition = result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION);
			assertNotNull(headerContentDisposition);
			assertEquals(avvisoPagamentoBilingueMapper.nomePdf(avvisoRataUnica), AvvisiPagamentoFactory.extractFilename(headerContentDisposition));
		}
	}
	
	@Test
	void UC_5_11_AvvisoPostaleRataUnicaOk() throws Exception {
		PaymentNotice avvisoRataUnica = this.avvisiPagamentoFactory.creaPaymentNoticeFull();
		avvisoRataUnica.setPostal(true);
		avvisoRataUnica.getFull().getIban().setOwnerBusinessName(null); // senza ridefinire l'owner

		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andReturn();

		String headerContentType = result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE);
		assertNotNull(headerContentType);
		assertEquals(MediaType.APPLICATION_PDF_VALUE, headerContentType);
		String headerContentDisposition = result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION);
		assertNotNull(headerContentDisposition);
		assertEquals(avvisoPagamentoBilingueMapper.nomePdf(avvisoRataUnica), AvvisiPagamentoFactory.extractFilename(headerContentDisposition));
	}
	
	@Test
	void UC_5_12_AvvisoPostaleDoppiaRataOk() throws Exception {
		PaymentNotice avvisoRataUnica = this.avvisiPagamentoFactory.creaPaymentNoticeDueRate();
		avvisoRataUnica.setPostal(true);
		avvisoRataUnica.getInstalments().get(0).getIban().setOwnerBusinessName(null); // senza ridefinire l'owner
		avvisoRataUnica.getInstalments().get(1).getIban().setOwnerBusinessName(null); // senza ridefinire l'owner

		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andReturn();

		String headerContentType = result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE);
		assertNotNull(headerContentType);
		assertEquals(MediaType.APPLICATION_PDF_VALUE, headerContentType);
		String headerContentDisposition = result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION);
		assertNotNull(headerContentDisposition);
		assertEquals(avvisoPagamentoBilingueMapper.nomePdf(avvisoRataUnica), AvvisiPagamentoFactory.extractFilename(headerContentDisposition));
	}

	// ==================== issue #34 ====================

	/**
	 * {@code poste} e' letto da tutti i template V2 per decidere se mostrare il bollettino
	 * postale: senza valorizzarlo (bug originale) l'avviso bilingue postale non lo mostrerebbe
	 * nonostante datamatrix/numero CC/autorizzazione siano gia' calcolati nella rata.
	 */
	@Test
	@DisplayName("UC_5_13: avviso bilingue postale valorizza 'poste', quello non postale lo lascia assente")
	void UC_5_13_AvvisoBilinguePosteValorizzatoSoloSePostale() {
		PaymentNotice avvisoPostale = this.avvisiPagamentoFactory.creaPaymentNoticeFull();
		avvisoPostale.setPostal(true);
		AvvisoPagamentoInput inputPostale = this.avvisoPagamentoBilingueMapper
				.toPaymentNoticeAvvisoPagamentoInput(logger, avvisoPostale, this.labelAvvisiProperties);
		assertTrue(Boolean.TRUE.equals(inputPostale.isPoste()));

		PaymentNotice avvisoStandard = this.avvisiPagamentoFactory.creaPaymentNoticeFull();
		AvvisoPagamentoInput inputStandard = this.avvisoPagamentoBilingueMapper
				.toPaymentNoticeAvvisoPagamentoInput(logger, avvisoStandard, this.labelAvvisiProperties);
		assertNull(inputStandard.isPoste());
	}

	/**
	 * Bug originale: nel ramo "rata con numeroRata" di {@code impostaLabelsNellaRataAvviso} la
	 * label tradotta per "rata unica entro il" veniva scritta con {@code setScadenzaTra} invece
	 * di {@code setScadenzaUnicaTra}, lasciando {@code scadenzaUnicaTra} sempre null e
	 * sovrascrivendo il valore (gia' corretto) di {@code scadenzaTra}.
	 */
	@Test
	@DisplayName("UC_5_14: rate con lingua secondaria valorizzano scadenzaUnicaTra, non sovrascrivono scadenzaTra")
	void UC_5_14_RataConLinguaSecondariaValorizzaScadenzaUnicaTra() {
		PaymentNotice avviso = this.avvisiPagamentoFactory.creaPaymentNoticeDueRate();

		AvvisoPagamentoInput input = this.avvisoPagamentoBilingueMapper
				.toPaymentNoticeAvvisoPagamentoInput(logger, avviso, this.labelAvvisiProperties);

		RataAvviso rata = input.getPagine().getSingolaOrDoppia().stream()
				.filter(PaginaAvvisoDoppia.class::isInstance)
				.map(PaginaAvvisoDoppia.class::cast)
				.findFirst().orElseThrow()
				.getRata().get(0);

		String scadenzaUnicaTraAttesa = this.labelAvvisiProperties.getSl().get("rata_unica_entro_il");
		assertEquals(scadenzaUnicaTraAttesa, rata.getScadenzaUnicaTra());
		assertNotEquals(scadenzaUnicaTraAttesa, rata.getScadenzaTra());
	}

	/**
	 * Bug originale: {@code creaRatePerAvvisoBilingue} scriveva la nota importo della lingua
	 * SECONDARIA nelle etichette {@code italiano}, lasciando {@code traduzione} a null —
	 * esattamente invertito. {@code creaRateRidottePerAvvisoBilingue} (soglie ridotte) non ha
	 * mai avuto questo bug ed e' il riferimento del comportamento corretto.
	 */
	@Test
	@DisplayName("UC_5_15: nota importo (rata unica) va in italiano con la lingua principale, in "
			+ "traduzione con la secondaria")
	void UC_5_15_NotaImportoNonScambiataTraLingue() {
		PaymentNotice avviso = this.avvisiPagamentoFactory.creaPaymentNoticeFull();

		AvvisoPagamentoInput input = this.avvisoPagamentoBilingueMapper
				.toPaymentNoticeAvvisoPagamentoInput(logger, avviso, this.labelAvvisiProperties);

		String notaImportoIta = this.labelAvvisiProperties.getIt().get("nota_importo");
		String notaImportoSl = this.labelAvvisiProperties.getSl().get("nota_importo");

		assertEquals(notaImportoIta, input.getEtichette().getItaliano().getNota1());
		assertEquals(notaImportoSl, input.getEtichette().getTraduzione().getNota1());
		assertFalse(notaImportoIta.equals(notaImportoSl));
	}
}

