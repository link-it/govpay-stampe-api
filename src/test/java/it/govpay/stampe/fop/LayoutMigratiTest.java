package it.govpay.stampe.fop;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import it.govpay.stampe.model.v1.AvvisoPagamentoInput;
import it.govpay.stampe.model.v1.PaginaAvvisoDoppia;
import it.govpay.stampe.model.v1.PaginaAvvisoMultipla;
import it.govpay.stampe.model.v1.PaginaAvvisoSingola;
import it.govpay.stampe.model.v1.PaginaAvvisoTripla;
import it.govpay.stampe.model.v1.PagineAvviso;

/** Scelta del motore per layout (issue #37): un avviso con pagine senza layout FOP resta su Jasper. */
class LayoutMigratiTest {

	private static AvvisoPagamentoInput avviso(Object... pagine) {
		AvvisoPagamentoInput a = new AvvisoPagamentoInput();
		a.setPagine(new PagineAvviso());
		for (Object p : pagine) {
			a.getPagine().getSingolaOrDoppiaOrTripla().add(p);
		}
		return a;
	}

	@Test
	void avvisoSempliceTuttiILayout() {
		assertTrue(LayoutMigrati.avvisoSemplice(avviso(new PaginaAvvisoSingola())));
		assertTrue(LayoutMigrati.avvisoSemplice(avviso(new PaginaAvvisoTripla(), new PaginaAvvisoMultipla())));
		assertFalse(LayoutMigrati.avvisoSemplice(avviso()));
	}

	@Test
	void avvisoPostaleSenzaPagineMultiple() {
		assertTrue(LayoutMigrati.avvisoPostale(avviso(new PaginaAvvisoSingola(), new PaginaAvvisoDoppia(), new PaginaAvvisoTripla())));
		assertFalse(LayoutMigrati.avvisoPostale(avviso(new PaginaAvvisoSingola(), new PaginaAvvisoMultipla())));
	}
}
