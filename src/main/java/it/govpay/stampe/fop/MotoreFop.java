package it.govpay.stampe.fop;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
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
import org.apache.fop.event.LoggingEventListener;
import org.apache.fop.events.Event;
import org.apache.fop.events.EventFormatter;
import org.apache.fop.events.EventListener;
import org.apache.fop.events.model.EventSeverity;
import org.apache.xmlgraphics.io.Resource;
import org.apache.xmlgraphics.io.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * Fogli, font, icone e configurazione sono letti dal classpath (font e icone con lo schema {@code classpath:}),
 * che funziona allo stesso modo da {@code target/classes}, dal jar eseguibile e dal war. Il motore non
 * apre altre risorse: loghi e codici sono URI {@code data:}, gestiti da FOP stesso.
 * <p>
 * Un logo che FOP non riesce a stampare (SVG con risorse esterne o malformato, immagine troncata, formato
 * sconosciuto) non blocca il documento (issue #67): il documento viene ristampato senza quel logo e
 * l'anomalia finisce nel log, come per il logo di default del core (govpay#902).
 */
@Component
public class MotoreFop {

	static final String SCHEMA_CLASSPATH = "classpath";
	private static final String BASE_XSL = "fop/";
	private static final String CONFIGURAZIONE = "fop/fop.xconf";
	private static final String[] LIMITI_XPATH = { "jdk.xml.xpathExprGrpLimit", "jdk.xml.xpathExprOpLimit", "jdk.xml.xpathTotalOpLimit" };
	private static final String FUNZIONI_ESTENSIONE = "jdk.xml.enableExtensionFunctions";
	private static final int MAX_LUNGHEZZA_CAUSA = 300;
	private static final String IMMAGINE_NON_LEGGIBILE = "immagine non leggibile: formato non supportato o contenuto non valido";
	/** Pagina minima con il solo logo, per verificare se si stampa: lo stesso external-graphic dei layout. */
	private static final String FO_LOGO = "<fo:root xmlns:fo=\"http://www.w3.org/1999/XSL/Format\"><fo:layout-master-set>"
			+ "<fo:simple-page-master master-name=\"logo\" page-width=\"200pt\" page-height=\"200pt\"><fo:region-body/></fo:simple-page-master>"
			+ "</fo:layout-master-set><fo:page-sequence master-reference=\"logo\"><fo:flow flow-name=\"xsl-region-body\"><fo:block>"
			+ "<fo:external-graphic content-width=\"100pt\" content-height=\"100pt\" scaling=\"uniform\" src=\"url(%s)\"/>"
			+ "</fo:block></fo:flow></fo:page-sequence></fo:root>";

	private static final Logger LOG = LoggerFactory.getLogger(MotoreFop.class);

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
		// i fogli sono interni, non arrivano dall'esterno: alcune espressioni XPath dei layout (condizioni delle
		// righe rimosse, posizioni) superano i limiti di default del JDK
		for (String limite : LIMITI_XPATH) {
			this.transformerFactory.setAttribute(limite, 0);
		}
		// i layout chiamano TestoFop come funzione di estensione (tj:): dal JDK 24 le funzioni di estensione sono
		// disattivate di default e vanno abilitate; i fogli vengono solo dal classpath (ClasspathUriResolver)
		try {
			this.transformerFactory.setFeature(FUNZIONI_ESTENSIONE, true);
		} catch (TransformerConfigurationException e) {
			throw new IllegalStateException("Funzioni di estensione XSLT non abilitabili", e);
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
	 * Come {@link #genera(String, JAXBContext, Object)}, con i loghi del documento: se la stampa fallisce o FOP
	 * segnala un errore, ogni logo viene verificato da solo e quelli che non si stampano vengono tolti dal
	 * modello, con un WARN nel log; poi il documento viene stampato una seconda volta. Se tutti i loghi si
	 * stampano, l'errore non dipende da loro e resta quello della prima stampa.
	 */
	public byte[] genera(String foglio, JAXBContext contesto, Object avviso, LoghiDocumento loghi) {
		ErroriFop errori = new ErroriFop();
		byte[] pdf = null;
		RuntimeException fallimento = null;
		try {
			pdf = genera(foglio, contesto, avviso, MimeConstants.MIME_PDF, 0, errori);
		} catch (RuntimeException e) {
			fallimento = e;
		}
		if (fallimento == null && errori.nessuno()) {
			return pdf;
		}
		boolean tolti = false;
		for (LoghiDocumento.Logo logo : loghi.loghi()) {
			if (logo.valore() == null || logo.valore().isBlank()) {
				continue;
			}
			String causa = verificaLogo(logo.valore());
			if (causa != null) {
				LOG.warn("Logo [{}] dell'ente [{}] non stampabile nel documento [{}]: {}. Il documento viene prodotto senza il logo.",
						logo.campo(), loghi.ente(), loghi.documento(), causa);
				logo.rimuovi().run();
				tolti = true;
			}
		}
		if (!tolti) {
			// i loghi si stampano: gli errori di FOP vanno nel log come sempre, e un fallimento resta tale
			errori.registra();
			if (fallimento != null) {
				throw fallimento;
			}
			return pdf;
		}
		return genera(foglio, contesto, avviso);
	}

	/**
	 * Stampa una pagina con il solo logo.
	 *
	 * @param logo URI data del logo
	 * @return la causa per cui il logo non si stampa, {@code null} se si stampa
	 */
	String verificaLogo(String logo) {
		ErroriFop errori = new ErroriFop();
		try {
			FOUserAgent agente = this.fopFactory.newFOUserAgent();
			agente.getEventBroadcaster().addEventListener(errori);
			Fop fop = this.fopFactory.newFop(MimeConstants.MIME_PDF, agente, OutputStream.nullOutputStream());
			String fo = String.format(FO_LOGO, logo.replace("&", "&amp;").replace("<", "&lt;").replace("\"", "&quot;"));
			this.transformerFactory.newTransformer().transform(new StreamSource(new StringReader(fo)), new SAXResult(fop.getDefaultHandler()));
		} catch (TransformerException | FOPException | RuntimeException e) {
			return descriviCausa(e);
		}
		return errori.nessuno() ? null : errori.causa();
	}

	/**
	 * Come {@link #genera(String, JAXBContext, Object)}, nel formato indicato. Per i test: con
	 * {@link MimeConstants#MIME_PNG} e una risoluzione in dpi la pagina e' resa come immagine, da cui
	 * leggere QR code e DataMatrix.
	 */
	byte[] genera(String foglio, JAXBContext contesto, Object avviso, String formato, float dpi) {
		ErroriFop errori = new ErroriFop();
		try {
			return genera(foglio, contesto, avviso, formato, dpi, errori);
		} finally {
			errori.registra();
		}
	}

	/** Stampa raccogliendo in {@code errori} gli eventi di FOP di severita' ERROR o FATAL. */
	private byte[] genera(String foglio, JAXBContext contesto, Object avviso, String formato, float dpi, ErroriFop errori) {
		try {
			ByteArrayOutputStream pdf = new ByteArrayOutputStream();
			FOUserAgent agente = this.fopFactory.newFOUserAgent();
			agente.getEventBroadcaster().addEventListener(errori);
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
	 * Messaggio dell'errore piu' interno, senza le URI data e le posizioni prive di significato
	 * ({@code file:...:-1}) che Batik antepone ai suoi messaggi, come {@code LogoEnte.descriviCausa} del core.
	 */
	static String descriviCausa(Throwable e) {
		String messaggio = null;
		for (Throwable t = e; t != null; t = t.getCause() == t ? null : t.getCause()) {
			if (t instanceof NullPointerException) {
				// es. PNG troncato: il decoder si ferma su un blocco mancante
				return IMMAGINE_NON_LEGGIBILE;
			}
			if (t.getMessage() != null && !t.getMessage().isBlank()) {
				messaggio = t.getMessage();
			}
		}
		return pulisci(messaggio == null ? e.getClass().getSimpleName() : messaggio);
	}

	private static String pulisci(String messaggio) {
		String causa = messaggio
				.replaceAll("data:[^\\s\"')]{20,}", "data:...")
				// percorso base dei documenti SVG (la cartella dei fogli), che Batik antepone ai messaggi
				.replaceAll("file:[^\\s\"']*?:-?\\d*(\\s|$)", "")
				.replaceAll("file:[^\\s\"']*/", "")
				.replaceAll("\\(See position [^)]*\\)", "")
				.replaceAll("\\s+", " ")
				.trim();
		return causa.length() > MAX_LUNGHEZZA_CAUSA ? causa.substring(0, MAX_LUNGHEZZA_CAUSA) + "..." : causa;
	}

	/**
	 * Eventi di FOP: quelli di severita' ERROR o FATAL (immagine non trovata, SVG non costruibile) sono
	 * raccolti, per decidere se ristampare senza un logo; gli altri vanno subito nel log come fa FOP quando
	 * non ci sono ascoltatori, per esempio gli avvisi di testo che non sta nel riquadro.
	 */
	private static final class ErroriFop implements EventListener {
		private final LoggingEventListener log = new LoggingEventListener();
		private final List<Event> errori = new ArrayList<>();

		@Override
		public synchronized void processEvent(Event event) {
			if (event.getSeverity() == EventSeverity.ERROR || event.getSeverity() == EventSeverity.FATAL) {
				this.errori.add(event);
			} else {
				this.log.processEvent(event);
			}
		}

		synchronized boolean nessuno() {
			return this.errori.isEmpty();
		}

		synchronized String causa() {
			Event evento = this.errori.get(0);
			// "Image not found" per una URI data: FOP non ha un lettore per quel contenuto
			return "imageNotFound".equals(evento.getEventKey()) ? IMMAGINE_NON_LEGGIBILE : pulisci(EventFormatter.format(evento));
		}

		/** Scrive nel log gli errori raccolti, come avrebbe fatto FOP. */
		synchronized void registra() {
			this.errori.forEach(this.log::processEvent);
			this.errori.clear();
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

	/** Risorse di FOP (i font della configurazione e le icone dei layout) lette solo dal classpath. */
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
