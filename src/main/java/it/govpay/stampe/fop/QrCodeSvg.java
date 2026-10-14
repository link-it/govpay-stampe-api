package it.govpay.stampe.fop;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * QR code dell'avviso come SVG vettoriale.
 * <p>
 * La matrice e' calcolata come nei template Jasper
 * ({@code new QRCodeWriter().encode(qr_code, QR_CODE, 300, 300)}): stessa dimensione e stessa
 * zona di rispetto, quindi stessa geometria una volta scalata nel riquadro del layout.
 * Le righe di pixel identiche e consecutive sono fuse in un unico rettangolo, per tenere
 * l'SVG compatto.
 */
public final class QrCodeSvg {

	static final int DIMENSIONE = 300;

	private QrCodeSvg() {
	}

	public static String svg(String contenuto) throws WriterException {
		BitMatrix m = new QRCodeWriter().encode(contenuto, BarcodeFormat.QR_CODE, DIMENSIONE, DIMENSIONE);
		int w = m.getWidth();
		int h = m.getHeight();
		StringBuilder sb = new StringBuilder(8192);
		sb.append("<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 ").append(w).append(' ').append(h)
				.append("' width='").append(w).append("' height='").append(h)
				.append("' shape-rendering='crispEdges'><path fill='#000000' d='");
		int y = 0;
		while (y < h) {
			// righe uguali consecutive: un modulo del QR occupa piu' righe di pixel
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

	public static String dataUri(String contenuto) throws WriterException {
		return "data:image/svg+xml;base64,"
				+ Base64.getEncoder().encodeToString(svg(contenuto).getBytes(StandardCharsets.UTF_8));
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
