package it.govpay.stampe.fop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.MimeConstants;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;

/**
 * Regressione dei layout XSL-FO (issue #37): ogni caso in {@code src/test/resources/regressione-layout/<foglio>/}
 * e' stampato con il foglio {@code <foglio>.xsl} e l'area tree di FOP, cioe' la pagina gia' impaginata con
 * posizioni, testi e immagini, deve avere l'hash SHA-256 indicato in {@code attesi.properties}.
 * <p>
 * Un hash diverso significa che il PDF e' cambiato: l'area tree del caso viene scritta in
 * {@code target/regressione-layout/}, insieme al PDF, per il confronto con quella della versione precedente.
 * Se la modifica del layout e' voluta, gli hash si aggiornano con
 * {@code mvn test -Dtest=RegressioneLayoutTest -Dregressione.aggiorna=true}. Gli input si rigenerano con
 * {@link GeneraCasiRegressioneTest}.
 */
class RegressioneLayoutTest {

	static final String CARTELLA = "regressione-layout";
	static final String SEGNAPOSTO_LOGO = "@logo";
	private static final Path SORGENTI = Path.of("src", "test", "resources", CARTELLA);
	private static final Path USCITA = Path.of("target", CARTELLA);
	private static final String ATTESI = "attesi.properties";

	private static MotoreFop motore;
	private static String logo;
	private static Map<String, String> attesi;
	private static final Map<String, String> calcolati = new TreeMap<>();

	@BeforeAll
	static void prepara() throws IOException {
		motore = new MotoreFop();
		try (InputStream is = MotoreFop.apri("logo_encoded.txt")) {
			// il logo di prova e' l'URI data: del PNG, a sua volta codificato in base64
			logo = new String(Base64.getMimeDecoder().decode(is.readAllBytes()), StandardCharsets.UTF_8).trim();
		}
		attesi = new TreeMap<>();
		for (String riga : Files.readAllLines(SORGENTI.resolve(ATTESI))) {
			if (!riga.isBlank() && !riga.startsWith("#")) {
				attesi.put(riga.substring(0, riga.indexOf('=')).trim(), riga.substring(riga.indexOf('=') + 1).trim());
			}
		}
	}

	static Stream<String> casi() throws IOException {
		List<String> casi = new ArrayList<>();
		try (Stream<Path> file = Files.walk(SORGENTI)) {
			file.filter(f -> f.toString().endsWith(".xml"))
					.forEach(f -> casi.add(SORGENTI.relativize(f).toString().replace('\\', '/').replaceAll("\\.xml$", "")));
		}
		return casi.stream().sorted();
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("casi")
	void layoutInvariato(String caso) throws Exception {
		String foglio = caso.substring(0, caso.indexOf('/')) + ".xsl";
		String xml = Files.readString(SORGENTI.resolve(caso + ".xml")).replace(">" + SEGNAPOSTO_LOGO + "<", ">" + logo + "<");
		JAXBContext contesto = InputStampa.contesto(foglio);
		JAXBElement<?> elemento = InputStampa.leggi(foglio, contesto, new StreamSource(new StringReader(xml)));
		byte[] areaTree = motore.genera(foglio, contesto, elemento, MimeConstants.MIME_FOP_AREA_TREE, 0);
		// la prima riga riporta la versione di FOP: un aggiornamento di FOP conta solo se cambia la pagina
		String hash = sha256(new String(areaTree, StandardCharsets.UTF_8).replaceFirst("<!--Produced by [^>]*-->", ""));
		calcolati.put(caso, hash);
		if (Boolean.getBoolean("regressione.aggiorna")) {
			return;
		}
		if (!hash.equals(attesi.get(caso))) {
			Path at = USCITA.resolve(caso + ".at.xml");
			Files.createDirectories(at.getParent());
			Files.write(at, areaTree);
			Files.write(USCITA.resolve(caso + ".pdf"), motore.genera(foglio, contesto, elemento));
		}
		assertFalse(attesi.get(caso) == null, "hash atteso mancante per " + caso + ": aggiornare con -Dregressione.aggiorna=true");
		assertEquals(attesi.get(caso), hash, "layout cambiato per " + caso + ": area tree e PDF in " + USCITA.resolve(caso + ".*"));
	}

	@AfterAll
	static void aggiorna() throws IOException {
		if (Boolean.getBoolean("regressione.aggiorna")) {
			StringBuilder sb = new StringBuilder("# SHA-256 dell'area tree FOP di ogni caso, scritto da RegressioneLayoutTest con -Dregressione.aggiorna=true\n");
			calcolati.forEach((caso, hash) -> sb.append(caso).append('=').append(hash).append('\n'));
			Files.writeString(SORGENTI.resolve(ATTESI), sb.toString());
		}
	}

	private static String sha256(String testo) throws Exception {
		return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(testo.getBytes(StandardCharsets.UTF_8)));
	}
}
