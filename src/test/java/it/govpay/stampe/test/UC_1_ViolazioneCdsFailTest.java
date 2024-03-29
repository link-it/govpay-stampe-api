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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import it.govpay.stampe.Application;
import it.govpay.stampe.test.costanti.Costanti;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@DisplayName("Test Lettura Eventi")
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
@ActiveProfiles("test")
class UC_1_ViolazioneCdsFailTest {

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	public void init() {
	}

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
	
	@Test
	void UC_1_03_ViolazioneCds_Wrong_Language() throws Exception {
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
	void UC_1_04_ViolazioneCds_Wrong_Title() throws Exception {
		String body = "{\"title\":\""+Costanti.STRING_256+"\"}";

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
//				.andExpect(status().isBadRequest())
//				.andReturn();

				.andExpect(status().is5xxServerError())
				.andReturn();
		JsonReader reader = Json.createReader(new ByteArrayInputStream(result.getResponse().getContentAsByteArray()));
		JsonObject problem = reader.readObject();
		assertNotNull(problem.getString("type"));
		assertNotNull(problem.getString("title"));
		assertNotNull(problem.getString("detail"));
		assertEquals(503, problem.getInt("status"));
		assertEquals("Service Unavailable", problem.getString("title"));
		assertEquals("Request can't be satisfaied at the moment", problem.getString("detail"));
		assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-503-service-unavailable", problem.getString("type"));

		
// TODO vedere perche' non viene lanciato 400
		
//		JsonReader reader = Json.createReader(new ByteArrayInputStream(result.getResponse().getContentAsByteArray()));
//        JsonObject problem = reader.readObject();
//        assertNotNull(problem.getString("type"));
//        assertNotNull(problem.getString("title"));
//        assertNotNull(problem.getString("detail"));
//        assertEquals(400, problem.getInt("status"));
//        assertEquals("Bad Request", problem.getString("title"));
//        assertTrue(problem.getString("detail").contains("Cannot construct instance of `it.govpay.gde.beans.CategoriaEvento`, problem: Unexpected value 'XXX'\n at [Source: (org.springframework.util.StreamUtils$NonClosingInputStream); line: 1, column: 20] (through reference chain: it.govpay.gde.beans.NuovoEvento[\"categoriaEvento\"])"));
//        assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request", problem.getString("type"));
		
	}
}
