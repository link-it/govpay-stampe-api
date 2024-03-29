package it.govpay.stampe.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.TimeZone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import it.govpay.stampe.Application;
import it.govpay.stampe.beans.Amount;
import it.govpay.stampe.beans.CdsViolation;
import it.govpay.stampe.beans.Creditor;
import it.govpay.stampe.beans.Debtor;
import it.govpay.stampe.beans.Languages;
import it.govpay.stampe.test.costanti.Costanti;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@DisplayName("Test Lettura Eventi")
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
@ActiveProfiles("test")
class UC_2_ViolazioneCdsTest {

	@Autowired
	private MockMvc mockMvc;

	private ObjectMapper mapper;

	@BeforeEach
	public void init() {
		SimpleDateFormat sdf = new SimpleDateFormat(Costanti.PATTERN_DATA_JSON_YYYY_MM_DD_T_HH_MM_SS);
		sdf.setTimeZone(TimeZone.getTimeZone("Europe/Rome"));
		sdf.setLenient(false);
		
		mapper = JsonMapper.builder().build();
		mapper.registerModule(new JavaTimeModule());
		mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
		mapper.enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID); 
		mapper.setDateFormat(sdf);
	}

	@Test
	void UC_2_01_ViolazioneCdsOk() throws Exception {
		CdsViolation cdsViolation = new CdsViolation();
		cdsViolation.setLanguage(Languages.IT);
		Creditor creditor = new Creditor();
		creditor.setCbillCode("ABC12");
		creditor.setFiscalCode(Costanti.ID_DOMINIO_1);
		creditor.setBusinessName("Ente Creditore Test");
		cdsViolation.setCreditor(creditor);
		
		Debtor debtor = new Debtor();
		debtor.setFiscalCode("RSSMRA50A01A110X");
		debtor.setFullName("Mario Rossi");
		cdsViolation.setDebtor(debtor );
		
		cdsViolation.setPostal(false);
		Amount ridotto = new Amount();
		ridotto.setAmount(Double.valueOf(50.00));
		ridotto.setDueDate(LocalDate.now());
		ridotto.setNoticeNumber("001340000013900808");
		ridotto.setQrcode("PAGOPA|002|001340000013900808|12345678901|5000");
		cdsViolation.setReducedAmount(ridotto );
		
		Amount scontato = new Amount();
		scontato.setAmount(Double.valueOf(150.00));
		scontato.setDueDate(LocalDate.now());
		scontato.setNoticeNumber("001340000013900909");
		scontato.setQrcode("PAGOPA|002|001340000013900909|12345678901|15000");
		cdsViolation.setDiscountedAmount(scontato );
		
		
		String body = mapper.writeValueAsString(cdsViolation);

		MvcResult result = this.mockMvc.perform(post(Costanti.CDS_VIOLATION_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andReturn();

		assertNotNull(result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE));
		assertNotNull(result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION));
		// TODO aggiungi check nome file
	}
	
}

