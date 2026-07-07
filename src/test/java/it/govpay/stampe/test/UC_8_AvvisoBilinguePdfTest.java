package it.govpay.stampe.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
import it.govpay.stampe.mapper.AvvisoPagamentoBilingueMapper;
import it.govpay.stampe.test.costanti.Costanti;
import it.govpay.stampe.test.serializer.ObjectMapperUtils;
import it.govpay.stampe.test.utils.AvvisiPagamentoFactory;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@DisplayName("Test Avvisi Bilingue")
@ActiveProfiles("test")
class UC_8_AvvisoBilinguePdfTest {

	@Autowired
	private MockMvc mockMvc;

	private ObjectMapper mapper = ObjectMapperUtils.createObjectMapper();
	
	@Autowired
	AvvisoPagamentoBilingueMapper avvisoPagamentoBilingueMapper;
	
	@Autowired
	AvvisiPagamentoFactory avvisiPagamentoFactory;
	
	private static final String PDF_SAVE_DIRECTORY = "/tmp/avvisipagamento";

    private void savePdfResponseToFile(MvcResult result, String basePath) throws IOException {
    	File basePathFile = new File(basePath);
    	
    	if (!basePathFile.exists()) {
    		basePathFile.mkdir();
		}
    	
        byte[] pdfContent = result.getResponse().getContentAsByteArray();
        String contentDisposition = result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION);
        String filename = AvvisiPagamentoFactory.extractFilename(contentDisposition);

        File outputFile = new File(basePath + File.separator + filename);
        
		if (!outputFile.exists()) {
			outputFile.createNewFile();
		}
        
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(pdfContent);
        }
    }

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
		
		// Salva il PDF sul file system
        savePdfResponseToFile(result, PDF_SAVE_DIRECTORY);
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
		
		// Salva il PDF sul file system
        savePdfResponseToFile(result, PDF_SAVE_DIRECTORY);
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
		
		// Salva il PDF sul file system
        savePdfResponseToFile(result, PDF_SAVE_DIRECTORY);
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
		
		// Salva il PDF sul file system
        savePdfResponseToFile(result, PDF_SAVE_DIRECTORY);
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
		
		// Salva il PDF sul file system
        savePdfResponseToFile(result, PDF_SAVE_DIRECTORY);
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
		
		// Salva il PDF sul file system
        savePdfResponseToFile(result, PDF_SAVE_DIRECTORY);
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
			
			// Salva il PDF sul file system
	        savePdfResponseToFile(result, PDF_SAVE_DIRECTORY);
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
			
			// Salva il PDF sul file system
	        savePdfResponseToFile(result, PDF_SAVE_DIRECTORY);
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
			
			// Salva il PDF sul file system
	        savePdfResponseToFile(result, PDF_SAVE_DIRECTORY);
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
			
			// Salva il PDF sul file system
	        savePdfResponseToFile(result, PDF_SAVE_DIRECTORY);
		}
	}
}

