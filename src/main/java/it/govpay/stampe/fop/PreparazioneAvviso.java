package it.govpay.stampe.fop;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;

import com.google.zxing.WriterException;

import it.govpay.stampe.model.v1.AvvisoPagamentoInput;
import it.govpay.stampe.model.v1.PaginaAvvisoDoppia;
import it.govpay.stampe.model.v1.PaginaAvvisoMultipla;
import it.govpay.stampe.model.v1.PaginaAvvisoSingola;
import it.govpay.stampe.model.v1.PaginaAvvisoTripla;
import it.govpay.stampe.model.v1.RataAvviso;

/**
 * Completa l'avviso con i dati che XSLT non puo' calcolare, prima della stampa con FOP (issue #37):
 * <ul>
 * <li>{@code qr_code_svg} e {@code data_matrix_svg} di ogni rata, come URI data: SVG, dal
 * {@code qr_code} e dal {@code data_matrix} del bollettino postale. Il contenuto del DataMatrix e'
 * usato cosi' com'e', spazi compresi: fanno parte del tracciato;</li>
 * <li>{@code logo_ente_px} e {@code logo_ente_secondario_px}: dimensioni in pixel dei loghi, per
 * esempio "500 572". Servono al layout per posizionare l'immagine come Jasper, che con scaleImage
 * RetainShape tronca a un numero intero di punti lo spazio libero nel riquadro. Un logo non
 * decodificabile resta senza dimensioni: il layout lo scala nel riquadro senza troncamenti.</li>
 * </ul>
 */
public final class PreparazioneAvviso {

	private PreparazioneAvviso() {
	}

	public static void completa(AvvisoPagamentoInput avviso) {
		avviso.setLogoEntePx(dimensioni(avviso.getLogoEnte()));
		avviso.setLogoEnteSecondarioPx(dimensioni(avviso.getLogoEnteSecondario()));
		try {
			for (RataAvviso rata : rate(avviso)) {
				if (rata.getQrCode() != null && !rata.getQrCode().isBlank()) {
					rata.setQrCodeSvg(CodiciSvg.qrCodeDataUri(rata.getQrCode().trim()));
				}
				if (rata.getDataMatrix() != null && !rata.getDataMatrix().isBlank()) {
					rata.setDataMatrixSvg(CodiciSvg.dataMatrixDataUri(rata.getDataMatrix()));
				}
			}
		} catch (WriterException e) {
			throw new IllegalStateException("Generazione del QR code non riuscita", e);
		}
	}

	/** Tutte le rate dell'avviso, compresa la rata unica delle pagine doppia e tripla. */
	static List<RataAvviso> rate(AvvisoPagamentoInput avviso) {
		List<RataAvviso> rate = new ArrayList<>();
		if (avviso.getPagine() == null) {
			return rate;
		}
		for (Object pagina : avviso.getPagine().getSingolaOrDoppiaOrTripla()) {
			if (pagina instanceof PaginaAvvisoSingola p) {
				aggiungi(rate, p.getRata());
			} else if (pagina instanceof PaginaAvvisoDoppia p) {
				aggiungi(rate, p.getUnica());
				p.getRata().forEach(r -> aggiungi(rate, r));
			} else if (pagina instanceof PaginaAvvisoTripla p) {
				aggiungi(rate, p.getUnica());
				p.getRata().forEach(r -> aggiungi(rate, r));
			} else if (pagina instanceof PaginaAvvisoMultipla p) {
				p.getRata().forEach(r -> aggiungi(rate, r));
			}
		}
		return rate;
	}

	private static void aggiungi(List<RataAvviso> rate, RataAvviso rata) {
		if (rata != null) {
			rate.add(rata);
		}
	}

	private static String dimensioni(String logo) {
		String valore = logo == null ? "" : logo.trim();
		if (valore.isEmpty()) {
			return null;
		}
		try {
			byte[] dati = Base64.getMimeDecoder().decode(valore.contains(",") ? valore.substring(valore.indexOf(',') + 1) : valore);
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(dati));
			return img == null ? null : img.getWidth() + " " + img.getHeight();
		} catch (IOException | IllegalArgumentException e) {
			// logo non leggibile: nessuna dimensione, come per un logo assente
			return null;
		}
	}
}
