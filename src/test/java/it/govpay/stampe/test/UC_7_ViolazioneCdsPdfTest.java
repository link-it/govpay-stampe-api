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
import it.govpay.stampe.beans.CdsViolation;
import it.govpay.stampe.mapper.ViolazioneCdsMapper;
import it.govpay.stampe.test.costanti.Costanti;
import it.govpay.stampe.test.serializer.ObjectMapperUtils;
import it.govpay.stampe.test.utils.AvvisiPagamentoFactory;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@DisplayName("Test Avvisi Violazione CDS")
@ActiveProfiles("test")
class UC_7_ViolazioneCdsPdfTest {

	@Autowired
	private MockMvc mockMvc;

	private ObjectMapper mapper = ObjectMapperUtils.createObjectMapper();
	
	@Autowired
	ViolazioneCdsMapper violazioneCdsMapper;
	
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
	void UC_2_01_ViolazioneCdsOk() throws Exception {
		CdsViolation cdsViolation = this.avvisiPagamentoFactory.creaCdsViolation();
		
		
		String body = mapper.writeValueAsString(cdsViolation);

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andReturn();

		String headerContentType = result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE);
		assertNotNull(headerContentType);
		assertEquals(MediaType.APPLICATION_PDF_VALUE, headerContentType);
		String headerContentDisposition = result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION);
		assertNotNull(headerContentDisposition);
		assertEquals(violazioneCdsMapper.nomePdf(cdsViolation), AvvisiPagamentoFactory.extractFilename(headerContentDisposition));
		
		 // Salva il PDF sul file system
        savePdfResponseToFile(result, PDF_SAVE_DIRECTORY);
	}
	
	@Test
	void UC_2_02_ViolazioneCdsPostaleOk() throws Exception {
		CdsViolation cdsViolation = this.avvisiPagamentoFactory.creaCdsViolation();
		cdsViolation.setPostal(true);
		
		
		String body = mapper.writeValueAsString(cdsViolation);

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andReturn();

		String headerContentType = result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE);
		assertNotNull(headerContentType);
		assertEquals(MediaType.APPLICATION_PDF_VALUE, headerContentType);
		String headerContentDisposition = result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION);
		assertNotNull(headerContentDisposition);
		assertEquals(violazioneCdsMapper.nomePdf(cdsViolation), AvvisiPagamentoFactory.extractFilename(headerContentDisposition));
		
		 // Salva il PDF sul file system
        savePdfResponseToFile(result, PDF_SAVE_DIRECTORY);
	}
}

