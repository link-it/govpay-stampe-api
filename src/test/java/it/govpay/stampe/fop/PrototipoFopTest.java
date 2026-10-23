package it.govpay.stampe.fop;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import javax.xml.transform.stream.StreamSource;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import it.govpay.stampe.model.v1.AvvisoPagamentoInput;
import it.govpay.stampe.model.v1.RicevutaTelematicaInput;

/**
 * Prototipo FOP (issue #37): genera con FOP i PDF dei casi di riferimento, a partire dallo stesso
 * XML di input passato a Jasper, per il confronto con i PDF prodotti da Jasper.
 * <p>
 * L'XML e' letto nel modello JAXB e stampato come fa il servizio: {@link PreparazioneAvviso} e
 * {@link MotoreFop} sullo stesso oggetto.
 * <p>
 * I casi di riferimento non sono nel repository: il test si esegue solo indicando la directory
 * del run e l'elenco dei casi, per esempio
 * {@code -Dprototipo.run=pdf-riferimento/run-20261012 -Dprototipo.casi=casi-prototipo-RataUnica.json}
 * e, se il layout non e' quello dell'avviso semplice, il foglio: {@code -Dprototipo.foglio=avviso-postale.xsl}.
 * Con {@code ricevuta.xsl} l'XML e' letto nel modello della ricevuta, con {@code avviso-bilingue.xsl} nel
 * modello v2.
 * I PDF generati vanno in {@code target/prototipo-fop/}, con il documento XSL-FO di ciascuno.
 */
class PrototipoFopTest {

	@Test
	void generaCasiDiRiferimento() throws Exception {
		String run = System.getProperty("prototipo.run");
		String casi = System.getProperty("prototipo.casi");
		Assumptions.assumeTrue(run != null && casi != null, "prototipo.run e prototipo.casi non indicati");

		Path dirRun = Path.of(run);
		List<Map<String, Object>> elenco = new ObjectMapper().readValue(dirRun.resolve(casi).toFile(),
				new TypeReference<List<Map<String, Object>>>() {});
		Path uscita = Path.of("target", "prototipo-fop");
		Files.createDirectories(uscita);

		String foglio = System.getProperty("prototipo.foglio", "avviso-semplice.xsl");
		boolean ricevuta = "ricevuta.xsl".equals(foglio);
		boolean bilingue = "avviso-bilingue.xsl".equals(foglio);
		JAXBContext contesto = bilingue ? JAXBContext.newInstance(it.govpay.stampe.model.v2.AvvisoPagamentoInput.class)
				: JAXBContext.newInstance(AvvisoPagamentoInput.class, RicevutaTelematicaInput.class);
		MotoreFop motore = new MotoreFop();
		for (Map<String, Object> caso : elenco) {
			String xml = (String) caso.get("xml");
			// la radice resta quella dell'XML: AvvisoPagamentoInput, ViolazioneInput per la violazione CdS, root per la ricevuta
			StreamSource sorgente = new StreamSource(dirRun.resolve("xml").resolve(xml).toFile());
			long inizio = System.nanoTime();
			JAXBElement<?> elemento;
			if (bilingue) {
				JAXBElement<it.govpay.stampe.model.v2.AvvisoPagamentoInput> v2 = contesto.createUnmarshaller()
						.unmarshal(sorgente, it.govpay.stampe.model.v2.AvvisoPagamentoInput.class);
				PreparazioneAvviso.completa(v2.getValue());
				elemento = v2;
			} else if (ricevuta) {
				JAXBElement<RicevutaTelematicaInput> rt = contesto.createUnmarshaller().unmarshal(sorgente, RicevutaTelematicaInput.class);
				PreparazioneAvviso.completa(rt.getValue());
				elemento = rt;
			} else {
				JAXBElement<AvvisoPagamentoInput> avviso = contesto.createUnmarshaller().unmarshal(sorgente, AvvisoPagamentoInput.class);
				PreparazioneAvviso.completa(avviso.getValue());
				elemento = avviso;
			}
			byte[] pdf = motore.genera(foglio, contesto, elemento);
			long ms = (System.nanoTime() - inizio) / 1_000_000;
			Path file = uscita.resolve(xml.replace(".xml", ".pdf"));
			Files.write(file, pdf);
			// il documento XSL-FO, con le funzioni Java: per le verifiche e per il confronto delle posizioni
			Files.writeString(uscita.resolve(xml.replace(".xml", ".fo")), motore.fo(foglio, contesto, elemento));
			System.out.printf("prototipo FOP: %s -> %s (%d byte, %d ms)%n", xml, file, pdf.length, ms);
			assertTrue(pdf.length > 0);
		}
	}
}
