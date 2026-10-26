package it.govpay.stampe.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import tools.jackson.databind.ObjectMapper;

import it.govpay.stampe.Application;
import it.govpay.stampe.beans.PaymentNotice;
import it.govpay.stampe.beans.PaymentNoticeOption;
import it.govpay.stampe.config.LabelAvvisiConfiguration.LabelAvvisiProperties;
import it.govpay.stampe.mapper.AvvisoPagamentoMapper;
import it.govpay.stampe.model.v1.AvvisoPagamentoInput;
import it.govpay.stampe.model.v1.PaginaAvvisoSingola;
import it.govpay.stampe.model.v1.PaginaAvvisoTripla;
import it.govpay.stampe.test.costanti.Costanti;
import it.govpay.stampe.test.serializer.ObjectMapperUtils;
import it.govpay.stampe.test.utils.AvvisiPagamentoFactory;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@DisplayName("Test Avvisi con Opzioni di Pagamento Alternative")
@ActiveProfiles("test")
class UC_13_AvvisoOpzioniPagamentoTest {

	private static final Logger log = LoggerFactory.getLogger(UC_13_AvvisoOpzioniPagamentoTest.class);

	@Autowired
	private MockMvc mockMvc;

	private ObjectMapper mapper = ObjectMapperUtils.createObjectMapper();

	@Autowired
	AvvisiPagamentoFactory avvisiPagamentoFactory;

	@Autowired
	AvvisoPagamentoMapper avvisoPagamentoMapper;

	@Autowired
	@Qualifier("labelAvvisiProperties")
	LabelAvvisiProperties labelAvvisiProperties;

	@Test
	@DisplayName("due opzioni a rata unica: due pagine singole, una per opzione, mai mescolate")
	void UC_13_01_DueOpzioniFull_DuePagineSingole() {
		PaymentNotice avviso = this.avvisiPagamentoFactory.creaPaymentNoticeConOpzioni(
				this.avvisiPagamentoFactory.creaOpzioneFull(), this.avvisiPagamentoFactory.creaOpzioneFull());

		AvvisoPagamentoInput input = this.avvisoPagamentoMapper.toPaymentNoticeAvvisoPagamentoInput(log, avviso,
				this.labelAvvisiProperties);

		List<Object> pagine = input.getPagine().getSingolaOrDoppiaOrTripla();
		assertEquals(2, pagine.size());
		assertInstanceOf(PaginaAvvisoSingola.class, pagine.get(0));
		assertInstanceOf(PaginaAvvisoSingola.class, pagine.get(1));

		String primaOpzione = ((PaginaAvvisoSingola) pagine.get(0)).getRata().getCodiceAvviso();
		String secondaOpzione = ((PaginaAvvisoSingola) pagine.get(1)).getRata().getCodiceAvviso();
		assertNotEquals(primaOpzione, secondaOpzione);
	}

	@Test
	@DisplayName("opzione a rata unica + opzione a 3 rate: la seconda pagina non contiene righe della prima opzione")
	void UC_13_02_OpzioneFullEOpzioneTreRate_NessunaMescolanza() {
		PaymentNotice avviso = this.avvisiPagamentoFactory.creaPaymentNoticeConOpzioni(
				this.avvisiPagamentoFactory.creaOpzioneFull(), this.avvisiPagamentoFactory.creaOpzioneConRate(3));

		AvvisoPagamentoInput input = this.avvisoPagamentoMapper.toPaymentNoticeAvvisoPagamentoInput(log, avviso,
				this.labelAvvisiProperties);

		List<Object> pagine = input.getPagine().getSingolaOrDoppiaOrTripla();
		// opzione 1 (full): 1 pagina singola; opzione 2 (3 rate): 1 pagina tripla, mai mescolate
		assertEquals(2, pagine.size());
		assertInstanceOf(PaginaAvvisoSingola.class, pagine.get(0));
		assertInstanceOf(PaginaAvvisoTripla.class, pagine.get(1));

		PaginaAvvisoTripla paginaTripla = (PaginaAvvisoTripla) pagine.get(1);
		assertNull(paginaTripla.getUnica());
		assertEquals(3, paginaTripla.getRata().size());
	}

