package it.govpay.stampe.fop;

import it.govpay.stampe.model.v1.AvvisoPagamentoInput;
import it.govpay.stampe.model.v1.PaginaAvvisoMultipla;
import it.govpay.stampe.model.v1.PaginaAvvisoSingola;

/**
 * Layout gia' migrati a XSL-FO (issue #37). Con il motore FOP configurato, un avviso che usa un
 * layout non ancora migrato viene stampato con JasperReports: la migrazione procede per layout
 * senza bloccare la stampa degli altri casi.
 */
public final class LayoutMigrati {

	private LayoutMigrati() {
	}

	/** Avviso semplice v1: migrate tutte le pagine (singola, doppia, tripla e multipla). */
	public static boolean avvisoSemplice(AvvisoPagamentoInput avviso) {
		return avviso.getPagine() != null && !avviso.getPagine().getSingolaOrDoppiaOrTripla().isEmpty();
	}

	/**
	 * Avviso con bollettino postale v1: migrate le pagine singola, doppia e tripla, le sole che il mapper
	 * produce per il postale. Le pagine multiple non hanno un layout postale.
	 */
	public static boolean avvisoPostale(AvvisoPagamentoInput avviso) {
		return avvisoSemplice(avviso)
				&& avviso.getPagine().getSingolaOrDoppiaOrTripla().stream().noneMatch(PaginaAvvisoMultipla.class::isInstance);
	}

	/**
	 * Violazione del codice della strada senza bollettino: migrato il layout Sanzione, per le pagine con
	 * importo ridotto e scontato, l'unico che il mapper produce. RidottoScontato (un solo importo) no.
	 */
	public static boolean violazioneCds(AvvisoPagamentoInput avviso) {
		return avvisoSemplice(avviso) && avviso.getPagine().getSingolaOrDoppiaOrTripla().stream()
				.allMatch(p -> p instanceof PaginaAvvisoSingola s && s.getRata() != null
						&& s.getRata().getImportoRidotto() != null && s.getRata().getImportoScontato() != null);
	}
}
