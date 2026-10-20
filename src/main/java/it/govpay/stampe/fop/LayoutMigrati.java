package it.govpay.stampe.fop;

import it.govpay.stampe.model.v1.AvvisoPagamentoInput;
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

	/** Avviso con bollettino postale v1: migrata la sola pagina singola (rata unica). */
	public static boolean avvisoPostale(AvvisoPagamentoInput avviso) {
		return avvisoSemplice(avviso)
				&& avviso.getPagine().getSingolaOrDoppiaOrTripla().stream().allMatch(PaginaAvvisoSingola.class::isInstance);
	}
}
