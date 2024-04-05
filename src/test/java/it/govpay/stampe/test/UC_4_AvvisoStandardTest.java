package it.govpay.stampe.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.govpay.stampe.Application;
import it.govpay.stampe.beans.PaymentNotice;
import it.govpay.stampe.mapper.AvvisoPagamentoMapper;
import it.govpay.stampe.test.costanti.Costanti;
import it.govpay.stampe.test.serializer.ObjectMapperUtils;
import it.govpay.stampe.test.utils.AvvisiPagamentoFactory;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@DisplayName("Test Avvisi Standard")
@ActiveProfiles("test")
class UC_4_AvvisoStandardTest {

	@Autowired
	private MockMvc mockMvc;

	private ObjectMapper mapper = ObjectMapperUtils.createObjectMapper();

	@Autowired
	AvvisoPagamentoMapper avvisoPagamentoMapper;
	
	@Autowired
	AvvisiPagamentoFactory avvisiPagamentoFactory;

	@Test
	void UC_4_01_AvvisoSempliceRataUnicaOk() throws Exception {
		PaymentNotice avvisoRataUnica = this.avvisiPagamentoFactory.creaPaymentNoticeFull();
		avvisoRataUnica.setSecondLanguage(null); // avviso monolingua

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
		assertEquals(avvisoPagamentoMapper.nomePdf(avvisoRataUnica), AvvisiPagamentoFactory.extractFilename(headerContentDisposition));
	}

	@Test
	void UC_4_02_AvvisoPostaleRataUnicaOk() throws Exception {
		PaymentNotice avvisoRataUnica = this.avvisiPagamentoFactory.creaPaymentNoticeFull();
		avvisoRataUnica.setPostal(true);
		avvisoRataUnica.setSecondLanguage(null); // avviso monolingua

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
		assertEquals(avvisoPagamentoMapper.nomePdf(avvisoRataUnica), AvvisiPagamentoFactory.extractFilename(headerContentDisposition));
	}

	@Test
	void UC_4_03_AvvisoSempliceDoppiaRataOk() throws Exception {
		PaymentNotice avvisoRataUnica = this.avvisiPagamentoFactory.creaPaymentNoticeDueRate();
		avvisoRataUnica.setSecondLanguage(null); // avviso monolingua

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
		assertEquals(avvisoPagamentoMapper.nomePdf(avvisoRataUnica), AvvisiPagamentoFactory.extractFilename(headerContentDisposition));
	}

	@Test
	void UC_4_04_AvvisoPostaleDoppiaRataOk() throws Exception {
		PaymentNotice avvisoRataUnica = this.avvisiPagamentoFactory.creaPaymentNoticeDueRate();
		avvisoRataUnica.setPostal(true);
		avvisoRataUnica.setSecondLanguage(null); // avviso monolingua

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
		assertEquals(avvisoPagamentoMapper.nomePdf(avvisoRataUnica), AvvisiPagamentoFactory.extractFilename(headerContentDisposition));
	}

	@Test
	void UC_4_05_AvvisoSempliceTriplaRataOk() throws Exception {
		PaymentNotice avvisoRataUnica = this.avvisiPagamentoFactory.creaPaymentNoticeTreRate();
		avvisoRataUnica.setSecondLanguage(null); // avviso monolingua

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
		assertEquals(avvisoPagamentoMapper.nomePdf(avvisoRataUnica), AvvisiPagamentoFactory.extractFilename(headerContentDisposition));
	}

	@Test
	void UC_4_06_AvvisoPostaleTripleRataOk() throws Exception {
		PaymentNotice avvisoRataUnica = this.avvisiPagamentoFactory.creaPaymentNoticeTreRate();
		avvisoRataUnica.setPostal(true);
		avvisoRataUnica.setSecondLanguage(null); // avviso monolingua

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
		assertEquals(avvisoPagamentoMapper.nomePdf(avvisoRataUnica), AvvisiPagamentoFactory.extractFilename(headerContentDisposition));
	}

	@Test
	void UC_4_07_AvvisoSempliceRateMultipleOk() throws Exception {

		for (int i = 0; i < 15; i++) {
			PaymentNotice avvisoRataUnica = this.avvisiPagamentoFactory.creaPaymentNoticeConRate((i+1), false);
			avvisoRataUnica.setSecondLanguage(null); // avviso monolingua

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
			assertEquals(avvisoPagamentoMapper.nomePdf(avvisoRataUnica), AvvisiPagamentoFactory.extractFilename(headerContentDisposition));
		}
	}

	@Test
	void UC_4_08_AvvisoPostaleRateMultipleOk() throws Exception {
		for (int i = 0; i < 15; i++) {
			PaymentNotice avvisoRataUnica = this.avvisiPagamentoFactory.creaPaymentNoticeConRate((i+1), false);
			avvisoRataUnica.setPostal(true);
			avvisoRataUnica.setSecondLanguage(null); // avviso monolingua

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
			assertEquals(avvisoPagamentoMapper.nomePdf(avvisoRataUnica), AvvisiPagamentoFactory.extractFilename(headerContentDisposition));
		}
	}
	
	@Test
	void UC_4_09_AvvisoSempliceRateMultipleConRataUnicaOk() throws Exception {

		for (int i = 0; i < 16; i++) {
			PaymentNotice avvisoRataUnica = this.avvisiPagamentoFactory.creaPaymentNoticeConRate(i, true);
			avvisoRataUnica.setSecondLanguage(null); // avviso monolingua

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
			assertEquals(avvisoPagamentoMapper.nomePdf(avvisoRataUnica), AvvisiPagamentoFactory.extractFilename(headerContentDisposition));
		}
	}

	@Test
	void UC_4_10_AvvisoPostaleRateMultipleConRataUnicaOk() throws Exception {
		for (int i = 0; i < 16; i++) {
			PaymentNotice avvisoRataUnica = this.avvisiPagamentoFactory.creaPaymentNoticeConRate(i, true);
			avvisoRataUnica.setPostal(true);
			avvisoRataUnica.setSecondLanguage(null); // avviso monolingua

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
			assertEquals(avvisoPagamentoMapper.nomePdf(avvisoRataUnica), AvvisiPagamentoFactory.extractFilename(headerContentDisposition));
		}
	}
}