	@Test
	@DisplayName("opzione a 2 rate + opzione a 4 rate: il confine fra opzioni resta rispettato")
	void UC_13_03_DueOpzioniConRate_ConfineRispettato() {
		PaymentNotice avviso = this.avvisiPagamentoFactory.creaPaymentNoticeConOpzioni(
				this.avvisiPagamentoFactory.creaOpzioneConRate(2), this.avvisiPagamentoFactory.creaOpzioneConRate(4));

		AvvisoPagamentoInput input = this.avvisoPagamentoMapper.toPaymentNoticeAvvisoPagamentoInput(log, avviso,
				this.labelAvvisiProperties);

		List<Object> pagine = input.getPagine().getSingolaOrDoppiaOrTripla();
		// opzione 1 (2 rate): 1 pagina doppia; opzione 2 (4 rate, layout multiplo): pagina
		// principale doppia + pagina finale multipla - 3 pagine totali, nessuna mescolata
		assertEquals(3, pagine.size());
	}

	@Test
	@DisplayName("il nome del PDF usa il notice number della prima opzione")
	void UC_13_04_NomePdf_UsaPrimaOpzione() {
		PaymentNotice avviso = this.avvisiPagamentoFactory.creaPaymentNoticeConOpzioni(
				this.avvisiPagamentoFactory.creaOpzioneFull(), this.avvisiPagamentoFactory.creaOpzioneConRate(2));

		String nomePdf = this.avvisoPagamentoMapper.nomePdf(avviso);

		assertTrue(nomePdf.contains(avviso.getOptions().get(0).getFull().getNoticeNumber()));
	}

	@Test
	@DisplayName("201, PDF generato correttamente con due opzioni alternative")
	void UC_13_05_GenerazionePdfOk() throws Exception {
		PaymentNotice avviso = this.avvisiPagamentoFactory.creaPaymentNoticeConOpzioni(
				this.avvisiPagamentoFactory.creaOpzioneFull(), this.avvisiPagamentoFactory.creaOpzioneConRate(2));

		String body = mapper.writeValueAsString(avviso);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andReturn();

		assertEquals(MediaType.APPLICATION_PDF_VALUE, result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE));
		assertNotNull(result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION));
		assertEquals(this.avvisoPagamentoMapper.nomePdf(avviso), AvvisiPagamentoFactory
				.extractFilename(result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION)));
	}

	@Test
	@DisplayName("201, variante bollettino postale con due opzioni")
	void UC_13_06_GenerazionePdfPostaleOk() throws Exception {
		PaymentNotice avviso = this.avvisiPagamentoFactory.creaPaymentNoticeConOpzioni(
				this.avvisiPagamentoFactory.creaOpzioneFull(), this.avvisiPagamentoFactory.creaOpzioneConRate(2));
		avviso.setPostal(true);

		String body = mapper.writeValueAsString(avviso);

		MvcResult result = this.mockMvc.perform(post(Costanti.STANDARD_PATH)
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andReturn();

		assertEquals(MediaType.APPLICATION_PDF_VALUE, result.getResponse().getHeader(HttpHeaders.CONTENT_TYPE));
	}

	@Test
	@DisplayName("opzione con 'full' e una sola rata, in formato postale: nessuna perdita, due pagine singole separate")
	void UC_13_07_FullConUnaSolaRataPostale_DuePagineSingoleSeparate() {
		PaymentNoticeOption opzioneMista = this.avvisiPagamentoFactory.creaOpzioneFull();
		opzioneMista.setInstalments(this.avvisiPagamentoFactory.creaOpzioneConRate(1).getInstalments());

		PaymentNotice avviso = this.avvisiPagamentoFactory.creaPaymentNoticeConOpzioni(opzioneMista,
				this.avvisiPagamentoFactory.creaOpzioneFull());
		avviso.setPostal(true);

		AvvisoPagamentoInput input = this.avvisoPagamentoMapper.toPaymentNoticeAvvisoPagamentoInput(log, avviso,
				this.labelAvvisiProperties);

		List<Object> pagine = input.getPagine().getSingolaOrDoppiaOrTripla();
		// opzione 1 (full + 1 rata, postale): 2 pagine singole separate, nessuna perdita;
		// opzione 2 (full): 1 pagina singola
		assertEquals(3, pagine.size());
		assertInstanceOf(PaginaAvvisoSingola.class, pagine.get(0));
		assertInstanceOf(PaginaAvvisoSingola.class, pagine.get(1));
		String rataFull = ((PaginaAvvisoSingola) pagine.get(0)).getRata().getCodiceAvviso();
		String rataUnica = ((PaginaAvvisoSingola) pagine.get(1)).getRata().getCodiceAvviso();
		assertNotEquals(rataFull, rataUnica);
	}
}
