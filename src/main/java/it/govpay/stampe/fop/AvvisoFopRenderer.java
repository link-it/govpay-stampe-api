package it.govpay.stampe.fop;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopConfParser;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.zxing.WriterException;

/**
 * Genera il PDF di un avviso con Apache FOP a partire dallo stesso XML (AvvisoPagamentoInput)
 * che oggi viene passato a JasperReports (issue #37).
 * <p>
 * Configurazione di FOP e fogli XSLT sono letti una volta sola: {@link FopFactory} e
 * {@link Templates} sono thread-safe e riusabili tra le richieste.
 * <p>
 * Prima della trasformazione ogni {@code rata} con un {@code qr_code} riceve l'elemento
 * {@code qr_code_svg} con il QR code gia' calcolato (URI data:), perche' XSLT non puo' generarlo.
 * L'elemento non fa parte dello schema: esiste solo nel documento passato alla trasformazione.
 */
public class AvvisoFopRenderer {

	private static final String BASE_XSL = "/fop/";
	private static final String CONFIGURAZIONE = "/fop/fop.xconf";
	private static final String FONT = "/fonts/";

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
	private final Templates templates;

	public AvvisoFopRenderer(String foglio) {
		try {
			URL font = risorsa(FONT);
			try (InputStream conf = risorsa(CONFIGURAZIONE).openStream()) {
				this.fopFactory = new FopConfParser(conf, font.toURI()).getFopFactoryBuilder().build();
			}
			TransformerFactory tf = TransformerFactory.newInstance();
			tf.setURIResolver(new ClasspathResolver());
			URL xsl = risorsa(BASE_XSL + foglio);
			this.templates = tf.newTemplates(new StreamSource(xsl.openStream(), xsl.toExternalForm()));
		} catch (IOException | SAXException | URISyntaxException | TransformerException e) {
			throw new IllegalStateException("Inizializzazione del renderer FOP per " + foglio + " non riuscita", e);
		}
	}

	public byte[] genera(byte[] xmlInput) {
		try {
			Document doc = leggi(xmlInput);
			aggiungiQrCode(doc);

			ByteArrayOutputStream pdf = new ByteArrayOutputStream();
			Fop fop = this.fopFactory.newFop(MimeConstants.MIME_PDF, pdf);
			Transformer t = this.templates.newTransformer();
			// il processore del JDK di default scrive gli errori nel log e prosegue:
			// un errore di trasformazione o di validazione FO deve invece far fallire la stampa
			t.setErrorListener(ERRORI_BLOCCANTI);
			Result fo = new SAXResult(fop.getDefaultHandler());
			t.transform(new DOMSource(doc), fo);
			if (fop.getResults() == null || fop.getResults().getPageCount() == 0) {
				throw new IllegalStateException("FOP non ha prodotto pagine");
			}
			return pdf.toByteArray();
		} catch (TransformerException | SAXException | IOException | ParserConfigurationException | WriterException e) {
			throw new IllegalStateException("Generazione del PDF con FOP non riuscita", e);
		}
	}

	private static Document leggi(byte[] xml) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		// l'input e' generato dal servizio, ma il parser resta chiuso a DTD ed entita' esterne
		dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		dbf.setXIncludeAware(false);
		dbf.setExpandEntityReferences(false);
		return dbf.newDocumentBuilder().parse(new ByteArrayInputStream(xml));
	}

	private static void aggiungiQrCode(Document doc) throws WriterException {
		NodeList rate = doc.getElementsByTagName("rata");
		for (int i = 0; i < rate.getLength(); i++) {
			Element rata = (Element) rate.item(i);
			NodeList qr = rata.getElementsByTagName("qr_code");
			if (qr.getLength() == 0) {
				continue;
			}
			String contenuto = qr.item(0).getTextContent();
			if (contenuto == null || contenuto.isBlank()) {
				continue;
			}
			Element svg = doc.createElement("qr_code_svg");
			svg.setTextContent(QrCodeSvg.dataUri(contenuto.trim()));
			rata.appendChild(svg);
		}
	}

	private static URL risorsa(String percorso) {
		URL url = AvvisoFopRenderer.class.getResource(percorso);
		if (url == null) {
			throw new IllegalStateException("Risorsa non trovata nel classpath: " + percorso);
		}
		return url;
	}

	/** Risolve gli xsl:include relativi rispetto al foglio che li contiene, anche dentro il jar. */
	private static final class ClasspathResolver implements URIResolver {
		@Override
		public Source resolve(String href, String base) throws TransformerException {
			try {
				// URI.resolve non funziona sugli URI jar: (opachi): si parte dalla cartella del foglio base
				URL url = base != null
						? URI.create(base.substring(0, base.lastIndexOf('/') + 1) + href).toURL()
						: risorsa(BASE_XSL + href);
				return new StreamSource(url.openStream(), url.toExternalForm());
			} catch (IOException | IllegalArgumentException e) {
				throw new TransformerException("Foglio XSLT non trovato: " + href, e);
			}
		}
	}
}
