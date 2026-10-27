package it.govpay.stampe.fop;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopConfParser;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.xmlgraphics.io.Resource;
import org.apache.xmlgraphics.io.ResourceResolver;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.util.JAXBSource;

/**
 * Motore di stampa Apache FOP (issue #37): trasforma un oggetto JAXB con un foglio XSLT in XSL-FO e
 * lo rende in PDF, senza passare da un XML serializzato. L'oggetto e' letto dalla trasformazione
 * tramite {@link JAXBSource} e il risultato va direttamente a FOP come eventi SAX.
 * <p>
 * {@link FopFactory} e {@link Templates} sono thread-safe: la configurazione e i font sono letti
 * una volta sola, i fogli XSLT sono compilati al primo uso (o con {@link #prepara(String)}) e
 * riusati tra le richieste.
 * <p>
 * Fogli, font e configurazione sono letti dal classpath (i font con lo schema {@code classpath:}), che
 * funziona allo stesso modo da {@code target/classes}, dal jar eseguibile e dal war. Il motore non
 * apre altre risorse: le immagini dei layout sono URI {@code data:}, gestiti da FOP stesso.
 */
@Component
public class MotoreFop {

	static final String SCHEMA_CLASSPATH = "classpath";
	private static final String BASE_XSL = "fop/";
	private static final String CONFIGURAZIONE = "fop/fop.xconf";
	private static final String[] LIMITI_XPATH = { "jdk.xml.xpathExprGrpLimit", "jdk.xml.xpathExprOpLimit", "jdk.xml.xpathTotalOpLimit" };

	private static final ErrorListener ERRORI_BLOCCANTI = new ErrorListener() {
		@Override
		public void warning(TransformerException e) {
			// gli avvisi del processore non bloccano la stampa
		}

		@Override
		public void error(TransformerException e) throws TransformerException {
			throw e;
		}

		@Override
		public void fatalError(TransformerException e) throws TransformerException {
			throw e;
		}
	};

	private final FopFactory fopFactory;
	private final TransformerFactory transformerFactory;
	private final Map<String, Templates> fogli = new ConcurrentHashMap<>();

	public MotoreFop() {
		try (InputStream conf = apri(CONFIGURAZIONE)) {
			// la base deve essere un URL apribile, perche' FOP la passa anche ai documenti SVG; le risorse
			// della configurazione hanno comunque URI classpath: assoluti
			URI base = MotoreFop.class.getClassLoader().getResource(BASE_XSL).toURI();
			this.fopFactory = new FopConfParser(conf, base, new ClasspathResourceResolver()).getFopFactoryBuilder().build();
		} catch (IOException | SAXException | URISyntaxException e) {
			throw new IllegalStateException("Configurazione di FOP non leggibile: " + CONFIGURAZIONE, e);
		}
		// il processore XSLT del JDK (XSLTC), non quello trovato nel classpath: Xalan, che arrivava con
		// JasperReports, non risolve i template con nome dei fogli inclusi, e una dipendenza che lo
		// riportasse cambierebbe il processore senza che nessuno lo scelga
		this.transformerFactory = TransformerFactory.newDefaultInstance();
		// i fogli sono generati da jrxml2fo.py, non arrivano dall'esterno: le posizioni con righe rimosse
		// e sottoreport allungati sono espressioni XPath piu' lunghe dei limiti di default del JDK
		for (String limite : LIMITI_XPATH) {
			this.transformerFactory.setAttribute(limite, 0);
		}
		this.transformerFactory.setURIResolver(new ClasspathUriResolver());
	}

	/** Compila il foglio, se non e' gia' in cache: un errore nel foglio emerge all'avvio e non alla prima stampa. */
	public void prepara(String foglio) {
		templates(foglio);
	}

	/**
	 * Genera il PDF.
	 *
	 * @param foglio nome del foglio XSLT in {@code fop/} nel classpath, per esempio {@code avviso-semplice.xsl}
	 * @param contesto contesto JAXB del modello
	 * @param avviso elemento radice JAXB (un {@code JAXBElement} o un oggetto annotato {@code @XmlRootElement})
	 */
	public byte[] genera(String foglio, JAXBContext contesto, Object avviso) {
		return genera(foglio, contesto, avviso, MimeConstants.MIME_PDF, 0);
	}

