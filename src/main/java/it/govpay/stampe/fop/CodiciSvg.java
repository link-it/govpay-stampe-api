package it.govpay.stampe.fop;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.datamatrix.DataMatrixWriter;
import com.google.zxing.datamatrix.encoder.SymbolShapeHint;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * QR code e DataMatrix dell'avviso come SVG vettoriale, da passare al layout XSL-FO (issue #37).
 * <ul>
 * <li>QR code: matrice calcolata come nei template Jasper
 * ({@code new QRCodeWriter().encode(qr_code, QR_CODE, 300, 300)}), con la stessa zona di rispetto:
 * stessa geometria una volta scalata nel riquadro.</li>
 * <li>DataMatrix del bollettino postale: simbolo quadrato senza zona di rispetto, che riempie il
 * riquadro come il componente barcode4j di Jasper. Dimensione del simbolo e contenuto coincidono;
 * la disposizione dei moduli puo' differire, perche' zxing e barcode4j scelgono codifiche diverse
 * per gli stessi dati.</li>
 * </ul>
 * Le righe identiche e consecutive sono fuse in un unico rettangolo, per tenere l'SVG compatto.
 */
public final class CodiciSvg {

	static final int DIMENSIONE_QR = 300;

	private CodiciSvg() {
	}

	public static String qrCodeDataUri(String contenuto) throws WriterException {
		return dataUri(svg(new QRCodeWriter().encode(contenuto, BarcodeFormat.QR_CODE, DIMENSIONE_QR, DIMENSIONE_QR)));
	}

	public static String dataMatrixDataUri(String contenuto) {
		BitMatrix m = new DataMatrixWriter().encode(contenuto, BarcodeFormat.DATA_MATRIX, 0, 0,
				Map.of(EncodeHintType.DATA_MATRIX_SHAPE, SymbolShapeHint.FORCE_SQUARE));
		return dataUri(svg(m));
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
