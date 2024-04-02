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
import it.govpay.stampe.beans.CdsViolation;
import it.govpay.stampe.test.costanti.Costanti;
import it.govpay.stampe.test.serializer.ObjectMapperUtils;
import it.govpay.stampe.test.utils.Utils;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@DisplayName("Test Validazione avvisi Violazione CDS")
@ActiveProfiles("test")
class UC_1_ViolazioneCdsFailTest {

	@Autowired
	private MockMvc mockMvc;
	
	private ObjectMapper mapper = ObjectMapperUtils.createObjectMapper();

	@Test
	void UC_1_01_ViolazioneCds_NoBody() throws Exception {
		String body = "";

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
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
	void UC_1_02_ViolazioneCds_WrongContentType() throws Exception {
		String body = "{}";

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
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
	
	// campi not null 
	// language firstLogo creditor debtor discountedAmount reducedAmount
	
	@Test
	void UC_1_03_ViolazioneCds_MissingLanguage() throws Exception {
		CdsViolation cdsViolation = Utils.creaCdsViolation();
		cdsViolation.setLanguage(null);
		
		
		String body = mapper.writeValueAsString(cdsViolation);

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'cdsViolation' on field 'language': rejected value [null]; must not be null"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}
	
	@Test
	void UC_1_04_ViolazioneCds_MissingFirstLogo() throws Exception {
		CdsViolation cdsViolation = Utils.creaCdsViolation();
		cdsViolation.setFirstLogo(null);
		
		
		String body = mapper.writeValueAsString(cdsViolation);

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'cdsViolation' on field 'firstLogo': rejected value [null]; must not be null"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}
	
	@Test
	void UC_1_05_ViolazioneCds_MissingCreditor() throws Exception {
		CdsViolation cdsViolation = Utils.creaCdsViolation();
		cdsViolation.setCreditor(null);
		
		
		String body = mapper.writeValueAsString(cdsViolation);

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'cdsViolation' on field 'creditor': rejected value [null]; must not be null"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}
	
	@Test
	void UC_1_06_ViolazioneCds_MissingDebtor() throws Exception {
		CdsViolation cdsViolation = Utils.creaCdsViolation();
		cdsViolation.setDebtor(null);
		
		
		String body = mapper.writeValueAsString(cdsViolation);

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'cdsViolation' on field 'debtor': rejected value [null]; must not be null"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}
	
	@Test
	void UC_1_07_ViolazioneCds_MissingDiscountedAmount() throws Exception {
		CdsViolation cdsViolation = Utils.creaCdsViolation();
		cdsViolation.setDiscountedAmount(null);
		
		String body = mapper.writeValueAsString(cdsViolation);

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'cdsViolation' on field 'discountedAmount': rejected value [null]; must not be null"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}
	
	@Test
	void UC_1_08_ViolazioneCds_MissingReducedAmount() throws Exception {
		CdsViolation cdsViolation = Utils.creaCdsViolation();
		cdsViolation.setReducedAmount(null);
		
		String body = mapper.writeValueAsString(cdsViolation);

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'cdsViolation' on field 'reducedAmount': rejected value [null]; must not be null"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}
	
	@Test
	void UC_1_09_ViolazioneCds_InvalidLanguage() throws Exception {
		String body = "{\"language\":\"XXX\"}";

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
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
        assertTrue(problem.getString("detail").contains("Cannot construct instance of `it.govpay.stampe.beans.Languages`, problem: Unexpected value 'XXX'\n at [Source: (org.springframework.util.StreamUtils$NonClosingInputStream); line: 1, column: 13] (through reference chain: it.govpay.stampe.beans.CdsViolation[\"language\"])"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}
	
	@Test
	void UC_1_10_ViolazioneCds_InvalidTitleSize() throws Exception {
		CdsViolation cdsViolation = Utils.creaCdsViolation();
		cdsViolation.setTitle(Costanti.STRING_256);
		
		String body = mapper.writeValueAsString(cdsViolation);

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'cdsViolation' on field 'title': rejected value"));
        assertTrue(problem.getString("detail").contains("size must be between 0 and 70"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));

	}
	
	@Test
	void UC_1_11_ViolazioneCds_MissingCreditorCF() throws Exception {
		CdsViolation cdsViolation = Utils.creaCdsViolation();
		cdsViolation.getCreditor().setFiscalCode(null);
		
		String body = mapper.writeValueAsString(cdsViolation);

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'cdsViolation' on field 'creditor.fiscalCode': rejected value [null]; must not be null"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}
	
	@Test
	void UC_1_12_ViolazioneCds_MissingCreditorBusinessName() throws Exception {
		CdsViolation cdsViolation = Utils.creaCdsViolation();
		cdsViolation.getCreditor().setBusinessName(null);
		
		String body = mapper.writeValueAsString(cdsViolation);

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'cdsViolation' on field 'creditor.businessName': rejected value [null]; must not be null"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}
	
	@Test
	void UC_1_13_ViolazioneCds_InvalidCreditorCFSize() throws Exception {
		CdsViolation cdsViolation = Utils.creaCdsViolation();
		cdsViolation.getCreditor().setFiscalCode(Costanti.STRING_256);
		
		String body = mapper.writeValueAsString(cdsViolation);

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'cdsViolation' on field 'creditor.fiscalCode': rejected value"));
        assertTrue(problem.getString("detail").contains("size must be between 0 and 16"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));

	}
	
	@Test
	void UC_1_14_ViolazioneCds_InvalidCreditorBusinessNameSize() throws Exception {
		CdsViolation cdsViolation = Utils.creaCdsViolation();
		cdsViolation.getCreditor().setBusinessName(Costanti.STRING_256);
		
		String body = mapper.writeValueAsString(cdsViolation);

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'cdsViolation' on field 'creditor.businessName': rejected value"));
        assertTrue(problem.getString("detail").contains("size must be between 0 and 50"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));

	}
	
	@Test
	void UC_1_15_ViolazioneCds_InvalidCreditorDepartmentNameSize() throws Exception {
		CdsViolation cdsViolation = Utils.creaCdsViolation();
		cdsViolation.getCreditor().setDepartmentName(Costanti.STRING_256);
		
		String body = mapper.writeValueAsString(cdsViolation);

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'cdsViolation' on field 'creditor.departmentName': rejected value"));
        assertTrue(problem.getString("detail").contains("size must be between 0 and 50"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));

	}
	
	@Test
	void UC_1_16_ViolazioneCds_InvalidCreditorInfoLine1Size() throws Exception {
		CdsViolation cdsViolation = Utils.creaCdsViolation();
		cdsViolation.getCreditor().setInfoLine1(Costanti.STRING_256);
		
		String body = mapper.writeValueAsString(cdsViolation);

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'cdsViolation' on field 'creditor.infoLine1': rejected value"));
        assertTrue(problem.getString("detail").contains("size must be between 0 and 50"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));

	}
	
	@Test
	void UC_1_17_ViolazioneCds_InvalidCreditorInfoline2Size() throws Exception {
		CdsViolation cdsViolation = Utils.creaCdsViolation();
		cdsViolation.getCreditor().setInfoLine2(Costanti.STRING_256);
		
		String body = mapper.writeValueAsString(cdsViolation);

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'cdsViolation' on field 'creditor.infoLine2': rejected value"));
        assertTrue(problem.getString("detail").contains("size must be between 0 and 50"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));

	}
	
	@Test
	void UC_1_18_ViolazioneCds_InvalidCreditorCbillPattern() throws Exception {
		CdsViolation cdsViolation = Utils.creaCdsViolation();
		cdsViolation.getCreditor().setCbillCode("123456");
		
		String body = mapper.writeValueAsString(cdsViolation);

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'cdsViolation' on field 'creditor.cbillCode': rejected value"));
        assertTrue(problem.getString("detail").contains("rejected value [123456]; must match \"[A-Z0-9]{5}\""));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));

	}
	
	@Test
	void UC_1_19_ViolazioneCds_MissingDebtorCF() throws Exception {
		CdsViolation cdsViolation = Utils.creaCdsViolation();
		cdsViolation.getDebtor().setFiscalCode(null);
		
		String body = mapper.writeValueAsString(cdsViolation);

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'cdsViolation' on field 'debtor.fiscalCode': rejected value [null]; must not be null"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}
	
	@Test
	void UC_1_20_ViolazioneCds_MissingDebtorFullname() throws Exception {
		CdsViolation cdsViolation = Utils.creaCdsViolation();
		cdsViolation.getDebtor().setFullName(null);
		
		String body = mapper.writeValueAsString(cdsViolation);

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'cdsViolation' on field 'debtor.fullName': rejected value [null]; must not be null"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}
	
	@Test
	void UC_1_21_ViolazioneCds_InvalidDebtorCFSize() throws Exception {
		CdsViolation cdsViolation = Utils.creaCdsViolation();
		cdsViolation.getDebtor().setFiscalCode(Costanti.STRING_256);
		
		String body = mapper.writeValueAsString(cdsViolation);

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'cdsViolation' on field 'debtor.fiscalCode': rejected value"));
        assertTrue(problem.getString("detail").contains("size must be between 0 and 16"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));

	}
	
	@Test
	void UC_1_22_ViolazioneCds_InvalidDebtorFullnameSize() throws Exception {
		CdsViolation cdsViolation = Utils.creaCdsViolation();
		cdsViolation.getDebtor().setFullName(Costanti.STRING_256);
		
		String body = mapper.writeValueAsString(cdsViolation);

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'cdsViolation' on field 'debtor.fullName': rejected value"));
        assertTrue(problem.getString("detail").contains("size must be between 0 and 70"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));

	}
	
	@Test
	void UC_1_23_ViolazioneCds_InvalidDebtorAddress1Size() throws Exception {
		CdsViolation cdsViolation = Utils.creaCdsViolation();
		cdsViolation.getDebtor().setAddressLine1(Costanti.STRING_256);
		
		String body = mapper.writeValueAsString(cdsViolation);

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'cdsViolation' on field 'debtor.addressLine1': rejected value"));
        assertTrue(problem.getString("detail").contains("size must be between 0 and 70"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));

	}
	
	@Test
	void UC_1_24_ViolazioneCds_InvalidDebtorAddress2Size() throws Exception {
		CdsViolation cdsViolation = Utils.creaCdsViolation();
		cdsViolation.getDebtor().setAddressLine2(Costanti.STRING_256);
		
		String body = mapper.writeValueAsString(cdsViolation);

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'cdsViolation' on field 'debtor.addressLine2': rejected value"));
        assertTrue(problem.getString("detail").contains("size must be between 0 and 70"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));

	}
	
	@Test
	void UC_1_25_ViolazioneCds_MissingDiscountedAmountAmount() throws Exception {
		CdsViolation cdsViolation = Utils.creaCdsViolation();
		cdsViolation.getDiscountedAmount().setAmount(null);
		
		String body = mapper.writeValueAsString(cdsViolation);

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'cdsViolation' on field 'discountedAmount.amount': rejected value [null]; must not be null"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}
	
	@Test
	void UC_1_26_ViolazioneCds_MissingDiscountedAmountNoticeNumber() throws Exception {
		CdsViolation cdsViolation = Utils.creaCdsViolation();
		cdsViolation.getDiscountedAmount().setNoticeNumber(null);
		
		String body = mapper.writeValueAsString(cdsViolation);

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'cdsViolation' on field 'discountedAmount.noticeNumber': rejected value [null]; must not be null"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}
	
	@Test
	void UC_1_27_ViolazioneCds_MissingDiscountedAmountQrcode() throws Exception {
		CdsViolation cdsViolation = Utils.creaCdsViolation();
		cdsViolation.getDiscountedAmount().setQrcode(null);
		
		String body = mapper.writeValueAsString(cdsViolation);

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'cdsViolation' on field 'discountedAmount.qrcode': rejected value [null]; must not be null"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}
	
	@Test
	void UC_1_28_ViolazioneCds_InvalidDiscountedAmountNoticeNumberPattern() throws Exception {
		CdsViolation cdsViolation = Utils.creaCdsViolation();
		cdsViolation.getDiscountedAmount().setNoticeNumber(Costanti.STRING_256);
		
		String body = mapper.writeValueAsString(cdsViolation);

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'cdsViolation' on field 'discountedAmount.noticeNumber': rejected value"));
        assertTrue(problem.getString("detail").contains("must match \"[0-9]{18}\""));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));

	}
	
	@Test
	void UC_1_29_ViolazioneCds_InvalidDiscountedAmountAmount() throws Exception {
		CdsViolation cdsViolation = Utils.creaCdsViolation();
		cdsViolation.getDiscountedAmount().setAmount(Double.valueOf(0d));	
		
		String body = mapper.writeValueAsString(cdsViolation);

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'cdsViolation' on field 'discountedAmount.amount': rejected value"));
        assertTrue(problem.getString("detail").contains("rejected value [0.0]; must be greater than or equal to 0.01"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));

	}
	
	@Test
	void UC_1_30_ViolazioneCds_MissingReducedAmountAmount() throws Exception {
		CdsViolation cdsViolation = Utils.creaCdsViolation();
		cdsViolation.getReducedAmount().setAmount(null);
		
		String body = mapper.writeValueAsString(cdsViolation);

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'cdsViolation' on field 'reducedAmount.amount': rejected value [null]; must not be null"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}
	
	@Test
	void UC_1_31_ViolazioneCds_MissingReducedAmountNoticeNumber() throws Exception {
		CdsViolation cdsViolation = Utils.creaCdsViolation();
		cdsViolation.getReducedAmount().setNoticeNumber(null);
		
		String body = mapper.writeValueAsString(cdsViolation);

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'cdsViolation' on field 'reducedAmount.noticeNumber': rejected value [null]; must not be null"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}
	
	@Test
	void UC_1_32_ViolazioneCds_MissingReducedAmountQrcode() throws Exception {
		CdsViolation cdsViolation = Utils.creaCdsViolation();
		cdsViolation.getReducedAmount().setQrcode(null);
		
		String body = mapper.writeValueAsString(cdsViolation);

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'cdsViolation' on field 'reducedAmount.qrcode': rejected value [null]; must not be null"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
	}
	
	@Test
	void UC_1_33_ViolazioneCds_InvalidReducedAmountNoticeNumberPattern() throws Exception {
		CdsViolation cdsViolation = Utils.creaCdsViolation();
		cdsViolation.getReducedAmount().setNoticeNumber(Costanti.STRING_256);
		
		String body = mapper.writeValueAsString(cdsViolation);

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'cdsViolation' on field 'reducedAmount.noticeNumber': rejected value"));
        assertTrue(problem.getString("detail").contains("must match \"[0-9]{18}\""));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));

	}
	
	@Test
	void UC_1_34_ViolazioneCds_InvalidReducedAmountAmount() throws Exception {
		CdsViolation cdsViolation = Utils.creaCdsViolation();
		cdsViolation.getReducedAmount().setAmount(Double.valueOf(0d));	
		
		String body = mapper.writeValueAsString(cdsViolation);

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
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
        assertTrue(problem.getString("detail").contains("Field error in object 'cdsViolation' on field 'reducedAmount.amount': rejected value"));
        assertTrue(problem.getString("detail").contains("rejected value [0.0]; must be greater than or equal to 0.01"));
        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));

	}
}
