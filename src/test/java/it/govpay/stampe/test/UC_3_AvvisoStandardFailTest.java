package it.govpay.stampe.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayInputStream;

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
import it.govpay.stampe.beans.PaymentNotice;
import it.govpay.stampe.test.costanti.Costanti;
import it.govpay.stampe.test.serializer.ObjectMapperUtils;
import it.govpay.stampe.test.utils.Utils;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@DisplayName("Test Validazione Avvisi Standard")
@ActiveProfiles("test")
class UC_3_AvvisoStandardFailTest {

	@Autowired
	private MockMvc mockMvc;
	
	private ObjectMapper mapper = ObjectMapperUtils.createObjectMapper();

	@Test
	void UC_3_01_AvvisoStandard_NoBody() throws Exception {
		String body = "";

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
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
	void UC_3_02_AvvisoStandard_WrongContentType() throws Exception {
		String body = "{}";

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
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
        assertTrue(problem.getString("detail").contains("Content type 'text/html' not supported"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
		
	}
	
	@Test
	void UC_3_03_AvvisoStandard_MissingLanguage() throws Exception {
		PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeFull();
		avvisoRataUnica.setLanguage(null);
		
		
		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'paymentNotice' on field 'language': rejected value [null]; must not be null"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}
	
	@Test
	void UC_3_04_AvvisoStandard_MissingFirstLogo() throws Exception {
		PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeFull();
		avvisoRataUnica.setFirstLogo(null);
		
		
		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'paymentNotice' on field 'firstLogo': rejected value [null]; must not be null"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}
	
	@Test
	void UC_3_05_AvvisoStandard_MissingCreditor() throws Exception {
		PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeFull();
		avvisoRataUnica.setCreditor(null);
		
		
		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'paymentNotice' on field 'creditor': rejected value [null]; must not be null"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}
	
	@Test
	void UC_3_06_AvvisoStandard_MissingDebtor() throws Exception {
		PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeFull();
		avvisoRataUnica.setDebtor(null);
		
		
		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'paymentNotice' on field 'debtor': rejected value [null]; must not be null"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}
	
	@Test
	void UC_3_09_AvvisoStandard_InvalidLanguage() throws Exception {
		String body = "{\"language\":\"XXX\"}";

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
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
        assertTrue(problem.getString("detail").contains("Cannot construct instance of `it.govpay.stampe.beans.Languages`, problem: Unexpected value 'XXX'\n at [Source: (org.springframework.util.StreamUtils$NonClosingInputStream); line: 1, column: 13] (through reference chain: it.govpay.stampe.beans.PaymentNotice[\"language\"])"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}
	
	@Test
	void UC_3_10_AvvisoStandard_InvalidTitleSize() throws Exception {
		PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeFull();
		avvisoRataUnica.setTitle(Costanti.STRING_256);
		
		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
				.andReturn();

		JsonReader reader = Json.createReader(new ByteArrayInputStream(result.getResponse().getContentAsByteArray()));
        JsonObject problem = reader.readObject();
        assertNotNull(problem.getString("type"));
        assertNotNull(problem.getString("title"));
        assertNotNull(problem.getString("detail"));
        assertEquals(400, problem.getInt("status"));
        assertEquals("Bad Request", problem.getString("title"));
        assertTrue(problem.getString("detail").contains("Field error in object 'paymentNotice' on field 'title': rejected value"));
        assertTrue(problem.getString("detail").contains("size must be between 0 and 70"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));

	}
	
	@Test
	void UC_3_11_AvvisoStandard_MissingCreditorCF() throws Exception {
		PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeFull();
		avvisoRataUnica.getCreditor().setFiscalCode(null);
		
		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'paymentNotice' on field 'creditor.fiscalCode': rejected value [null]; must not be null"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}
	
	@Test
	void UC_3_12_AvvisoStandard_MissingCreditorBusinessName() throws Exception {
		PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeFull();
		avvisoRataUnica.getCreditor().setBusinessName(null);
		
		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'paymentNotice' on field 'creditor.businessName': rejected value [null]; must not be null"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}
	
	@Test
	void UC_3_13_AvvisoStandard_InvalidCreditorCFSize() throws Exception {
		PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeFull();
		avvisoRataUnica.getCreditor().setFiscalCode(Costanti.STRING_256);
		
		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
				.andReturn();

		JsonReader reader = Json.createReader(new ByteArrayInputStream(result.getResponse().getContentAsByteArray()));
        JsonObject problem = reader.readObject();
        assertNotNull(problem.getString("type"));
        assertNotNull(problem.getString("title"));
        assertNotNull(problem.getString("detail"));
        assertEquals(400, problem.getInt("status"));
        assertEquals("Bad Request", problem.getString("title"));
        assertTrue(problem.getString("detail").contains("Field error in object 'paymentNotice' on field 'creditor.fiscalCode': rejected value"));
        assertTrue(problem.getString("detail").contains("size must be between 0 and 16"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));

	}
	
	@Test
	void UC_3_14_AvvisoStandard_InvalidCreditorBusinessNameSize() throws Exception {
		PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeFull();
		avvisoRataUnica.getCreditor().setBusinessName(Costanti.STRING_256);
		
		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
				.andReturn();

		JsonReader reader = Json.createReader(new ByteArrayInputStream(result.getResponse().getContentAsByteArray()));
        JsonObject problem = reader.readObject();
        assertNotNull(problem.getString("type"));
        assertNotNull(problem.getString("title"));
        assertNotNull(problem.getString("detail"));
        assertEquals(400, problem.getInt("status"));
        assertEquals("Bad Request", problem.getString("title"));
        assertTrue(problem.getString("detail").contains("Field error in object 'paymentNotice' on field 'creditor.businessName': rejected value"));
        assertTrue(problem.getString("detail").contains("size must be between 0 and 50"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));

	}
	
	@Test
	void UC_3_15_AvvisoStandard_InvalidCreditorDepartmentNameSize() throws Exception {
		PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeFull();
		avvisoRataUnica.getCreditor().setDepartmentName(Costanti.STRING_256);
		
		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
				.andReturn();

		JsonReader reader = Json.createReader(new ByteArrayInputStream(result.getResponse().getContentAsByteArray()));
        JsonObject problem = reader.readObject();
        assertNotNull(problem.getString("type"));
        assertNotNull(problem.getString("title"));
        assertNotNull(problem.getString("detail"));
        assertEquals(400, problem.getInt("status"));
        assertEquals("Bad Request", problem.getString("title"));
        assertTrue(problem.getString("detail").contains("Field error in object 'paymentNotice' on field 'creditor.departmentName': rejected value"));
        assertTrue(problem.getString("detail").contains("size must be between 0 and 50"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));

	}
	
	@Test
	void UC_3_16_AvvisoStandard_InvalidCreditorInfoLine1Size() throws Exception {
		PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeFull();
		avvisoRataUnica.getCreditor().setInfoLine1(Costanti.STRING_256);
		
		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
				.andReturn();

		JsonReader reader = Json.createReader(new ByteArrayInputStream(result.getResponse().getContentAsByteArray()));
        JsonObject problem = reader.readObject();
        assertNotNull(problem.getString("type"));
        assertNotNull(problem.getString("title"));
        assertNotNull(problem.getString("detail"));
        assertEquals(400, problem.getInt("status"));
        assertEquals("Bad Request", problem.getString("title"));
        assertTrue(problem.getString("detail").contains("Field error in object 'paymentNotice' on field 'creditor.infoLine1': rejected value"));
        assertTrue(problem.getString("detail").contains("size must be between 0 and 50"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));

	}
	
	@Test
	void UC_3_17_AvvisoStandard_InvalidCreditorInfoline2Size() throws Exception {
		PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeFull();
		avvisoRataUnica.getCreditor().setInfoLine2(Costanti.STRING_256);
		
		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
				.andReturn();

		JsonReader reader = Json.createReader(new ByteArrayInputStream(result.getResponse().getContentAsByteArray()));
        JsonObject problem = reader.readObject();
        assertNotNull(problem.getString("type"));
        assertNotNull(problem.getString("title"));
        assertNotNull(problem.getString("detail"));
        assertEquals(400, problem.getInt("status"));
        assertEquals("Bad Request", problem.getString("title"));
        assertTrue(problem.getString("detail").contains("Field error in object 'paymentNotice' on field 'creditor.infoLine2': rejected value"));
        assertTrue(problem.getString("detail").contains("size must be between 0 and 50"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));

	}
	
	@Test
	void UC_3_18_AvvisoStandard_InvalidCreditorCbillPattern() throws Exception {
		PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeFull();
		avvisoRataUnica.getCreditor().setCbillCode("123456");
		
		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
				.andReturn();

		JsonReader reader = Json.createReader(new ByteArrayInputStream(result.getResponse().getContentAsByteArray()));
        JsonObject problem = reader.readObject();
        assertNotNull(problem.getString("type"));
        assertNotNull(problem.getString("title"));
        assertNotNull(problem.getString("detail"));
        assertEquals(400, problem.getInt("status"));
        assertEquals("Bad Request", problem.getString("title"));
        assertTrue(problem.getString("detail").contains("Field error in object 'paymentNotice' on field 'creditor.cbillCode': rejected value"));
        assertTrue(problem.getString("detail").contains("rejected value [123456]; must match \"[A-Z0-9]{5}\""));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));

	}
	
	@Test
	void UC_3_19_AvvisoStandard_MissingDebtorCF() throws Exception {
		PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeFull();
		avvisoRataUnica.getDebtor().setFiscalCode(null);
		
		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'paymentNotice' on field 'debtor.fiscalCode': rejected value [null]; must not be null"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}
	
	@Test
	void UC_3_20_AvvisoStandard_MissingDebtorFullname() throws Exception {
		PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeFull();
		avvisoRataUnica.getDebtor().setFullName(null);
		
		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'paymentNotice' on field 'debtor.fullName': rejected value [null]; must not be null"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}
	
	@Test
	void UC_3_21_AvvisoStandard_InvalidDebtorCFSize() throws Exception {
		PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeFull();
		avvisoRataUnica.getDebtor().setFiscalCode(Costanti.STRING_256);
		
		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
				.andReturn();

		JsonReader reader = Json.createReader(new ByteArrayInputStream(result.getResponse().getContentAsByteArray()));
        JsonObject problem = reader.readObject();
        assertNotNull(problem.getString("type"));
        assertNotNull(problem.getString("title"));
        assertNotNull(problem.getString("detail"));
        assertEquals(400, problem.getInt("status"));
        assertEquals("Bad Request", problem.getString("title"));
        assertTrue(problem.getString("detail").contains("Field error in object 'paymentNotice' on field 'debtor.fiscalCode': rejected value"));
        assertTrue(problem.getString("detail").contains("size must be between 0 and 16"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));

	}
	
	@Test
	void UC_3_22_AvvisoStandard_InvalidDebtorFullnameSize() throws Exception {
		PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeFull();
		avvisoRataUnica.getDebtor().setFullName(Costanti.STRING_256);
		
		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
				.andReturn();

		JsonReader reader = Json.createReader(new ByteArrayInputStream(result.getResponse().getContentAsByteArray()));
        JsonObject problem = reader.readObject();
        assertNotNull(problem.getString("type"));
        assertNotNull(problem.getString("title"));
        assertNotNull(problem.getString("detail"));
        assertEquals(400, problem.getInt("status"));
        assertEquals("Bad Request", problem.getString("title"));
        assertTrue(problem.getString("detail").contains("Field error in object 'paymentNotice' on field 'debtor.fullName': rejected value"));
        assertTrue(problem.getString("detail").contains("size must be between 0 and 70"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));

	}
	
	@Test
	void UC_3_23_AvvisoStandard_InvalidDebtorAddress1Size() throws Exception {
		PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeFull();
		avvisoRataUnica.getDebtor().setAddressLine1(Costanti.STRING_256);
		
		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
				.andReturn();

		JsonReader reader = Json.createReader(new ByteArrayInputStream(result.getResponse().getContentAsByteArray()));
        JsonObject problem = reader.readObject();
        assertNotNull(problem.getString("type"));
        assertNotNull(problem.getString("title"));
        assertNotNull(problem.getString("detail"));
        assertEquals(400, problem.getInt("status"));
        assertEquals("Bad Request", problem.getString("title"));
        assertTrue(problem.getString("detail").contains("Field error in object 'paymentNotice' on field 'debtor.addressLine1': rejected value"));
        assertTrue(problem.getString("detail").contains("size must be between 0 and 70"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));

	}
	
	@Test
	void UC_3_24_AvvisoStandard_InvalidDebtorAddress2Size() throws Exception {
		PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeFull();
		avvisoRataUnica.getDebtor().setAddressLine2(Costanti.STRING_256);
		
		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
				.andReturn();

		JsonReader reader = Json.createReader(new ByteArrayInputStream(result.getResponse().getContentAsByteArray()));
        JsonObject problem = reader.readObject();
        assertNotNull(problem.getString("type"));
        assertNotNull(problem.getString("title"));
        assertNotNull(problem.getString("detail"));
        assertEquals(400, problem.getInt("status"));
        assertEquals("Bad Request", problem.getString("title"));
        assertTrue(problem.getString("detail").contains("Field error in object 'paymentNotice' on field 'debtor.addressLine2': rejected value"));
        assertTrue(problem.getString("detail").contains("size must be between 0 and 70"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));

	}
	
	@Test
	void UC_3_25_AvvisoStandard_MissingFullAmount() throws Exception {
		PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeFull();
		avvisoRataUnica.getFull().setAmount(null);
		
		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'paymentNotice' on field 'full.amount': rejected value [null]; must not be null"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}
	
	@Test
	void UC_3_26_AvvisoStandard_MissingFullNoticeNumber() throws Exception {
		PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeFull();
		avvisoRataUnica.getFull().setNoticeNumber(null);
		
		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'paymentNotice' on field 'full.noticeNumber': rejected value [null]; must not be null"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}
	
	@Test
	void UC_3_27_AvvisoStandard_MissingFullQrcode() throws Exception {
		PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeFull();
		avvisoRataUnica.getFull().setQrcode(null);
		
		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'paymentNotice' on field 'full.qrcode': rejected value [null]; must not be null"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}
	
	@Test
	void UC_3_28_AvvisoStandard_InvalidFullNoticeNumberPattern() throws Exception {
		PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeFull();
		avvisoRataUnica.getFull().setNoticeNumber(Costanti.STRING_256);
		
		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
				.andReturn();

		JsonReader reader = Json.createReader(new ByteArrayInputStream(result.getResponse().getContentAsByteArray()));
        JsonObject problem = reader.readObject();
        assertNotNull(problem.getString("type"));
        assertNotNull(problem.getString("title"));
        assertNotNull(problem.getString("detail"));
        assertEquals(400, problem.getInt("status"));
        assertEquals("Bad Request", problem.getString("title"));
        assertTrue(problem.getString("detail").contains("Field error in object 'paymentNotice' on field 'full.noticeNumber': rejected value"));
        assertTrue(problem.getString("detail").contains("must match \"[0-9]{18}\""));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));

	}
	
	@Test
	void UC_3_29_AvvisoStandard_InvalidFullAmount() throws Exception {
		PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeFull();
		avvisoRataUnica.getFull().setAmount(Double.valueOf(0d));	
		
		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
				.andReturn();

		JsonReader reader = Json.createReader(new ByteArrayInputStream(result.getResponse().getContentAsByteArray()));
        JsonObject problem = reader.readObject();
        assertNotNull(problem.getString("type"));
        assertNotNull(problem.getString("title"));
        assertNotNull(problem.getString("detail"));
        assertEquals(400, problem.getInt("status"));
        assertEquals("Bad Request", problem.getString("title"));
        assertTrue(problem.getString("detail").contains("Field error in object 'paymentNotice' on field 'full.amount': rejected value"));
        assertTrue(problem.getString("detail").contains("rejected value [0.0]; must be greater than or equal to 0.01"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));

	}
	
	@Test
	void UC_3_30_AvvisoStandard_MissingSecondLanguageLanguage() throws Exception {
		PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeFull();
		avvisoRataUnica.getSecondLanguage().setLanguage(null);
		
		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'paymentNotice' on field 'secondLanguage.language': rejected value [null]; must not be null"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}
	
	@Test
	void UC_3_31_AvvisoStandard_MissingSecondLanguageTitle() throws Exception {
		PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeFull();
		avvisoRataUnica.getSecondLanguage().setTitle(null);
		
		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'paymentNotice' on field 'secondLanguage.title': rejected value [null]; must not be null"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}
	
	@Test
	void UC_3_32_AvvisoStandard_MissingSecondLanguageBilinguism() throws Exception {
		PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeFull();
		avvisoRataUnica.getSecondLanguage().setBilinguism(null);
		
		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'paymentNotice' on field 'secondLanguage.bilinguism': rejected value [null]; must not be null"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}
	
	@Test
	void UC_3_33_AvvisoStandard_InvalidSecondLanguageTitleSize() throws Exception {
		PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeFull();
		avvisoRataUnica.getSecondLanguage().setTitle(Costanti.STRING_256);
		
		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
				.andReturn();

		JsonReader reader = Json.createReader(new ByteArrayInputStream(result.getResponse().getContentAsByteArray()));
        JsonObject problem = reader.readObject();
        assertNotNull(problem.getString("type"));
        assertNotNull(problem.getString("title"));
        assertNotNull(problem.getString("detail"));
        assertEquals(400, problem.getInt("status"));
        assertEquals("Bad Request", problem.getString("title"));
        assertTrue(problem.getString("detail").contains("Field error in object 'paymentNotice' on field 'secondLanguage.title': rejected value"));
        assertTrue(problem.getString("detail").contains("size must be between 0 and 70"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));

	}
	
	@Test
	void UC_3_34_AvvisoStandard_MissingPostal() throws Exception {
		PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeFull();
		avvisoRataUnica.setPostal(null);
		
		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'paymentNotice' on field 'postal': rejected value [null]; must not be null"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}
	
	@Test
	void UC_3_35_AvvisoStandard_MissingTitle() throws Exception {
		PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeFull();
		avvisoRataUnica.setTitle(null);
		
		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'paymentNotice' on field 'title': rejected value [null]; must not be null"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}
	
	@Test
	void UC_3_36_AvvisoStandard_MissingPostalRequired() throws Exception {
		PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeFull();
		avvisoRataUnica.setPostal(null);
		
		String body = mapper.writeValueAsString(avvisoRataUnica);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'paymentNotice' on field 'postal': rejected value [null]; must not be null"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}
	
	@Test
	void UC_3_37_AvvisoStandard_InvalidFullAmountIbanAndPostal() throws Exception {
	    PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeFull();
	    avvisoRataUnica.setPostal(true);
	    avvisoRataUnica.getFull().setIbanCode(null);
	
	    String body = mapper.writeValueAsString(avvisoRataUnica);
	
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
	    assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-422-unprocessable-content", problem.getString("type"));
	}
	
	@Test
	void UC_3_38_AvvisoStandard_InvalidInstalmentIbanAndPostal() throws Exception {
	    PaymentNotice avvisoRataUnica = Utils.creaPaymentNoticeConRate(1,false);
	    avvisoRataUnica.setPostal(true);
	    avvisoRataUnica.getInstalments().get(0).setIbanCode(null);
	
	    String body = mapper.writeValueAsString(avvisoRataUnica);
	
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
	    assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-422-unprocessable-content", problem.getString("type"));
	}
}
