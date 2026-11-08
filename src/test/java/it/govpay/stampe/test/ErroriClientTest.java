package it.govpay.stampe.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayInputStream;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import it.govpay.stampe.Application;
import it.govpay.stampe.test.costanti.Costanti;

/**
 * Errori del client gestiti da Spring MVC: devono rispondere con lo stato corretto
 * (non 500/503) e in formato problem+json come gli altri errori del servizio.
 */
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@DisplayName("Test Errori del Client")
@ActiveProfiles("test")
class ErroriClientTest {

	@Autowired
	private MockMvc mockMvc;

	private static JsonObject problem(MvcResult result) {
		JsonReader reader = Json.createReader(new ByteArrayInputStream(result.getResponse().getContentAsByteArray()));
		return reader.readObject();
	}

	@Test
	void pathInesistente_404() throws Exception {
		MvcResult result = this.mockMvc.perform(get(Costanti.API_BASE_PATH + "/inesistente"))
				.andExpect(status().isNotFound())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
				.andReturn();

		JsonObject problem = problem(result);
		assertEquals(404, problem.getInt("status"));
		assertEquals("Not Found", problem.getString("title"));
		assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found", problem.getString("type"));
	}

	@Test
	void metodoNonSupportato_405() throws Exception {
		MvcResult result = this.mockMvc.perform(get(Costanti.STANDARD_PATH))
				.andExpect(status().isMethodNotAllowed())
				.andExpect(header().string("Allow", "POST"))
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
				.andReturn();

		JsonObject problem = problem(result);
		assertEquals(405, problem.getInt("status"));
		assertEquals("Method Not Allowed", problem.getString("title"));
		assertEquals("https://www.rfc-editor.org/rfc/rfc9110.html#name-405-method-not-allowed", problem.getString("type"));

		this.mockMvc.perform(delete(Costanti.RECEIPT_PATH))
				.andExpect(status().isMethodNotAllowed());
	}

	@Test
	void mediaTypeNonSupportato_415() throws Exception {
		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
				.content("x")
				.contentType(MediaType.TEXT_PLAIN))
				.andExpect(status().isUnsupportedMediaType())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
				.andReturn();

		JsonObject problem = problem(result);
		assertEquals(415, problem.getInt("status"));
		assertTrue(problem.getString("detail").contains("Content-Type 'text/plain' is not supported"));
	}
}
