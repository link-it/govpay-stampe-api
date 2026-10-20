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
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A capo dei testi variabili come JasperReports (issue #37), per i layout XSL-FO: richiamata dai fogli
 * XSLT come funzione di estensione, restituisce le righe del testo separate da '\n'.
 * <p>
 * Le righe sono calcolate come in Jasper e come per i testi fissi in {@code script/fop/SpezzaRighe.java}:
 * {@link LineBreakMeasurer} con il FontRenderContext di Jasper (antialiasing e metriche frazionarie), gli
 * stessi file di font e la larghezza utile del riquadro. Un riquadro che non si allunga mostra al massimo
 * {@code maxRighe} righe: le altre sono scartate e, con {@code net.sf.jasperreports.text.truncate.at.char},
 * l'ultima riga visibile e' riempita carattere per carattere con il testo rimanente.
 */
public final class TestoJasper {

	private static final FontRenderContext FRC = new FontRenderContext(null, true, true);
	private static final Map<String, Font> FONT = new ConcurrentHashMap<>();

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