	/**
	 * Come {@link #genera(String, JAXBContext, Object)}, nel formato indicato. Per i test: con
	 * {@link MimeConstants#MIME_PNG} e una risoluzione in dpi la pagina e' resa come immagine, da cui
	 * leggere QR code e DataMatrix.
	 */
	byte[] genera(String foglio, JAXBContext contesto, Object avviso, String formato, float dpi) {
		try {
			ByteArrayOutputStream pdf = new ByteArrayOutputStream();
			FOUserAgent agente = this.fopFactory.newFOUserAgent();
			if (dpi > 0) {
				agente.setTargetResolution(dpi);
			}
			Fop fop = this.fopFactory.newFop(formato, agente, pdf);
			Transformer t = templates(foglio).newTransformer();
			// il processore del JDK di default scrive gli errori nel log e prosegue:
			// un errore di trasformazione o di validazione FO deve invece far fallire la stampa
			t.setErrorListener(ERRORI_BLOCCANTI);
			t.transform(new JAXBSource(contesto, avviso), new SAXResult(fop.getDefaultHandler()));
			if (fop.getResults() == null || fop.getResults().getPageCount() == 0) {
				throw new IllegalStateException("FOP non ha prodotto pagine");
			}
			return pdf.toByteArray();
		} catch (TransformerException | JAXBException | FOPException e) {
			throw new IllegalStateException("Generazione del PDF con FOP non riuscita (" + foglio + ")", e);
		}
	}

	/**
	 * Per le verifiche: il documento XSL-FO prodotto dal foglio, con le funzioni di estensione Java che
	 * xsltproc non ha (per esempio gli a capo di {@link TestoJasper}).
	 */
	String fo(String foglio, JAXBContext contesto, Object avviso) {
		try {
			java.io.StringWriter fo = new java.io.StringWriter();
			Transformer t = templates(foglio).newTransformer();
			t.setErrorListener(ERRORI_BLOCCANTI);
			t.transform(new JAXBSource(contesto, avviso), new javax.xml.transform.stream.StreamResult(fo));
			return fo.toString();
		} catch (TransformerException | JAXBException e) {
			throw new IllegalStateException("Trasformazione XSLT non riuscita (" + foglio + ")", e);
		}
	}

	private Templates templates(String foglio) {
		return this.fogli.computeIfAbsent(foglio, f -> {
			try {
				return this.transformerFactory.newTemplates(sorgente(f));
			} catch (TransformerException e) {
				throw new IllegalStateException("Foglio XSLT non valido: " + f, e);
			}
		});
	}

	private static Source sorgente(String foglio) throws TransformerException {
		try {
			return new StreamSource(apri(BASE_XSL + foglio), SCHEMA_CLASSPATH + ":/" + BASE_XSL + foglio);
		} catch (IOException e) {
			throw new TransformerException("Foglio XSLT non trovato: " + foglio, e);
		}
	}

	static InputStream apri(String percorso) throws IOException {
		InputStream is = MotoreFop.class.getClassLoader().getResourceAsStream(percorso);
		if (is == null) {
			throw new IOException("Risorsa non trovata nel classpath: " + percorso);
		}
		return is;
	}

	/** I fogli sono tutti nella stessa cartella: gli {@code xsl:include} si risolvono per nome. */
	private static final class ClasspathUriResolver implements URIResolver {
		@Override
		public Source resolve(String href, String base) throws TransformerException {
			return sorgente(href.substring(href.lastIndexOf('/') + 1));
		}
	}

	/** Risorse di FOP (i font della configurazione) lette solo dal classpath. */
	private static final class ClasspathResourceResolver implements ResourceResolver {
		@Override
		public Resource getResource(URI uri) throws IOException {
			if (!SCHEMA_CLASSPATH.equals(uri.getScheme())) {
				throw new IOException("Risorsa non ammessa per la stampa: " + uri);
			}
			return new Resource(apri(uri.getPath().substring(1)));
		}

		@Override
		public OutputStream getOutputStream(URI uri) throws IOException {
			throw new IOException("Scrittura non ammessa per la stampa: " + uri);
		}
	}
}
