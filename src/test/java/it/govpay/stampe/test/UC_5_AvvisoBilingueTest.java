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
import it.govpay.stampe.mapper.AvvisoPagamentoBilingueMapper;
import it.govpay.stampe.test.costanti.Costanti;
import it.govpay.stampe.test.serializer.ObjectMapperUtils;
import it.govpay.stampe.test.utils.Utils;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@DisplayName("Test Avvisi Bilingue")
@ActiveProfiles("test")
class UC_5_AvvisoBilingueTest {

	@Autowired
	private MockMvc mockMvc;

	private ObjectMapper mapper = ObjectMapperUtils.createObjectMapper();
	
	@Autowired
	AvvisoPagamentoBilingueMapper avvisoPagamentoBilingueMapper;

	@Test
	void UC_5_01_AvvisoBilingueRataUnicaOk() throws Exception {
		PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeFull();
		

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
		assertEquals(avvisoPagamentoBilingueMapper.nomePdf(avvisoRataUnica), Utils.extractFilename(headerContentDisposition));
	}

	@Test
	void UC_5_02_AvvisoBilinguePostaleRataUnicaOk() throws Exception {
		PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeFull();
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
		assertEquals(avvisoPagamentoBilingueMapper.nomePdf(avvisoRataUnica), Utils.extractFilename(headerContentDisposition));
	}

	@Test
	void UC_5_03_AvvisoBilingueDoppiaRataOk() throws Exception {
		PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeDueRate();
		

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
		assertEquals(avvisoPagamentoBilingueMapper.nomePdf(avvisoRataUnica), Utils.extractFilename(headerContentDisposition));
	}

	@Test
	void UC_5_04_AvvisoBilinguePostaleDoppiaRataOk() throws Exception {
		PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeDueRate();
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
		assertEquals(avvisoPagamentoBilingueMapper.nomePdf(avvisoRataUnica), Utils.extractFilename(headerContentDisposition));
	}

	@Test
	void UC_5_05_AvvisoBilingueTriplaRataOk() throws Exception {
		PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeTreRate();
		

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
		assertEquals(avvisoPagamentoBilingueMapper.nomePdf(avvisoRataUnica), Utils.extractFilename(headerContentDisposition));
	}

	@Test
	void UC_5_06_AvvisoBilinguePostaleTripleRataOk() throws Exception {
		PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeTreRate();
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
		assertEquals(avvisoPagamentoBilingueMapper.nomePdf(avvisoRataUnica), Utils.extractFilename(headerContentDisposition));
	}

	@Test
	void UC_5_07_AvvisoBilingueRateMultipleOk() throws Exception {

		for (int i = 0; i < 15; i++) {
			PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeConRate((i+1), false);
			

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
			assertEquals(avvisoPagamentoBilingueMapper.nomePdf(avvisoRataUnica), Utils.extractFilename(headerContentDisposition));
		}
	}

	@Test
	void UC_5_08_AvvisoBilinguePostaleRateMultipleOk() throws Exception {
		for (int i = 0; i < 15; i++) {
			PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeConRate((i+1), false);
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
			assertEquals(avvisoPagamentoBilingueMapper.nomePdf(avvisoRataUnica), Utils.extractFilename(headerContentDisposition));
		}
	}
	
	@Test
	void UC_5_09_AvvisoBilingueRateMultipleConRataUnicaOk() throws Exception {

		for (int i = 0; i < 16; i++) {
			PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeConRate(i, true);
			

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
			assertEquals(avvisoPagamentoBilingueMapper.nomePdf(avvisoRataUnica), Utils.extractFilename(headerContentDisposition));
		}
	}

	@Test
	void UC_5_10_AvvisoBilinguePostaleRateMultipleConRataUnicaOk() throws Exception {
		for (int i = 0; i < 16; i++) {
			PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeConRate(i, true);
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
			assertEquals(avvisoPagamentoBilingueMapper.nomePdf(avvisoRataUnica), Utils.extractFilename(headerContentDisposition));
		}
	}
}

