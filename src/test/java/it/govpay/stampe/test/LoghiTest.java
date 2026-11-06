package it.govpay.stampe.test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import tools.jackson.databind.ObjectMapper;

import it.govpay.stampe.Application;
import it.govpay.stampe.beans.PaymentNotice;
import it.govpay.stampe.beans.Receipt;
import it.govpay.stampe.beans.ReceiptStatus;
import it.govpay.stampe.beans.ReceiptVersion;
import it.govpay.stampe.test.costanti.Costanti;
import it.govpay.stampe.test.serializer.ObjectMapperUtils;
import it.govpay.stampe.test.utils.AvvisiPagamentoFactory;

/**
 * Loghi di avviso e ricevuta (issue #38): byte dell'immagine nei campi binari, tipo riconosciuto dal contenuto;
 * per l'avviso anche la URI data che i client mandano oggi; formato non riconosciuto: 400.
 */
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@DisplayName("Test Loghi")
@ActiveProfiles("test")
class LoghiTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	AvvisiPagamentoFactory avvisiPagamentoFactory;

	@Value("${stampe.logoEnte}")
	String logoEnte;

	private ObjectMapper mapper = ObjectMapperUtils.createObjectMapper();

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

	@Test
	void avvisoConLogoNonValido() throws Exception {
		PaymentNotice avviso = avviso();
		avviso.setFirstLogo(new ByteArrayResource("non e' un'immagine".getBytes(StandardCharsets.UTF_8)));
		stampa(Costanti.STANDARD_PATH, avviso, status().isBadRequest());
	}

	@Test
	void ricevutaConImmagine() throws Exception {
		Receipt ricevuta = this.avvisiPagamentoFactory.creaReceipt(ReceiptVersion.SANP_240, ReceiptStatus.EXECUTED, 2, false);
		assertTrue(ricevuta.getCreditorLogo() != null);
		stampa(Costanti.RECEIPT_PATH, ricevuta, status().isCreated());
	}

	@Test
	void ricevutaConLogoNonValido() throws Exception {
		Receipt ricevuta = this.avvisiPagamentoFactory.creaReceipt(ReceiptVersion.SANP_240, ReceiptStatus.EXECUTED, 2, false);
		ricevuta.setPagopaLogo(new ByteArrayResource("non e' un'immagine".getBytes(StandardCharsets.UTF_8)));
		stampa(Costanti.RECEIPT_PATH, ricevuta, status().isBadRequest());
	}
}
