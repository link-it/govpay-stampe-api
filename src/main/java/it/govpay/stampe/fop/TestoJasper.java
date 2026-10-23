package it.govpay.stampe.fop;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A capo dei testi variabili come JasperReports (issue #37), per i layout XSL-FO: richiamata dai fogli
 * XSLT come funzione di estensione, restituisce le righe del testo separate da '\n'.
 * <p>
 * Le righe sono calcolate come in Jasper e come per i testi fissi in {@code script/fop/SpezzaRighe.java}:
 * {@link LineBreakMeasurer} con il FontRenderContext di Jasper (antialiasing e metriche frazionarie), gli
 * stessi file di font e la larghezza utile del riquadro. Un riquadro che non si allunga mostra al massimo
 * {@code maxRighe} righe: le altre sono scartate e, con {@code net.sf.jasperreports.text.truncate.at.char},
 * l'ultima riga visibile e' riempita carattere per carattere con il testo rimanente.
 * <p>
 * {@link #stile} fa lo stesso per i testi con markup {@code styled} o {@code html} composti a runtime, per
 * esempio l'etichetta italiana seguita dalla traduzione in grigio e in un corpo minore degli avvisi
 * bilingue: interpreta i tag come Jasper e spezza le righe sul testo con i font di ogni tratto.
 */
public final class TestoJasper {

	private static final FontRenderContext FRC = new FontRenderContext(null, true, true);
	private static final Map<String, Font> FONT = new ConcurrentHashMap<>();
	private static final String NS_FO = "http://www.w3.org/1999/XSL/Format";

	/**
	 * File delle famiglie di font, come nell'estensione dei font di Jasper
	 * ({@code fonts/fontsfamily1620136860820.xml}): normale, grassetto, corsivo, grassetto corsivo. Una famiglia
	 * senza la variante usa la normale, e Jasper simula grassetto e corsivo senza cambiare la larghezza.
	 */
	private static final Map<String, String[]> FAMIGLIE = Map.of(
			"TitilliumWeb", new String[] { "TitilliumWeb-Regular.ttf", "TitilliumWeb-SemiBold.ttf", "TitilliumWeb-Italic.ttf", "TitilliumWeb-SemiBoldItalic.ttf" },
			"TitilliumWeb-Bold", new String[] { "TitilliumWeb-Bold.ttf", null, null, null },
			"TitilliumWeb-Black", new String[] { "TitilliumWeb-Black.ttf", null, null, null },
			"RobotoMono", new String[] { "RobotoMono-Regular.ttf", "RobotoMono-Bold.ttf", "RobotoMono-Italic.ttf", "RobotoMono-BoldItalic.ttf" },
			"Roboto", new String[] { "Roboto-Regular.ttf", null, null, null },
			"Roboto-Bold", new String[] { "Roboto-Bold.ttf", null, null, null });

	private static final Pattern TAG = Pattern.compile("<(/?)([a-zA-Z]+)([^>]*?)(/?)>");
	private static final Pattern ATTRIBUTO = Pattern.compile("([a-zA-Z-]+)\\s*=\\s*\"([^\"]*)\"");
	private static final Pattern ENTITA = Pattern.compile("&(#\\d+|#x[0-9a-fA-F]+|amp|lt|gt|quot|apos|nbsp);");

	/** Stile di un tratto di testo. */
	private record Stile(String famiglia, boolean grassetto, boolean corsivo, double corpo, String colore) {

		Font font() {
			String[] file = FAMIGLIE.getOrDefault(this.famiglia, FAMIGLIE.get("TitilliumWeb"));
			int variante = (this.grassetto ? 1 : 0) + (this.corsivo ? 2 : 0);
			String scelto = file[variante] != null ? file[variante] : file[this.grassetto ? 1 : 0] != null ? file[this.grassetto ? 1 : 0] : file[0];
			return FONT.computeIfAbsent(scelto, TestoJasper::carica).deriveFont((float) this.corpo);
		}
	}

	/** Tratto di testo con il suo stile. */
	private record Tratto(String testo, Stile stile) {
	}

	private TestoJasper() {
	}

	/**
	 * @param testo testo da spezzare (le righe gia' presenti, separate da '\n', restano)
	 * @param fileFont file del font in {@code fonts/} nel classpath, per esempio {@code TitilliumWeb-Bold.ttf}
	 * @param corpo corpo in punti
	 * @param larghezza larghezza utile del riquadro in punti
	 * @param maxRighe righe che stanno nel riquadro, 0 senza limite
	 * @param troncaCarattere se l'ultima riga visibile va riempita carattere per carattere
	 */
	public static String righe(String testo, String fileFont, double corpo, double larghezza, double maxRighe, boolean troncaCarattere) {
		if (testo == null || testo.isEmpty()) {
			return "";
		}
		Font font = FONT.computeIfAbsent(fileFont, TestoJasper::carica).deriveFont((float) corpo);
		float w = (float) larghezza;
		int limite = (int) maxRighe;
		List<String> righe = new ArrayList<>();
		String[] paragrafi = testo.split("\n", -1);
		for (int p = 0; p < paragrafi.length; p++) {
			String paragrafo = paragrafi[p];
			if (paragrafo.isEmpty()) {
				righe.add("");
				continue;
			}
			AttributedString as = new AttributedString(paragrafo);
			as.addAttribute(TextAttribute.FONT, font);
			LineBreakMeasurer lbm = new LineBreakMeasurer(as.getIterator(), FRC);
			while (lbm.getPosition() < paragrafo.length()) {
				int inizio = lbm.getPosition();
				if (limite > 0 && righe.size() == limite - 1) {
					// ultima riga visibile: il resto del testo, se non ci sta, si tronca
					StringBuilder resto = new StringBuilder(paragrafo.substring(inizio));
					for (int q = p + 1; q < paragrafi.length; q++) {
						resto.append('\n').append(paragrafi[q]);
					}
					righe.add(ultimaRiga(resto.toString().stripLeading(), font, w, troncaCarattere));
					return String.join("\n", righe);
				}
				lbm.nextLayout(w);
				righe.add(paragrafo.substring(inizio, lbm.getPosition()).stripTrailing());
			}
		}
		return String.join("\n", righe);
	}

	/**
	 * A capo di un testo con markup composto a runtime, come Jasper: restituisce le righe come nodi XSL-FO,
	 * testo e {@code fo:inline} per i tratti con uno stile diverso da quello del riquadro, separate da '\n'.
	 *
	 * @param testo testo con il markup
	 * @param markup {@code styled} o {@code html}
	 * @param famiglia famiglia di font del riquadro, per esempio {@code TitilliumWeb}
	 * @param grassetto se il riquadro e' in grassetto
	 * @param corsivo se il riquadro e' in corsivo
	 * @param corpo corpo del riquadro in punti
	 * @param colore colore del riquadro, per esempio {@code #000000}, vuoto se non indicato
	 * @param larghezza larghezza utile del riquadro in punti
	 * @param maxRighe righe che stanno nel riquadro, 0 senza limite
	 * @param troncaCarattere se l'ultima riga visibile va riempita carattere per carattere
	 */
	public static NodeList stile(String testo, String markup, String famiglia, boolean grassetto, boolean corsivo, double corpo,
			String colore, double larghezza, double maxRighe, boolean troncaCarattere) {
		Stile base = new Stile(famiglia, grassetto, corsivo, corpo, colore == null ? "" : colore);
		List<List<Tratto>> righe = spezza(tratti(testo == null ? "" : testo, "html".equals(markup), base), (float) larghezza, (int) maxRighe, troncaCarattere);
		return nodi(righe, stileBlocco(base));
	}

	/**
	 * Stile del blocco XSL-FO che contiene le righe: per Titillium e' sempre TitilliumWeb normale. FOP allarga
	 * una riga quando un fo:inline usa un file di font con capHeight maggiore di quello del blocco (Regular 692,
	 * Bold 680): la traduzione in TitilliumWeb dentro un riquadro TitilliumWeb-Bold abbassava la riga di 0,24 pt
	 * e la successiva di 0,48 pt. Con il blocco in Regular, che ha la capHeight maggiore, i tratti nelle altre
	 * varianti non spostano la riga, e la posizione e' la stessa del blocco nel font del riquadro. Il layout
	 * generato da jrxml2fo.py usa lo stesso font per il blocco.
	 */
	private static Stile stileBlocco(Stile base) {
		if (!base.famiglia().startsWith("TitilliumWeb")) {
			return base;
		}
		return new Stile("TitilliumWeb", false, false, base.corpo(), base.colore());
	}

	/** Tratti del testo con il markup di Jasper: style, font, b, i, span, br e le entita'. */
	private static List<Tratto> tratti(String testo, boolean html, Stile base) {
		List<Tratto> tratti = new ArrayList<>();
		Deque<Stile> pila = new ArrayDeque<>();
		pila.push(base);
		Matcher m = TAG.matcher(testo);
		int pos = 0;
		while (m.find()) {
			aggiungi(tratti, testo.substring(pos, m.start()), pila.peek(), html);
			pos = m.end();
			String nome = m.group(2).toLowerCase(Locale.ROOT);
			if (nome.equals("br")) {
				tratti.add(new Tratto("\n", pila.peek()));
			} else if (!m.group(1).isEmpty()) {
				if (pila.size() > 1) {
					pila.pop();
				}
			} else if (m.group(4).isEmpty()) {
				pila.push(apri(pila.peek(), nome, m.group(3)));
			}
		}
		aggiungi(tratti, testo.substring(pos), pila.peek(), html);
		return tratti;
	}

	private static void aggiungi(List<Tratto> tratti, String testo, Stile stile, boolean html) {
		String t = entita(html ? testo.replaceAll("\\s+", " ") : testo);
		if (!t.isEmpty()) {
			tratti.add(new Tratto(t, stile));
		}
	}

	/** Stile dentro un tag: i tag sconosciuti mantengono lo stile corrente. */
	private static Stile apri(Stile s, String nome, String attributi) {
		Map<String, String> a = new java.util.HashMap<>();
		Matcher m = ATTRIBUTO.matcher(attributi);
		while (m.find()) {
			a.put(m.group(1), m.group(2));
		}
		String famiglia = s.famiglia();
		boolean grassetto = s.grassetto();
		boolean corsivo = s.corsivo();
		double corpo = s.corpo();
		String colore = s.colore();
		switch (nome) {
		case "b", "strong" -> grassetto = true;
		case "i", "em" -> corsivo = true;
		case "style" -> {
			famiglia = a.getOrDefault("fontName", famiglia);
			corpo = a.containsKey("size") ? Double.parseDouble(a.get("size")) : corpo;
			grassetto = a.containsKey("isBold") ? Boolean.parseBoolean(a.get("isBold")) : grassetto;
			corsivo = a.containsKey("isItalic") ? Boolean.parseBoolean(a.get("isItalic")) : corsivo;
			colore = a.getOrDefault("forecolor", colore);
		}
		case "font" -> {
			famiglia = a.getOrDefault("face", famiglia);
			corpo = a.containsKey("size") ? Double.parseDouble(a.get("size")) : corpo;
			colore = a.getOrDefault("color", colore);
		}
		case "span" -> {
			for (String regola : a.getOrDefault("style", "").split(";")) {
				String[] kv = regola.split(":", 2);
				if (kv.length == 2 && kv[0].trim().equalsIgnoreCase("color")) {
					colore = kv[1].trim();
				}
			}
		}
		default -> {
			// tag senza effetto sullo stile
		}
		}
		return new Stile(famiglia, grassetto, corsivo, corpo, colore);
	}

	private static String entita(String testo) {
		Matcher m = ENTITA.matcher(testo);
		StringBuilder sb = new StringBuilder();
		while (m.find()) {
			String e = m.group(1);
			String c = switch (e) {
			case "amp" -> "&";
			case "lt" -> "<";
			case "gt" -> ">";
			case "quot" -> "\"";
			case "apos" -> "'";
			case "nbsp" -> "\u00a0";
			default -> new String(Character.toChars(e.startsWith("#x") ? Integer.parseInt(e.substring(2), 16) : Integer.parseInt(e.substring(1))));
			};
			m.appendReplacement(sb, Matcher.quoteReplacement(c));
		}
		m.appendTail(sb);
		return sb.toString();
	}

	/** Righe dei tratti, spezzate con LineBreakMeasurer sul testo di ogni paragrafo con i font dei tratti. */
	private static List<List<Tratto>> spezza(List<Tratto> tratti, float larghezza, int limite, boolean troncaCarattere) {
		List<List<Tratto>> paragrafi = new ArrayList<>();
		paragrafi.add(new ArrayList<>());
		for (Tratto t : tratti) {
			String[] parti = t.testo().split("\n", -1);
			for (int i = 0; i < parti.length; i++) {
				if (i > 0) {
					paragrafi.add(new ArrayList<>());
				}
				if (!parti[i].isEmpty()) {
					paragrafi.get(paragrafi.size() - 1).add(new Tratto(parti[i], t.stile()));
				}
			}
		}
		List<List<Tratto>> righe = new ArrayList<>();
		for (int p = 0; p < paragrafi.size(); p++) {
			List<Tratto> paragrafo = paragrafi.get(p);
			String testo = paragrafo.stream().map(Tratto::testo).reduce("", String::concat);
			if (testo.isEmpty()) {
				righe.add(new ArrayList<>());
				continue;
			}
			AttributedString as = attribuito(paragrafo, testo);
			LineBreakMeasurer lbm = new LineBreakMeasurer(as.getIterator(), FRC);
			while (lbm.getPosition() < testo.length()) {
				int inizio = lbm.getPosition();
				boolean resta = p < paragrafi.size() - 1;
				if (limite > 0 && righe.size() == limite - 1) {
					// ultima riga visibile: il resto del paragrafo, se non ci sta, si tronca
					int fine = fineUltimaRiga(as, testo, inizio, larghezza, troncaCarattere, resta);
					righe.add(sottotratti(paragrafo, inizio, fine, true));
					return righe;
				}
				lbm.nextLayout(larghezza);
				righe.add(sottotratti(paragrafo, inizio, lbm.getPosition(), true));
			}
		}
		return righe;
	}

	private static AttributedString attribuito(List<Tratto> paragrafo, String testo) {
		AttributedString as = new AttributedString(testo);
		int pos = 0;
		for (Tratto t : paragrafo) {
			as.addAttribute(TextAttribute.FONT, t.stile().font(), pos, pos + t.testo().length());
			pos += t.testo().length();
		}
		return as;
	}

	private static int fineUltimaRiga(AttributedString as, String testo, int inizio, float larghezza, boolean troncaCarattere, boolean altriParagrafi) {
		if (!altriParagrafi && avanzamento(as, testo, inizio, testo.length()) <= larghezza) {
			return testo.length();
		}
		if (!troncaCarattere) {
			LineBreakMeasurer m = new LineBreakMeasurer(as.getIterator(null, inizio, testo.length()), FRC);
			m.setPosition(inizio);
			m.nextLayout(larghezza);
			return m.getPosition();
		}
		int basso = inizio;
		int alto = testo.length();
		while (basso < alto) {
			int medio = (basso + alto + 1) / 2;
			if (avanzamento(as, testo, inizio, medio) <= larghezza) {
				basso = medio;
			} else {
				alto = medio - 1;
			}
		}
		return basso;
	}

	private static float avanzamento(AttributedString as, String testo, int inizio, int fine) {
		int f = fine;
		while (f > inizio && Character.isWhitespace(testo.charAt(f - 1))) {
			f--;
		}
		if (f == inizio) {
			return 0;
		}
		AttributedCharacterIterator it = as.getIterator(null, inizio, f);
		return new TextLayout(it, FRC).getVisibleAdvance();
	}

	/** I tratti del paragrafo tra inizio e fine; gli spazi in fondo alla riga si tolgono, come per i testi semplici. */
	private static List<Tratto> sottotratti(List<Tratto> paragrafo, int inizio, int fine, boolean togliSpazi) {
		List<Tratto> riga = new ArrayList<>();
		int pos = 0;
		for (Tratto t : paragrafo) {
			int da = Math.max(inizio, pos);
			int a = Math.min(fine, pos + t.testo().length());
			if (da < a) {
				riga.add(new Tratto(t.testo().substring(da - pos, a - pos), t.stile()));
			}
			pos += t.testo().length();
		}
		while (togliSpazi && !riga.isEmpty()) {
			Tratto ultimo = riga.get(riga.size() - 1);
			String t = ultimo.testo().stripTrailing();
			if (!t.isEmpty()) {
				riga.set(riga.size() - 1, new Tratto(t, ultimo.stile()));
				break;
			}
			riga.remove(riga.size() - 1);
		}
		return riga;
	}

	private static NodeList nodi(List<List<Tratto>> righe, Stile base) {
		Document doc;
		try {
			DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
			f.setNamespaceAware(true);
			doc = f.newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			throw new IllegalStateException(e);
		}
		Element contenitore = doc.createElementNS(NS_FO, "fo:wrapper");
		doc.appendChild(contenitore);
		for (int r = 0; r < righe.size(); r++) {
			if (r > 0) {
				contenitore.appendChild(doc.createTextNode("\n"));
			}
			for (Tratto t : righe.get(r)) {
				Stile s = t.stile();
				if (s.equals(base)) {
					contenitore.appendChild(doc.createTextNode(t.testo()));
					continue;
				}
				Element inline = doc.createElementNS(NS_FO, "fo:inline");
				if (!s.famiglia().equals(base.famiglia())) {
					inline.setAttribute("font-family", s.famiglia());
				}
				if (s.grassetto() != base.grassetto()) {
					inline.setAttribute("font-weight", s.grassetto() ? "bold" : "normal");
				}
				if (s.corsivo() != base.corsivo()) {
					inline.setAttribute("font-style", s.corsivo() ? "italic" : "normal");
				}
				if (s.corpo() != base.corpo()) {
					inline.setAttribute("font-size", s.corpo() + "pt");
				}
				if (!Objects.equals(s.colore(), base.colore()) && !s.colore().isEmpty()) {
					inline.setAttribute("color", s.colore());
				}
				inline.appendChild(doc.createTextNode(t.testo()));
				contenitore.appendChild(inline);
			}
		}
		return contenitore.getChildNodes();
	}

	private static String ultimaRiga(String resto, Font font, float larghezza, boolean troncaCarattere) {
		String paragrafo = resto.contains("\n") ? resto.substring(0, resto.indexOf('\n')) : resto;
		if (!resto.contains("\n") && avanzamento(paragrafo, font) <= larghezza) {
			return paragrafo;
		}
		if (!troncaCarattere) {
			// troncamento a parola: la riga che Jasper avrebbe comunque spezzato
			AttributedString as = new AttributedString(paragrafo);
			as.addAttribute(TextAttribute.FONT, font);
			LineBreakMeasurer m = new LineBreakMeasurer(as.getIterator(), FRC);
			m.nextLayout(larghezza);
			return paragrafo.substring(0, m.getPosition()).stripTrailing();
		}
		// troncamento a carattere: il prefisso piu' lungo che sta nella larghezza
		int basso = 0;
		int alto = paragrafo.length();
		while (basso < alto) {
			int medio = (basso + alto + 1) / 2;
			if (avanzamento(paragrafo.substring(0, medio), font) <= larghezza) {
				basso = medio;
			} else {
				alto = medio - 1;
			}
		}
		return paragrafo.substring(0, basso).stripTrailing();
	}

	private static float avanzamento(String testo, Font font) {
		String visibile = testo.stripTrailing();
		return visibile.isEmpty() ? 0 : new TextLayout(visibile, font, FRC).getVisibleAdvance();
	}

	private static Font carica(String fileFont) {
		try (InputStream is = MotoreFop.apri("fonts/" + fileFont)) {
			return Font.createFont(Font.TRUETYPE_FONT, is);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		} catch (FontFormatException e) {
			throw new IllegalStateException("Font non valido: " + fileFont, e);
		}
	}
}
