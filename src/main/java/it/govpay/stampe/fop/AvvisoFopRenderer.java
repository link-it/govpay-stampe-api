package it.govpay.stampe.fop;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Base64;

import javax.imageio.ImageIO;

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
import org.w3c.dom.Node;
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
 * Prima della trasformazione il documento viene arricchito con cio' che XSLT non puo' calcolare:
 * QR code e DataMatrix come SVG ({@code qr_code_svg}, {@code data_matrix_svg}) e dimensioni in pixel
 * dei loghi ({@code <logo>_px}). Questi elementi non fanno parte dello schema: esistono solo nel
 * documento passato alla trasformazione.
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
			aggiungiCodici(doc);
			aggiungiDimensioniLoghi(doc);

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

	/**
	 * Per ogni logo (elementi logo_* della radice) aggiunge l'elemento {@code <nome>_px} con larghezza e
	 * altezza in pixel, per esempio "500 572". Serve al layout per posizionare l'immagine come Jasper,
	 * che con scaleImage RetainShape tronca a un numero intero di punti lo spazio libero nel riquadro.
	 * Un logo non decodificabile resta senza dimensioni: il layout lo scala nel riquadro senza troncamenti.
	 */
	private static void aggiungiDimensioniLoghi(Document doc) {
		Element radice = doc.getDocumentElement();
		for (Node n = radice.getFirstChild(); n != null; n = n.getNextSibling()) {
			if (!(n instanceof Element logo) || !logo.getTagName().startsWith("logo") || logo.getTagName().endsWith("_px")) {
				continue;
			}
			String valore = logo.getTextContent() == null ? "" : logo.getTextContent().trim();
			if (valore.isEmpty()) {
				continue;
			}
			try {
				byte[] dati = Base64.getMimeDecoder().decode(valore.contains(",") ? valore.substring(valore.indexOf(',') + 1) : valore);
				java.awt.image.BufferedImage img = ImageIO.read(new ByteArrayInputStream(dati));
				if (img != null) {
					Element px = doc.createElement(logo.getTagName() + "_px");
					px.setTextContent(img.getWidth() + " " + img.getHeight());
					radice.insertBefore(px, logo.getNextSibling());
				}
			} catch (IOException | IllegalArgumentException e) {
				// logo non leggibile: nessuna dimensione, come per un logo assente
			}
		}
	}

	/**
	 * Aggiunge a ogni {@code rata} i codici gia' calcolati, come URI data: SVG: {@code qr_code_svg} dal
	 * {@code qr_code} e {@code data_matrix_svg} dal {@code data_matrix} del bollettino postale.
	 * Il contenuto del DataMatrix e' passato cosi' com'e', spazi compresi: fanno parte del tracciato.
	 */
	private static void aggiungiCodici(Document doc) throws WriterException {
		NodeList rate = doc.getElementsByTagName("rata");
		for (int i = 0; i < rate.getLength(); i++) {
			Element rata = (Element) rate.item(i);
			String qr = testo(rata, "qr_code");
			if (qr != null && !qr.isBlank()) {
				aggiungi(doc, rata, "qr_code_svg", CodiciSvg.qrCodeDataUri(qr.trim()));
			}
			String dm = testo(rata, "data_matrix");
			if (dm != null && !dm.isBlank()) {
				aggiungi(doc, rata, "data_matrix_svg", CodiciSvg.dataMatrixDataUri(dm));
			}
		}
	}

	private static String testo(Element padre, String nome) {
		NodeList n = padre.getElementsByTagName(nome);
		return n.getLength() == 0 ? null : n.item(0).getTextContent();
	}

	private static void aggiungi(Document doc, Element padre, String nome, String valore) {
		Element e = doc.createElement(nome);
		e.setTextContent(valore);
		padre.appendChild(e);
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
