package it.govpay.stampe.fop;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.MimeConstants;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;


/**
 * Prototipo FOP (issue #37): genera con FOP i PDF dei casi di riferimento, a partire dallo stesso
 * XML di input passato a Jasper, per il confronto con i PDF prodotti da Jasper.
 * <p>
 * L'XML e' letto nel modello JAXB e stampato come fa il servizio: {@link InputStampa} e {@link MotoreFop}.
 * <p>
 * I casi di riferimento non sono nel repository: il test si esegue solo indicando la directory
 * del run e l'elenco dei casi, per esempio
 * {@code -Dprototipo.run=pdf-riferimento/run-20261012 -Dprototipo.casi=casi-prototipo-RataUnica.json}
 * e, se il layout non e' quello dell'avviso semplice, il foglio: {@code -Dprototipo.foglio=avviso-postale.xsl}.
 * Con {@code ricevuta.xsl} l'XML e' letto nel modello della ricevuta, con {@code avviso-bilingue.xsl} nel
 * modello v2.
 * I PDF generati vanno in {@code target/prototipo-fop/}, con il documento XSL-FO di ciascuno e, con
 * {@code -Dprototipo.areatree=true}, l'area tree di FOP ({@code .at.xml}): la pagina impaginata, per
 * confrontare due versioni dei fogli senza passare dal PDF.
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
		boolean areaTree = Boolean.getBoolean("prototipo.areatree");
		JAXBContext contesto = InputStampa.contesto(foglio);
		MotoreFop motore = new MotoreFop();
		for (Map<String, Object> caso : elenco) {
			String xml = (String) caso.get("xml");
			long inizio = System.nanoTime();
			JAXBElement<?> elemento = InputStampa.leggi(foglio, contesto, new StreamSource(dirRun.resolve("xml").resolve(xml).toFile()));
			byte[] pdf = motore.genera(foglio, contesto, elemento);
			long ms = (System.nanoTime() - inizio) / 1_000_000;
			Path file = uscita.resolve(xml.replace(".xml", ".pdf"));
			Files.write(file, pdf);
			// il documento XSL-FO, con le funzioni Java: per le verifiche e per il confronto delle posizioni
			Files.writeString(uscita.resolve(xml.replace(".xml", ".fo")), motore.fo(foglio, contesto, elemento));
			if (areaTree) {
				Files.write(uscita.resolve(xml.replace(".xml", ".at.xml")), motore.genera(foglio, contesto, elemento, MimeConstants.MIME_FOP_AREA_TREE, 0));
			}
			System.out.printf("prototipo FOP: %s -> %s (%d byte, %d ms)%n", xml, file, pdf.length, ms);
			assertTrue(pdf.length > 0);
		}
	}
}
