package it.govpay.stampe.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.govpay.stampe.Application;
import it.govpay.stampe.beans.Receipt;
import it.govpay.stampe.beans.VoceRicevutaTelematicaInput;
import it.govpay.stampe.mapper.RicevutaMapper;
import it.govpay.stampe.test.costanti.Costanti;
import it.govpay.stampe.test.serializer.ObjectMapperUtils;
import it.govpay.stampe.test.utils.AvvisiPagamentoFactory;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@DisplayName("Test Ricevute di Pagamento")
@ActiveProfiles("test")
class UC_9_RicevutaTest {

	@Autowired
	private MockMvc mockMvc;

	private ObjectMapper mapper = ObjectMapperUtils.createObjectMapper();

	@Value("${stampe.logoEnte}")
	String logoEnte;

	@Autowired
	RicevutaMapper ricevutaMapper;

	@Test
	void UC_9_01_RicevutaOk() throws Exception {
		Receipt receipt = creaReceipt();

		String body = mapper.writeValueAsString(receipt);

		MvcResult result = this.mockMvc.perform(post(Costanti.RECEIPT_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andReturn();

		String headerContentType = result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE);
		assertNotNull(headerContentType);
		assertEquals(MediaType.APPLICATION_PDF_VALUE, headerContentType);
		String headerContentDisposition = result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION);
		assertNotNull(headerContentDisposition);
		assertEquals(ricevutaMapper.nomePdf(receipt), AvvisiPagamentoFactory.extractFilename(headerContentDisposition));
	}

	private Receipt creaReceipt() {
		Receipt receipt = new Receipt();

		// Loghi
		receipt.setLogoEnte(this.logoEnte);
		receipt.setLogoPagopa(this.logoEnte);

		// Dati ente
		receipt.setEnteDenominazione("Comune di Test");
		receipt.setIndirizzoEnte("Via Roma, 1");
		receipt.setLuogoEnte("00100 Roma (RM)");
		receipt.setCfEnte("01234567890");

		// Dati soggetto pagatore
		receipt.setSoggetto("Mario Rossi");
		receipt.setIndirizzoSoggetto("Viale dei Giardini, 00");
		receipt.setLuogoSoggetto("00000 Roma (RM)");
		receipt.setCfSoggetto("RSSMRA80A01H501U");

		// Dati pagamento
		receipt.setOggettoDelPagamento("Pagamento TARI 2024");
		receipt.setIstituto("Banca di Test S.p.A.");
		receipt.setImporto(150.00);
		receipt.setDataOperazione("15/01/2024 10:30:00");
		receipt.setDataApplicativa("15/01/2024");
		receipt.setStato("Eseguito");

		// Identificativi
		receipt.setIUV("01234567890123456");
		receipt.setCCP("0123456789012345678901234567890");
		receipt.setVersioneOggetto("6.2.0");

		// Voci pagamento
		List<VoceRicevutaTelematicaInput> voci = new ArrayList<>();

		VoceRicevutaTelematicaInput voce1 = new VoceRicevutaTelematicaInput();
		voce1.setDescrizione("TARI 2024 - Quota fissa");
		voce1.setIdRiscossione("1");
		voce1.setImporto(100.00);
		voce1.setStato("Eseguito");
		voci.add(voce1);

		VoceRicevutaTelematicaInput voce2 = new VoceRicevutaTelematicaInput();
		voce2.setDescrizione("TARI 2024 - Quota variabile");
		voce2.setIdRiscossione("2");
		voce2.setImporto(50.00);
		voce2.setStato("Eseguito");
		voci.add(voce2);

		receipt.setElencoVoci(voci);

		return receipt;
	}
}
