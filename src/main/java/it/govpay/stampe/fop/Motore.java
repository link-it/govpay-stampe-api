package it.govpay.stampe.fop;

import java.util.Locale;

/**
 * Motore di stampa di un tipo di avviso, scelto da configurazione con {@code stampe.motore.<tipo>}
 * (issue #37). Il default resta JasperReports finche' tutti i layout del tipo non sono migrati.
 */
public enum Motore {

	JASPER, FOP;

	public static Motore da(String proprieta, String valore) {
		try {
			return valueOf(valore.trim().toUpperCase(Locale.ROOT));
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new IllegalStateException("Valore non valido per " + proprieta + ": '" + valore + "' (ammessi: jasper, fop)", e);
		}
	}
}
