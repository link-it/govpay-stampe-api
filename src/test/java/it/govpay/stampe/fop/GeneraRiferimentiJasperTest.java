package it.govpay.stampe.fop;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamResult;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import jakarta.xml.bind.JAXBElement;
import tools.jackson.databind.ObjectMapper;

import it.govpay.stampe.Application;
import it.govpay.stampe.beans.Instalment;
import it.govpay.stampe.beans.PaymentNotice;
import it.govpay.stampe.config.LabelAvvisiConfiguration.LabelAvvisiProperties;
import it.govpay.stampe.mapper.AvvisoPagamentoMapper;
import it.govpay.stampe.model.v1.AvvisoPagamentoInput;
import it.govpay.stampe.service.AvvisoPostaleService;
import it.govpay.stampe.service.AvvisoSempliceService;
import it.govpay.stampe.test.utils.AvvisiPagamentoFactory;

/**
 * Casi di riferimento Jasper per i layout multi-rata (issue #37), che la testsuite del core non
 * produce: per ogni variante costruisce l'avviso come il controller, salva l'XML di input e il PDF
 * stampato con JasperReports.
 * <p>
 * L'uscita ha la stessa struttura dei run della testsuite del core ({@code xml/}, {@code pdf/} e un
 * elenco {@code casi-<famiglia>.json}), quindi si confronta con {@link PrototipoFopTest} e
 * {@code script/fop/verifica_pdf.py}. Si esegue solo indicando la directory di uscita:
 * {@code -Driferimenti.uscita=pdf-riferimento/jasper-multirata}.
 */
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@TestPropertySource(properties = { "stampe.motore.avviso-semplice=jasper", "stampe.motore.avviso-postale=jasper" })
class GeneraRiferimentiJasperTest {

	@Autowired
	AvvisiPagamentoFactory factory;

	@Autowired
	AvvisoPagamentoMapper mapper;

	@Autowired
	@Qualifier("labelAvvisiProperties")
	LabelAvvisiProperties labelAvvisiProperties;

	@Autowired
	@Qualifier("jaxb2MarshallerAvvisoPagamento")
	Jaxb2Marshaller marshaller;

	@Autowired
	AvvisoSempliceService avvisoSempliceService;

	@Autowired
	AvvisoPostaleService avvisoPostaleService;

	@Test
	void generaRiferimenti() throws Exception {
		String uscita = System.getProperty("riferimenti.uscita");
		Assumptions.assumeTrue(uscita != null, "riferimenti.uscita non indicata");
		Path dir = Path.of(uscita);
		Files.createDirectories(dir.resolve("xml"));
		Files.createDirectories(dir.resolve("pdf"));

		Map<String, List<Map<String, String>>> famiglie = new LinkedHashMap<>();
		int n = 0;
		for (Map.Entry<String, Supplier<PaymentNotice>> caso : casi().entrySet()) {
			PaymentNotice avviso = caso.getValue().get();
			avviso.setSecondLanguage(null);
			AvvisoPagamentoInput input = this.mapper.toPaymentNoticeAvvisoPagamentoInput(
					LoggerFactory.getLogger(GeneraRiferimentiJasperTest.class), avviso, this.labelAvvisiProperties);
			String xml = String.format("%05d.xml", ++n);
			this.marshaller.createMarshaller().marshal(
					new JAXBElement<>(new QName("", "AvvisoPagamentoInput"), AvvisoPagamentoInput.class, input),
					new StreamResult(dir.resolve("xml").resolve(xml).toFile()));
			boolean postale = input.getDiPoste() != null;
			byte[] pdf = postale ? this.avvisoPostaleService.creaAvviso(input) : this.avvisoSempliceService.creaAvviso(input);
			String nomePdf = caso.getKey() + ".pdf";
			Files.write(dir.resolve("pdf").resolve(nomePdf), pdf);
			famiglie.computeIfAbsent(postale ? "postale" : "semplice", k -> new ArrayList<>())
					.add(Map.of("caso", caso.getKey(), "xml", xml, "pdf", "pdf/" + nomePdf));
			System.out.printf("riferimento Jasper: %s -> %s (%d byte)%n", caso.getKey(), xml, pdf.length);
		}
		for (Map.Entry<String, List<Map<String, String>>> f : famiglie.entrySet()) {
			new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(dir.resolve("casi-" + f.getKey() + ".json").toFile(), f.getValue());
		}
	}

	private Map<String, Supplier<PaymentNotice>> casi() {
		Map<String, Supplier<PaymentNotice>> casi = new LinkedHashMap<>();
		// avviso semplice: pagina doppia e tripla, con e senza rata unica
		casi.put("doppia-unica", () -> rate(2, true, false));
		casi.put("doppia", () -> rate(2, false, false));
		casi.put("tripla-unica", () -> rate(3, true, false));
		casi.put("tripla", () -> rate(3, false, false));
		casi.put("soglie-2-unica", () -> this.factory.creaPaymentNoticeConSoglie(2, true, false, false));
		casi.put("soglie-3", () -> this.factory.creaPaymentNoticeConSoglie(3, false, false, false));
		casi.put("doppia-senza-indirizzo", () -> {
			PaymentNotice p = rate(2, true, false);
			p.getDebtor().setAddressLine1(null);
			p.getDebtor().setAddressLine2(null);
			return p;
		});
		casi.put("tripla-due-loghi-oggetto-lungo", () -> {
			PaymentNotice p = rate(3, true, false);
			p.setSecondLogo(new ByteArrayResource(p.getFirstLogo() instanceof ByteArrayResource b ? b.getByteArray() : new byte[0]));
			p.setTitle("Tassa sui rifiuti (TARI) anno 2026, saldo e acconto per l'immobile di via Roma 1, foglio 12, particella 345, subalterno 6");
			return p;
		});
		// avviso semplice: pagine multiple, dalla pagina principale all'ultima a 2 o 3 colonne
		for (int rate : new int[] { 4, 5, 6, 7, 8, 9, 10, 11, 13, 14, 17, 22 }) {
			casi.put("multipla-" + rate + "-unica", () -> rate(rate, true, false));
		}
		casi.put("multipla-4", () -> rate(4, false, false));
		casi.put("multipla-6", () -> rate(6, false, false));
		// avviso postale: pagine doppia e tripla, da sole e insieme alla singola
		for (int rate : new int[] { 2, 3, 4, 5, 6, 7 }) {
			casi.put("postale-" + rate, () -> rate(rate, false, true));
			casi.put("postale-" + rate + "-unica", () -> rate(rate, true, true));
		}
		return casi;
	}

	/** Avviso con N rate di importi e scadenze diversi, con o senza rata unica. */
	private PaymentNotice rate(int numero, boolean rataUnica, boolean postale) {
		PaymentNotice p = this.factory.creaPaymentNoticeConRate(numero, rataUnica);
		p.setPostal(postale);
		LocalDate inizio = LocalDate.of(2026, 11, 30);
		int i = 0;
		for (Instalment rata : p.getInstalments()) {
			rata.setDueDate(inizio.plusMonths(i));
			rata.setAmount(Double.valueOf(37.5 + 12.25 * i));
			i++;
		}
		if (p.getFull() != null) {
			p.getFull().setDueDate(inizio);
			p.getFull().setAmount(Double.valueOf(37.5 * numero + 12.25 * numero * (numero - 1) / 2));
		}
		return p;
	}
}
