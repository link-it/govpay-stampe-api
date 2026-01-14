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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.govpay.stampe.Application;
import it.govpay.stampe.beans.Payer;
import it.govpay.stampe.beans.Receipt;
import it.govpay.stampe.beans.ReceiptItem;
import it.govpay.stampe.beans.ReceiptItemStatus;
import it.govpay.stampe.beans.ReceiptOrganization;
import it.govpay.stampe.beans.ReceiptStatus;
import it.govpay.stampe.beans.ReceiptVersion;
import it.govpay.stampe.test.costanti.Costanti;
import it.govpay.stampe.test.serializer.ObjectMapperUtils;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@DisplayName("Test Validazione Ricevute di Pagamento")
@ActiveProfiles("test")
class UC_7_RicevutaFailTest {

	@Autowired
	private MockMvc mockMvc;

	private ObjectMapper mapper = ObjectMapperUtils.createObjectMapper();

	@Value("${stampe.logoEnte}")
	String logoEnte;

	// ==================== Test generici ====================

	@Test
	void UC_7_01_Receipt_NoBody() throws Exception {
		String body = "";

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		JsonReader reader = Json.createReader(new ByteArrayInputStream(result.getResponse().getContentAsByteArray()));
		JsonObject problem = reader.readObject();
		assertNotNull(problem.getString("type"));
		assertNotNull(problem.getString("title"));
		assertNotNull(problem.getString("detail"));
		assertEquals(400, problem.getInt("status"));
		assertEquals("Bad Request", problem.getString("title"));
		assertTrue(problem.getString("detail").contains("Required request body is missing"));
		assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}

