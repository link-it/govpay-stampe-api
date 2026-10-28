package it.govpay.stampe.fop;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Geometria dei testi di {@link TestoJasper}: valori dei layout generati prima che fossero calcolati a runtime
 * (titolo dell'avviso, TitilliumWeb-Bold 16 pt con interlinea Fixed 11 in un riquadro di 42 pt) e misure dei
 * PDF Jasper della ricevuta.
 */
class TestoJasperGeometriaTest {

	@Test
	void interlineaFissaDipendeDalleRighe() {
		assertEquals("24.336pt", TestoJasper.interlinea(1, 5, "TitilliumWeb-Bold", 16, 11));
		assertEquals("20.772pt", TestoJasper.interlinea(2, 5, "TitilliumWeb-Bold", 16, 11));
		assertEquals("18.634pt", TestoJasper.interlinea(5, 5, "TitilliumWeb-Bold", 16, 11));
		// oltre il limite resta la geometria del limite
		assertEquals("18.634pt", TestoJasper.interlinea(7, 5, "TitilliumWeb-Bold", 16, 11));
	}

	@Test
	void paddingDelTesto() {
		assertEquals("2.008pt", TestoJasper.paddingTesto(1, 5, "TitilliumWeb-Bold", 16, 11, "Top", 42, 0));
		assertEquals("3.79pt", TestoJasper.paddingTesto(2, 5, "TitilliumWeb-Bold", 16, 11, "Top", 42, 0));
		assertEquals("4.859pt", TestoJasper.paddingTesto(6, 5, "TitilliumWeb-Bold", 16, 11, "Top", 42, 0));
	}

	@Test
	void allungamentoDellaRicevuta() {
		// oggetto 14 pt Fixed 12 in 22 pt: su 2 righe il riquadro e' alto 39 pt
		assertEquals(0, TestoJasper.allungamento("una riga", "TitilliumWeb", 14, 12, 0, 22));
		assertEquals(17, TestoJasper.allungamento("prima\nseconda", "TitilliumWeb", 14, 12, 0, 22));
		assertEquals(3, TestoJasper.numeroRighe("a\nb\nc"));
	}
}
