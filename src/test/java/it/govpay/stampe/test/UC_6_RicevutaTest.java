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
import it.govpay.stampe.beans.Payer;
import it.govpay.stampe.beans.Receipt;
import it.govpay.stampe.beans.ReceiptItem;
import it.govpay.stampe.beans.ReceiptItemStatus;
import it.govpay.stampe.beans.ReceiptOrganization;
import it.govpay.stampe.beans.ReceiptStatus;
import it.govpay.stampe.beans.ReceiptVersion;
import it.govpay.stampe.mapper.RicevutaMapper;
import it.govpay.stampe.test.costanti.Costanti;
import it.govpay.stampe.test.serializer.ObjectMapperUtils;
import it.govpay.stampe.test.utils.AvvisiPagamentoFactory;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@DisplayName("Test Ricevute di Pagamento")
@ActiveProfiles("test")
class UC_6_RicevutaTest {

	@Autowired
	private MockMvc mockMvc;

	private ObjectMapper mapper = ObjectMapperUtils.createObjectMapper();

	@Value("${stampe.logoEnte}")
	String logoEnte;

	@Autowired
	RicevutaMapper ricevutaMapper;

	@Test
	void UC_6_01_RicevutaOk() throws Exception {
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