	@Test
	void UC_7_02_Receipt_WrongContentType() throws Exception {
		String body = "{}";

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.TEXT_HTML))
				.andExpect(status().isBadRequest())
				.andReturn();

		JsonReader reader = Json.createReader(new ByteArrayInputStream(result.getResponse().getContentAsByteArray()));
		JsonObject problem = reader.readObject();
		assertNotNull(problem.getString("type"));
		assertNotNull(problem.getString("title"));
		assertNotNull(problem.getString("detail"));
		assertEquals(400, problem.getInt("status"));
		assertEquals("Bad Request", problem.getString("title"));
		assertTrue(problem.getString("detail").contains("Content-Type 'text/html' is not supported"));
		assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}

	// ==================== Test campi required Receipt ====================

	@Test
	void UC_7_03_Receipt_MissingCreditorLogo() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.setCreditorLogo(null);

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "creditorLogo");
	}

	@Test
	void UC_7_04_Receipt_MissingPagopaLogo() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.setPagopaLogo(null);

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "pagopaLogo");
	}

	@Test
	void UC_7_05_Receipt_MissingPaymentSubject() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.setPaymentSubject(null);

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "paymentSubject");
	}

	@Test
	void UC_7_06_Receipt_MissingOrganization() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.setOrganization(null);

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "organization");
	}

	@Test
	void UC_7_07_Receipt_MissingPayer() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.setPayer(null);

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "payer");
	}

	@Test
	void UC_7_08_Receipt_MissingPsp() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.setPsp(null);

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "psp");
	}

	@Test
	void UC_7_09_Receipt_MissingAmount() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.setAmount(null);

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "amount");
	}

	@Test
	void UC_7_10_Receipt_MissingOperationDate() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.setOperationDate(null);

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "operationDate");
	}

	@Test
	void UC_7_11_Receipt_MissingApplicationDate() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.setApplicationDate(null);

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "applicationDate");
	}

	@Test
	void UC_7_12_Receipt_MissingStatus() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.setStatus(null);

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "status");
	}

	@Test
	void UC_7_13_Receipt_MissingCreditorReferenceId() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.setCreditorReferenceId(null);

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "creditorReferenceId");
	}

	@Test
	void UC_7_14_Receipt_MissingReceiptId() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.setReceiptId(null);

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "receiptId");
	}

	@Test
	void UC_7_15_Receipt_MissingObjectVersion() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.setObjectVersion(null);

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "objectVersion");
	}

	@Test
	void UC_7_16_Receipt_MissingItems() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.setItems(null);

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "items");
	}

	@Test
	void UC_7_17_Receipt_EmptyItems() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.setItems(new ArrayList<>());

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "items");
	}

	// ==================== Test campi required Organization ====================

	@Test
	void UC_7_18_Receipt_MissingOrganizationFiscalCode() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.getOrganization().setFiscalCode(null);

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "organization.fiscalCode");
	}

	@Test
	void UC_7_19_Receipt_MissingOrganizationBusinessName() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.getOrganization().setBusinessName(null);

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "organization.businessName");
	}

	@Test
	void UC_7_20_Receipt_MissingOrganizationAddress() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.getOrganization().setAddress(null);

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "organization.address");
	}

	@Test
	void UC_7_21_Receipt_MissingOrganizationLocation() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.getOrganization().setLocation(null);

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "organization.location");
	}

	// ==================== Test campi required Payer ====================

	@Test
	void UC_7_22_Receipt_MissingPayerFiscalCode() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.getPayer().setFiscalCode(null);

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "payer.fiscalCode");
	}

	@Test
	void UC_7_23_Receipt_MissingPayerFullName() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.getPayer().setFullName(null);

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "payer.fullName");
	}

	@Test
	void UC_7_24_Receipt_MissingPayerAddress() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.getPayer().setAddress(null);

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "payer.address");
	}

	@Test
	void UC_7_25_Receipt_MissingPayerLocation() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.getPayer().setLocation(null);

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "payer.location");
	}

	// ==================== Test campi required ReceiptItem ====================

	@Test
	void UC_7_26_Receipt_MissingItemDescription() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.getItems().get(0).setDescription(null);

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "items[0].description");
	}

	@Test
	void UC_7_27_Receipt_MissingItemIur() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.getItems().get(0).setIur(null);

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "items[0].iur");
	}

	@Test
	void UC_7_28_Receipt_MissingItemAmount() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.getItems().get(0).setAmount(null);

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "items[0].amount");
	}

	@Test
	void UC_7_29_Receipt_MissingItemStatus() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.getItems().get(0).setStatus(null);

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "items[0].status");
	}

	// ==================== Test valori invalidi ====================

	@Test
	void UC_7_30_Receipt_InvalidAmount() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.setAmount(0.001);

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "amount");
	}

	@Test
	void UC_7_31_Receipt_InvalidItemAmount() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.getItems().get(0).setAmount(0.001);

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "items[0].amount");
	}

	@Test
	void UC_7_32_Receipt_InvalidPaymentSubjectSize() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.setPaymentSubject("A".repeat(141));

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "paymentSubject");
	}

	@Test
	void UC_7_33_Receipt_InvalidPspSize() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.setPsp("A".repeat(71));

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "psp");
	}

	@Test
	void UC_7_34_Receipt_InvalidCreditorReferenceIdSize() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.setCreditorReferenceId("A".repeat(36));

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "creditorReferenceId");
	}

	@Test
	void UC_7_35_Receipt_InvalidReceiptIdSize() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.setReceiptId("A".repeat(36));

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "receiptId");
	}

	@Test
	void UC_7_36_Receipt_InvalidOrganizationFiscalCodeSize() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.getOrganization().setFiscalCode("A".repeat(17));

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "organization.fiscalCode");
	}

	@Test
	void UC_7_37_Receipt_InvalidOrganizationBusinessNameSize() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.getOrganization().setBusinessName("A".repeat(71));

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "organization.businessName");
	}

	@Test
	void UC_7_38_Receipt_InvalidPayerFiscalCodeSize() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.getPayer().setFiscalCode("A".repeat(17));

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "payer.fiscalCode");
	}

	@Test
	void UC_7_39_Receipt_InvalidPayerFullNameSize() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.getPayer().setFullName("A".repeat(71));

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "payer.fullName");
	}

	@Test
	void UC_7_40_Receipt_InvalidItemDescriptionSize() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.getItems().get(0).setDescription("A".repeat(141));

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "items[0].description");
	}

	@Test
	void UC_7_41_Receipt_InvalidItemIurSize() throws Exception {
		Receipt receipt = creaReceipt();
		receipt.getItems().get(0).setIur("A".repeat(36));

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertValidationError(result, "items[0].iur");
	}

	@Test
	void UC_7_42_Receipt_InvalidStatus() throws Exception {
		String body = mapper.writeValueAsString(creaReceipt());
		// Modifica manualmente il JSON per inserire uno status invalido
		body = body.replace("\"status\":\"EXECUTED\"", "\"status\":\"INVALID_STATUS\"");

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		JsonReader reader = Json.createReader(new ByteArrayInputStream(result.getResponse().getContentAsByteArray()));
		JsonObject problem = reader.readObject();
		assertEquals(400, problem.getInt("status"));
	}

	@Test
	void UC_7_43_Receipt_InvalidObjectVersion() throws Exception {
		String body = mapper.writeValueAsString(creaReceipt());
		// Modifica manualmente il JSON per inserire una versione invalida
		body = body.replace("\"object_version\":\"SANP_230\"", "\"object_version\":\"INVALID_VERSION\"");

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		JsonReader reader = Json.createReader(new ByteArrayInputStream(result.getResponse().getContentAsByteArray()));
		JsonObject problem = reader.readObject();
		assertEquals(400, problem.getInt("status"));
	}

	// ==================== Helper methods ====================

	private void assertValidationError(MvcResult result, String fieldName) throws Exception {
		JsonReader reader = Json.createReader(new ByteArrayInputStream(result.getResponse().getContentAsByteArray()));
		JsonObject problem = reader.readObject();
		assertNotNull(problem.getString("type"));
		assertNotNull(problem.getString("title"));
		assertNotNull(problem.getString("detail"));
		assertEquals(400, problem.getInt("status"));
		assertEquals("Bad Request", problem.getString("title"));
		assertTrue(problem.getString("detail").contains(fieldName),
				"Expected field '" + fieldName + "' in error detail: " + problem.getString("detail"));
		assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}

	private Receipt creaReceipt() {
		Receipt receipt = new Receipt();

		// Loghi
		receipt.setCreditorLogo(this.logoEnte);
		receipt.setPagopaLogo(this.logoEnte);

		// Dati ente (organization)
		ReceiptOrganization organization = new ReceiptOrganization();
		organization.setFiscalCode("01234567890");
		organization.setBusinessName("Comune di Test");
		organization.setAddress("Via Roma, 1");
		organization.setLocation("00100 Roma (RM)");
		receipt.setOrganization(organization);

		// Dati soggetto pagatore (payer)
		Payer payer = new Payer();
		payer.setFiscalCode("RSSMRA80A01H501U");
		payer.setFullName("Mario Rossi");
		payer.setAddress("Viale dei Giardini, 00");
		payer.setLocation("00000 Roma (RM)");
		receipt.setPayer(payer);

		// Dati pagamento
		receipt.setPaymentSubject("Pagamento TARI 2024");
		receipt.setPsp("Banca di Test S.p.A.");
		receipt.setAmount(150.00);
		receipt.setOperationDate("15/01/2024 10:30:00");
		receipt.setApplicationDate("15/01/2024");
		receipt.setStatus(ReceiptStatus.EXECUTED);

		// Identificativi
		receipt.setCreditorReferenceId("01234567890123456");
		receipt.setReceiptId("0123456789012345678901234567890");
		receipt.setObjectVersion(ReceiptVersion._230);

		// Voci pagamento (items)
		List<ReceiptItem> items = new ArrayList<>();

		ReceiptItem item1 = new ReceiptItem();
		item1.setDescription("TARI 2024 - Quota fissa");
		item1.setIur("1");
		item1.setAmount(100.00);
		item1.setStatus(ReceiptItemStatus.EXECUTED);
		items.add(item1);

		ReceiptItem item2 = new ReceiptItem();
		item2.setDescription("TARI 2024 - Quota variabile");
		item2.setIur("2");
		item2.setAmount(50.00);
		item2.setStatus(ReceiptItemStatus.EXECUTED);
		items.add(item2);

		receipt.setItems(items);

		return receipt;
	}
}
