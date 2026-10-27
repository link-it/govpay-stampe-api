package it.govpay.stampe.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayInputStream;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import tools.jackson.databind.ObjectMapper;

import it.govpay.stampe.Application;
import it.govpay.stampe.beans.NoticeMetadataSecondLanguage;
import it.govpay.stampe.beans.PaymentNotice;
import it.govpay.stampe.beans.PaymentNoticeOption;
import it.govpay.stampe.beans.Languages;
import it.govpay.stampe.test.costanti.Costanti;
import it.govpay.stampe.test.serializer.ObjectMapperUtils;
import it.govpay.stampe.test.utils.AvvisiPagamentoFactory;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@DisplayName("Test Validazione Avvisi con Opzioni di Pagamento Alternative")
@ActiveProfiles("test")
class UC_12_AvvisoOpzioniPagamentoFailTest {

	@Autowired
	private MockMvc mockMvc;

	private ObjectMapper mapper = ObjectMapperUtils.createObjectMapper();

	@Autowired
	AvvisiPagamentoFactory avvisiPagamentoFactory;

	@Test
	void UC_12_01_Opzioni_MutuaEsclusivitaConFull() throws Exception {
		PaymentNotice avviso = this.avvisiPagamentoFactory.creaPaymentNoticeConOpzioni(
				this.avvisiPagamentoFactory.creaOpzioneFull(), this.avvisiPagamentoFactory.creaOpzioneConRate(2));
		// full a livello di documento, insieme a options: deve fallire
		avviso.setFull(this.avvisiPagamentoFactory.creaOpzioneFull().getFull());

		String body = mapper.writeValueAsString(avviso);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andReturn();

		JsonReader reader = Json.createReader(new ByteArrayInputStream(result.getResponse().getContentAsByteArray()));
		JsonObject problem = reader.readObject();
		assertNotNull(problem.getString("type"));
		assertEquals(422, problem.getInt("status"));
		assertTrue(problem.getString("detail")
				.contains("Non e' possibile indicare sia 'options' che 'full'/'instalments'/'reducedPayments'"));
	}

	@Test
	void UC_12_02_Opzioni_NonSupportatoConBilinguismo() throws Exception {
		PaymentNotice avviso = this.avvisiPagamentoFactory.creaPaymentNoticeConOpzioni(
				this.avvisiPagamentoFactory.creaOpzioneFull(), this.avvisiPagamentoFactory.creaOpzioneConRate(2));

		NoticeMetadataSecondLanguage secondLanguage = new NoticeMetadataSecondLanguage();
		secondLanguage.setBilinguism(true);
		secondLanguage.setLanguage(Languages.SL);
		secondLanguage.setTitle("Avviso nella seconda lingua");
		avviso.setSecondLanguage(secondLanguage);

		String body = mapper.writeValueAsString(avviso);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andReturn();

		JsonReader reader = Json.createReader(new ByteArrayInputStream(result.getResponse().getContentAsByteArray()));
		JsonObject problem = reader.readObject();
		assertEquals(422, problem.getInt("status"));
		assertTrue(problem.getString("detail")
				.contains("'options' non e' ancora supportato in combinazione con l'avviso bilingue"));
	}

	@Test
	void UC_12_03_Opzioni_UnaOpzioneSenzaImporto() throws Exception {
		PaymentNoticeOption opzioneVuota = new PaymentNoticeOption();
		PaymentNotice avviso = this.avvisiPagamentoFactory.creaPaymentNoticeConOpzioni(
				this.avvisiPagamentoFactory.creaOpzioneFull(), opzioneVuota);

		String body = mapper.writeValueAsString(avviso);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andReturn();

		JsonReader reader = Json.createReader(new ByteArrayInputStream(result.getResponse().getContentAsByteArray()));
		JsonObject problem = reader.readObject();
		assertEquals(422, problem.getInt("status"));
		assertTrue(problem.getString("detail").contains("Ogni elemento di 'options' deve indicare"));
	}

	@Test
	void UC_12_04_Opzioni_UnaOpzioneConRateESoglieInsieme() throws Exception {
		PaymentNoticeOption opzioneMista = this.avvisiPagamentoFactory.creaOpzioneConRate(2);
		// aggiungo anche reducedPayments alla stessa opzione -> mutua esclusivita' interna
		PaymentNotice conSoglie = this.avvisiPagamentoFactory.creaPaymentNoticeConSoglie(2, false, false, false);
		opzioneMista.setReducedPayments(conSoglie.getReducedPayments());

		PaymentNotice avviso = this.avvisiPagamentoFactory.creaPaymentNoticeConOpzioni(opzioneMista,
				this.avvisiPagamentoFactory.creaOpzioneFull());

		String body = mapper.writeValueAsString(avviso);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andReturn();

		JsonReader reader = Json.createReader(new ByteArrayInputStream(result.getResponse().getContentAsByteArray()));
		JsonObject problem = reader.readObject();
		assertEquals(422, problem.getInt("status"));
		assertTrue(problem.getString("detail")
				.contains("Non e' possibile indicare sia rate che pagamenti in forma ridotta nello stesso elemento"));
	}

