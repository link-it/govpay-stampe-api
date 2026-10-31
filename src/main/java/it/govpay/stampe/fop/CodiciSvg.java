package it.govpay.stampe.fop;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Dimension;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.datamatrix.DataMatrixWriter;
import com.google.zxing.datamatrix.encoder.SymbolShapeHint;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;

/**
 * QR code e DataMatrix dell'avviso come SVG vettoriale, da passare al layout XSL-FO (issue #37).
 * Entrambi senza zona di rispetto: il layout stampa il simbolo a 25 mm di lato e lascia bianco attorno.
 * <ul>
 * <li>QR code: versione 4 (33x33 moduli), correzione d'errore M, come da specifica pagoPA dell'avviso
 * (dati per il pagamento, codice QR). Un contenuto che non entra nella versione 4 usa la versione minima
 * sufficiente, sempre con correzione M.</li>
 * <li>DataMatrix del bollettino postale: ECC 200 quadrato 52x52, come da "Manuale della stampa in proprio"
 * di Poste Italiane (Bollettino Postale PA).</li>
 * </ul>
 * Le righe identiche e consecutive sono fuse in un unico rettangolo, per tenere l'SVG compatto.
 */
public final class CodiciSvg {

	static final int VERSIONE_QR = 4;
	static final int MODULI_DATA_MATRIX = 52;

	private CodiciSvg() {
	}

	public static String qrCodeDataUri(String contenuto) throws WriterException {
		return dataUri(svg(qrCode(contenuto)));
	}

	static BitMatrix qrCode(String contenuto) throws WriterException {
		ByteMatrix m;
		try {
			m = Encoder.encode(contenuto, ErrorCorrectionLevel.M, Map.of(EncodeHintType.QR_VERSION, VERSIONE_QR)).getMatrix();
		} catch (WriterException e) {
			// contenuto oltre la capacita' della versione 4: GovPay non lo produce, ma la stampa non deve fallire
			m = Encoder.encode(contenuto, ErrorCorrectionLevel.M).getMatrix();
		}
		BitMatrix b = new BitMatrix(m.getWidth(), m.getHeight());
		for (int y = 0; y < m.getHeight(); y++) {
			for (int x = 0; x < m.getWidth(); x++) {
				if (m.get(x, y) == 1) {
					b.set(x, y);
				}
			}
		}
		return b;
	}

	public static String dataMatrixDataUri(String contenuto) {
		return dataUri(svg(dataMatrix(contenuto)));
	}

	@SuppressWarnings("deprecation") // MIN_SIZE e' l'unico modo di zxing per chiedere un formato minimo
	static BitMatrix dataMatrix(String contenuto) {
		Dimension formato = new Dimension(MODULI_DATA_MATRIX, MODULI_DATA_MATRIX);
		return new DataMatrixWriter().encode(contenuto, BarcodeFormat.DATA_MATRIX, 0, 0,
				Map.of(EncodeHintType.DATA_MATRIX_SHAPE, SymbolShapeHint.FORCE_SQUARE, EncodeHintType.MIN_SIZE, formato));
	}

	static String svg(BitMatrix m) {
		int w = m.getWidth();
		int h = m.getHeight();
		StringBuilder sb = new StringBuilder(8192);
		sb.append("<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 ").append(w).append(' ').append(h)
				.append("' width='").append(w).append("' height='").append(h)
				.append("' preserveAspectRatio='none' shape-rendering='crispEdges'><path fill='#000000' d='");
		int y = 0;
		while (y < h) {
			int altezza = 1;
			while (y + altezza < h && rigaUguale(m, y, y + altezza, w)) {
				altezza++;
			}
			int x = 0;
			while (x < w) {
				if (m.get(x, y)) {
					int inizio = x;
					while (x < w && m.get(x, y)) {
						x++;
					}
					sb.append('M').append(inizio).append(' ').append(y)
							.append('h').append(x - inizio).append('v').append(altezza)
							.append('h').append(inizio - x).append('z');
				} else {
					x++;
				}
			}
			y += altezza;
		}
		return sb.append("'/></svg>").toString();
	}

	private static String dataUri(String svg) {
		return "data:image/svg+xml;base64," + Base64.getEncoder().encodeToString(svg.getBytes(StandardCharsets.UTF_8));
	}

	private static boolean rigaUguale(BitMatrix m, int a, int b, int w) {
		for (int x = 0; x < w; x++) {
			if (m.get(x, a) != m.get(x, b)) {
				return false;
			}
		}
		return true;
	}
}
