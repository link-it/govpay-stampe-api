import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Calcola gli a capo di un paragrafo come JasperReports (issue #37): LineBreakMeasurer di Java,
 * con lo stesso FontRenderContext usato da Jasper (antialiasing e metriche frazionarie), gli stessi
 * file di font e la stessa larghezza utile.
 * <p>
 * Usato da jrxml2fo.py per i testi fissi dei template. Una richiesta per riga su stdin:
 * {@code id TAB larghezza TAB run [RS run]...} dove ogni run e' {@code fileFont US corpo US testo}.
 * Per ogni richiesta scrive su stdout {@code id TAB posizioni} con le posizioni di a capo nel testo
 * concatenato dei run, separate da virgola (vuoto se il paragrafo sta su una riga).
 * <p>
 * Esecuzione: {@code java -Djava.awt.headless=true script/fop/SpezzaRighe.java DIRECTORY_FONT}
 */
public class SpezzaRighe {

	private static final FontRenderContext FRC = new FontRenderContext(null, true, true);
	private static final Map<String, Font> FONT = new HashMap<>();

	public static void main(String[] args) throws Exception {
		File dirFont = new File(args[0]);
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
		String riga;
		while ((riga = in.readLine()) != null) {
			if (riga.isEmpty()) {
				continue;
			}
			String[] campi = riga.split("\t", 3);
			float larghezza = Float.parseFloat(campi[1]);
			StringBuilder testo = new StringBuilder();
			List<Object[]> run = new ArrayList<>();
			for (String r : campi[2].split("\u001e")) {
				String[] f = r.split("\u001f", 3);
				Font font = font(dirFont, f[0]).deriveFont(Float.parseFloat(f[1]));
				run.add(new Object[] { testo.length(), testo.length() + f[2].length(), font });
				testo.append(f[2]);
			}
			System.out.println(campi[0] + "\t" + aCapo(testo.toString(), run, larghezza));
		}
	}

	private static String aCapo(String testo, List<Object[]> run, float larghezza) {
		if (testo.isEmpty()) {
			return "";
		}
		AttributedString as = new AttributedString(testo);
		for (Object[] r : run) {
			if ((int) r[0] < (int) r[1]) {
				as.addAttribute(TextAttribute.FONT, r[2], (int) r[0], (int) r[1]);
			}
		}
		LineBreakMeasurer lbm = new LineBreakMeasurer(as.getIterator(), FRC);
		List<String> posizioni = new ArrayList<>();
		while (lbm.getPosition() < testo.length()) {
			lbm.nextLayout(larghezza);
			if (lbm.getPosition() < testo.length()) {
				posizioni.add(Integer.toString(lbm.getPosition()));
			}
		}
		return String.join(",", posizioni);
	}

	private static Font font(File dir, String nome) throws Exception {
		Font f = FONT.get(nome);
		if (f == null) {
			f = Font.createFont(Font.TRUETYPE_FONT, new File(dir, nome));
			FONT.put(nome, f);
		}
		return f;
	}
}
