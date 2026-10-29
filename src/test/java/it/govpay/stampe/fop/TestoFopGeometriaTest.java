package it.govpay.stampe.fop;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Geometria dei testi di {@link TestoFop}: valori dei layout generati prima che fossero calcolati a runtime
 * (titolo dell'avviso, TitilliumWeb-Bold 16 pt con interlinea Fixed 11 in un riquadro di 42 pt) e misure dei
 * PDF Jasper della ricevuta.
 */
class TestoFopGeometriaTest {

	@Test
	void interlineaFissaDipendeDalleRighe() {
		assertEquals("24.336pt", TestoFop.interlinea(1, 5, "TitilliumWeb-Bold", 16, 11));
		assertEquals("20.772pt", TestoFop.interlinea(2, 5, "TitilliumWeb-Bold", 16, 11));
		assertEquals("18.634pt", TestoFop.interlinea(5, 5, "TitilliumWeb-Bold", 16, 11));
		// oltre il limite resta la geometria del limite
		assertEquals("18.634pt", TestoFop.interlinea(7, 5, "TitilliumWeb-Bold", 16, 11));
	}

	@Test
	void paddingDelTesto() {
		assertEquals("2.008pt", TestoFop.paddingTesto(1, 5, "TitilliumWeb-Bold", 16, 11, "Top", 42, 0));
		assertEquals("3.79pt", TestoFop.paddingTesto(2, 5, "TitilliumWeb-Bold", 16, 11, "Top", 42, 0));
		assertEquals("4.859pt", TestoFop.paddingTesto(6, 5, "TitilliumWeb-Bold", 16, 11, "Top", 42, 0));
	}

	@Test
	void allungamentoDellaRicevuta() {
		// oggetto 14 pt Fixed 12 in 22 pt: su 2 righe il riquadro e' alto 39 pt
		assertEquals(0, TestoFop.allungamento("una riga", "TitilliumWeb", 14, 12, 0, 22));
		assertEquals(17, TestoFop.allungamento("prima\nseconda", "TitilliumWeb", 14, 12, 0, 22));
		assertEquals(3, TestoFop.numeroRighe("a\nb\nc"));
	}
}