	@Test
	void UC_12_05_Opzioni_UnaSolaOpzione_Rifiutata() throws Exception {
		PaymentNotice avviso = this.avvisiPagamentoFactory.creaPaymentNoticeConOpzioni(
				this.avvisiPagamentoFactory.creaOpzioneFull());

		String body = mapper.writeValueAsString(avviso);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		JsonReader reader = Json.createReader(new ByteArrayInputStream(result.getResponse().getContentAsByteArray()));
		JsonObject problem = reader.readObject();
		assertEquals(400, problem.getInt("status"));
		assertTrue(problem.getString("detail").contains("size must be between 2"));
	}

	@Test
	void UC_12_06_Opzioni_PostaleSenzaIbanSuUnaOpzione() throws Exception {
		PaymentNotice avviso = this.avvisiPagamentoFactory.creaPaymentNoticeConOpzioni(
				this.avvisiPagamentoFactory.creaOpzioneFull(), this.avvisiPagamentoFactory.creaOpzioneConRate(2));
		avviso.setPostal(true);
		avviso.getOptions().get(1).getInstalments().get(0).setIban(null);

		String body = mapper.writeValueAsString(avviso);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andReturn();

		JsonReader reader = Json.createReader(new ByteArrayInputStream(result.getResponse().getContentAsByteArray()));
		JsonObject problem = reader.readObject();
		assertEquals(422, problem.getInt("status"));
		assertTrue(problem.getString("detail").contains("Iban obbligatorio in caso di avviso postale"));
	}

	@Test
	void UC_12_07_Opzioni_ElementoNull_Rifiutato() throws Exception {
		PaymentNotice avviso = this.avvisiPagamentoFactory.creaPaymentNoticeConOpzioni(
				this.avvisiPagamentoFactory.creaOpzioneFull(), this.avvisiPagamentoFactory.creaOpzioneConRate(2));
		avviso.getOptions().set(1, null);

		String body = mapper.writeValueAsString(avviso);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andReturn();

		JsonReader reader = Json.createReader(new ByteArrayInputStream(result.getResponse().getContentAsByteArray()));
		JsonObject problem = reader.readObject();
		assertEquals(422, problem.getInt("status"));
		assertTrue(problem.getString("detail").contains("Un elemento di 'options' non puo' essere null"));
	}

	@Test
	void UC_12_08_Opzioni_FullConUnaSolaRataNonPostale_Rifiutata() throws Exception {
		PaymentNoticeOption opzioneMista = this.avvisiPagamentoFactory.creaOpzioneFull();
		opzioneMista.setInstalments(this.avvisiPagamentoFactory.creaOpzioneConRate(1).getInstalments());

		PaymentNotice avviso = this.avvisiPagamentoFactory.creaPaymentNoticeConOpzioni(opzioneMista,
				this.avvisiPagamentoFactory.creaOpzioneFull());

		String body = mapper.writeValueAsString(avviso);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andReturn();

		JsonReader reader = Json.createReader(new ByteArrayInputStream(result.getResponse().getContentAsByteArray()));
		JsonObject problem = reader.readObject();
		assertEquals(422, problem.getInt("status"));
		assertTrue(problem.getString("detail").contains("'full' insieme a esattamente una rata non e' stampabile"));
	}

	@Test
	void UC_12_09_Opzioni_PostaleConIntestatariDiversi_Rifiutata() throws Exception {
		PaymentNotice avviso = this.avvisiPagamentoFactory.creaPaymentNoticeConOpzioni(
				this.avvisiPagamentoFactory.creaOpzioneFull(), this.avvisiPagamentoFactory.creaOpzioneFull());
		avviso.setPostal(true);
		avviso.getOptions().get(0).getFull().getIban().setOwnerBusinessName("Banca Uno");
		avviso.getOptions().get(1).getFull().getIban().setOwnerBusinessName("Banca Due");

		String body = mapper.writeValueAsString(avviso);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andReturn();

		JsonReader reader = Json.createReader(new ByteArrayInputStream(result.getResponse().getContentAsByteArray()));
		JsonObject problem = reader.readObject();
		assertEquals(422, problem.getInt("status"));
		assertTrue(problem.getString("detail").contains("devono risolvere allo stesso intestatario"));
	}
}
