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
import it.govpay.stampe.mapper.AvvisoPagamentoMapper;
import it.govpay.stampe.test.costanti.Costanti;
import it.govpay.stampe.test.serializer.ObjectMapperUtils;
import it.govpay.stampe.test.utils.AvvisiPagamentoFactory;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@DisplayName("Test Avvisi Standard PDF")
@ActiveProfiles("test")
class UC_6_AvvisoStandardPdfTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper = ObjectMapperUtils.createObjectMapper();
    
	@Autowired
	AvvisiPagamentoFactory avvisiPagamentoFactory;

    @Autowired
    AvvisoPagamentoMapper avvisoPagamentoMapper;

    private static final String PDF_SAVE_DIRECTORY = "/tmp/avvisipagamento";

    private void savePdfResponseToFile(MvcResult result, String basePath) throws IOException {
    	File basePathFile = new File(basePath);
    	
    	if (!basePathFile.exists()) {
    		basePathFile.mkdir();
		}
    	
        byte[] pdfContent = result.getResponse().getContentAsByteArray();
        String contentDisposition = result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION);
        String filename = AvvisiPagamentoFactory.extractFilename(contentDisposition);

        System.out.println(filename);
        
        File outputFile = new File(basePath + File.separator + filename);
        
		if (!outputFile.exists()) {
			outputFile.createNewFile();
		}
        
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(pdfContent);
        }
    }

    // Esempio di test modificato per salvare il PDF
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

        assertNotNull(result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE));
        assertEquals(MediaType.APPLICATION_PDF_VALUE, result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE));
        assertNotNull(result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION));

        // Salva il PDF sul file system
        savePdfResponseToFile(result, PDF_SAVE_DIRECTORY);
    }

    // UC_4_02_AvvisoPostaleRataUnicaOk
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

        assertNotNull(result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE));
        assertEquals(MediaType.APPLICATION_PDF_VALUE, result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE));
        assertNotNull(result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION));

        // Salva il PDF sul file system
        savePdfResponseToFile(result, PDF_SAVE_DIRECTORY);
    }

    // UC_4_03_AvvisoSempliceDoppiaRataOk
    @Test
    void UC_4_03_AvvisoSempliceDoppiaRataOk() throws Exception {
        PaymentNotice avvisoDoppiaRata = this.avvisiPagamentoFactory.creaPaymentNoticeDueRate();
        avvisoDoppiaRata.setSecondLanguage(null); // avviso monolingua

        String body = mapper.writeValueAsString(avvisoDoppiaRata);

        MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        assertNotNull(result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE));
        assertEquals(MediaType.APPLICATION_PDF_VALUE, result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE));
        assertNotNull(result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION));

        // Salva il PDF sul file system
        savePdfResponseToFile(result, PDF_SAVE_DIRECTORY);
    }

    // UC_4_04_AvvisoPostaleDoppiaRataOk
    @Test
    void UC_4_04_AvvisoPostaleDoppiaRataOk() throws Exception {
        PaymentNotice avvisoPostaleDoppiaRata = this.avvisiPagamentoFactory.creaPaymentNoticeDueRate();
        avvisoPostaleDoppiaRata.setPostal(true);
        avvisoPostaleDoppiaRata.setSecondLanguage(null); // avviso monolingua

        String body = mapper.writeValueAsString(avvisoPostaleDoppiaRata);

        MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        assertNotNull(result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE));
        assertEquals(MediaType.APPLICATION_PDF_VALUE, result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE));
        assertNotNull(result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION));

        // Salva il PDF sul file system
        savePdfResponseToFile(result, PDF_SAVE_DIRECTORY);
    }

    // UC_4_05_AvvisoSempliceTriplaRataOk
    @Test
    void UC_4_05_AvvisoSempliceTriplaRataOk() throws Exception {
        PaymentNotice avvisoTriplaRata = this.avvisiPagamentoFactory.creaPaymentNoticeTreRate();
        avvisoTriplaRata.setSecondLanguage(null); // avviso monolingua

        String body = mapper.writeValueAsString(avvisoTriplaRata);

        MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        assertNotNull(result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE));
        assertEquals(MediaType.APPLICATION_PDF_VALUE, result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE));
        assertNotNull(result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION));

        // Salva il PDF sul file system
        savePdfResponseToFile(result, PDF_SAVE_DIRECTORY);
    }

    // UC_4_06_AvvisoPostaleTripleRataOk
    @Test
    void UC_4_06_AvvisoPostaleTripleRataOk() throws Exception {
        PaymentNotice avvisoPostaleTriplaRata = this.avvisiPagamentoFactory.creaPaymentNoticeTreRate();
        avvisoPostaleTriplaRata.setPostal(true);
        avvisoPostaleTriplaRata.setSecondLanguage(null); // avviso monolingua

        String body = mapper.writeValueAsString(avvisoPostaleTriplaRata);

        MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        assertNotNull(result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE));
        assertEquals(MediaType.APPLICATION_PDF_VALUE, result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE));
        assertNotNull(result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION));

        // Salva il PDF sul file system
        savePdfResponseToFile(result, PDF_SAVE_DIRECTORY);
    }

    // UC_4_07_AvvisoSempliceRateMultipleOk
    @Test
    void UC_4_07_AvvisoSempliceRateMultipleOk() throws Exception {
        for (int i = 0; i < 15; i++) {
            PaymentNotice avvisoRateMultiple = this.avvisiPagamentoFactory.creaPaymentNoticeConRate((i + 1), false);
            avvisoRateMultiple.setSecondLanguage(null); // avviso monolingua

            String body = mapper.writeValueAsString(avvisoRateMultiple);

            MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
                    .content(body)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andReturn();

            assertNotNull(result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE));
            assertEquals(MediaType.APPLICATION_PDF_VALUE, result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE));
            assertNotNull(result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION));

            // Salva il PDF sul file system
            savePdfResponseToFile(result, PDF_SAVE_DIRECTORY);
        }
    }

    // UC_4_08_AvvisoPostaleRateMultipleOk
    @Test
    void UC_4_08_AvvisoPostaleRateMultipleOk() throws Exception {
        for (int i = 0; i < 15; i++) {
            PaymentNotice avvisoPostaleRateMultiple = this.avvisiPagamentoFactory.creaPaymentNoticeConRate((i + 1), false);
            avvisoPostaleRateMultiple.setPostal(true);
            avvisoPostaleRateMultiple.setSecondLanguage(null); // avviso monolingua

            String body = mapper.writeValueAsString(avvisoPostaleRateMultiple);

            MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
                    .content(body)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andReturn();

            assertNotNull(result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE));
            assertEquals(MediaType.APPLICATION_PDF_VALUE, result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE));
            assertNotNull(result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION));

            // Salva il PDF sul file system
            savePdfResponseToFile(result, PDF_SAVE_DIRECTORY);
        }
    }

    // UC_4_09_AvvisoSempliceRateMultipleConRataUnicaOk
    @Test
    void UC_4_09_AvvisoSempliceRateMultipleConRataUnicaOk() throws Exception {
        for (int i = 0; i < 16; i++) {
            PaymentNotice avvisoRateMultipleConRataUnica = this.avvisiPagamentoFactory.creaPaymentNoticeConRate(i, true);
            avvisoRateMultipleConRataUnica.setSecondLanguage(null); // avviso monolingua

            String body = mapper.writeValueAsString(avvisoRateMultipleConRataUnica);

            MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
                    .content(body)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andReturn();

            assertNotNull(result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE));
            assertEquals(MediaType.APPLICATION_PDF_VALUE, result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE));
            assertNotNull(result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION));

            // Salva il PDF sul file system
            savePdfResponseToFile(result, PDF_SAVE_DIRECTORY);
        }
    }

    // UC_4_10_AvvisoPostaleRateMultipleConRataUnicaOk
    @Test
    void UC_4_10_AvvisoPostaleRateMultipleConRataUnicaOk() throws Exception {
        for (int i = 0; i < 16; i++) {
            PaymentNotice avvisoPostaleRateMultipleConRataUnica = this.avvisiPagamentoFactory.creaPaymentNoticeConRate(i, true);
            avvisoPostaleRateMultipleConRataUnica.setPostal(true);
            avvisoPostaleRateMultipleConRataUnica.setSecondLanguage(null); // avviso monolingua

            String body = mapper.writeValueAsString(avvisoPostaleRateMultipleConRataUnica);

            MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
                    .content(body)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andReturn();

            assertNotNull(result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE));
            assertEquals(MediaType.APPLICATION_PDF_VALUE, result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE));
            assertNotNull(result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION));

            // Salva il PDF sul file system
            savePdfResponseToFile(result, PDF_SAVE_DIRECTORY);
        }
    }
    
    
    @Test
	void UC_4_11_AvvisoSempliceRataUnica_NoScadenzaOk() throws Exception {
		PaymentNotice avvisoRataUnica = this.avvisiPagamentoFactory.creaPaymentNoticeFull();
		avvisoRataUnica.setSecondLanguage(null); // avviso monolingua
		avvisoRataUnica.getFull().setDueDate(null); // senza data scadenza

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
		
		   // Salva il PDF sul file system
        savePdfResponseToFile(result, PDF_SAVE_DIRECTORY);
	}

	@Test
	void UC_4_12_AvvisoPostaleRataUnica_NoScadenzaOk() throws Exception {
		PaymentNotice avvisoRataUnica = this.avvisiPagamentoFactory.creaPaymentNoticeFull();
		avvisoRataUnica.setPostal(true);
		avvisoRataUnica.setSecondLanguage(null); // avviso monolingua
		avvisoRataUnica.getFull().setDueDate(null); // senza data scadenza

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
		
		   // Salva il PDF sul file system
        savePdfResponseToFile(result, PDF_SAVE_DIRECTORY);
	}
}