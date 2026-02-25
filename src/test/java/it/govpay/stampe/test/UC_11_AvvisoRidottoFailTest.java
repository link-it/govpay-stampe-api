package it.govpay.stampe.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.govpay.stampe.Application;
import it.govpay.stampe.beans.Instalment;
import it.govpay.stampe.beans.PaymentNotice;
import it.govpay.stampe.beans.ThresholdPayment;
import it.govpay.stampe.beans.ThresholdType;
import it.govpay.stampe.test.costanti.Costanti;
import it.govpay.stampe.test.serializer.ObjectMapperUtils;
import it.govpay.stampe.test.utils.AvvisiPagamentoFactory;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@DisplayName("Test Validazione Avvisi con Pagamento in Forma Ridotta")
@ActiveProfiles("test")
class UC_11_AvvisoRidottoFailTest {

	@Autowired
	private MockMvc mockMvc;

	private ObjectMapper mapper = ObjectMapperUtils.createObjectMapper();

	@Autowired
	AvvisiPagamentoFactory avvisiPagamentoFactory;

	@Test
	void UC_11_01_AvvisoRidotto_MistiRateESoglie() throws Exception {
		// Creo un avviso con rate
		PaymentNotice avviso = this.avvisiPagamentoFactory.creaPaymentNoticeConRate(2, false);
		avviso.setSecondLanguage(null);

		// Aggiungo anche soglie ridotte -> deve fallire mutua esclusivita'
		PaymentNotice avvisoConSoglie = this.avvisiPagamentoFactory.creaPaymentNoticeConSoglie(2, false, false, false);
		avviso.setReducedPayments(avvisoConSoglie.getReducedPayments());

		String body = mapper.writeValueAsString(avviso);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andReturn();

		JsonReader reader = Json.createReader(new ByteArrayInputStream(result.getResponse().getContentAsByteArray()));
		JsonObject problem = reader.readObject();
		assertNotNull(problem.getString("type"));
		assertNotNull(problem.getString("title"));
		assertNotNull(problem.getString("detail"));
		assertEquals(422, problem.getInt("status"));
		assertEquals("Unprocessable Entity", problem.getString("title"));
		assertTrue(problem.getString("detail").contains("Non e' possibile indicare sia rate che pagamenti in forma ridotta"));
	}

	@Test
	void UC_11_02_AvvisoRidotto_PostaleSenzaIban() throws Exception {
		PaymentNotice avviso = this.avvisiPagamentoFactory.creaPaymentNoticeConSoglie(2, false, true, false);

		// Rimuovo IBAN dalla prima soglia
		avviso.getReducedPayments().get(0).setIban(null);

		String body = mapper.writeValueAsString(avviso);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andReturn();

		JsonReader reader = Json.createReader(new ByteArrayInputStream(result.getResponse().getContentAsByteArray()));
		JsonObject problem = reader.readObject();
		assertNotNull(problem.getString("type"));
		assertNotNull(problem.getString("title"));
		assertNotNull(problem.getString("detail"));
		assertEquals(422, problem.getInt("status"));
		assertEquals("Unprocessable Entity", problem.getString("title"));
		assertTrue(problem.getString("detail").contains("Iban obbligatorio in caso di avviso postale"));
	}

	@Test
	void UC_11_03_AvvisoRidotto_NessunImporto() throws Exception {
		// Creo un avviso senza full, senza rate e senza soglie
		PaymentNotice avviso = this.avvisiPagamentoFactory.creaPaymentNoticeConSoglie(2, false, false, false);
		avviso.setFull(null);
		avviso.setInstalments(null);
		avviso.setReducedPayments(null);

		String body = mapper.writeValueAsString(avviso);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andReturn();

		JsonReader reader = Json.createReader(new ByteArrayInputStream(result.getResponse().getContentAsByteArray()));
		JsonObject problem = reader.readObject();
		assertNotNull(problem.getString("type"));
		assertNotNull(problem.getString("title"));
		assertNotNull(problem.getString("detail"));
		assertEquals(422, problem.getInt("status"));
		assertEquals("Unprocessable Entity", problem.getString("title"));
		assertTrue(problem.getString("detail").contains("Indicare la rata unica, almeno una rata o almeno un pagamento in forma ridotta"));
	}
}
