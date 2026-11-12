package it.govpay.stampe.fop;

import java.util.List;

import it.govpay.stampe.model.v1.RicevutaTelematicaInput;

/**
 * Loghi di un documento da stampare (issue #67): per ciascuno il campo della richiesta, il valore (URI data)
 * e come toglierlo dal modello, insieme alle sue dimensioni in pixel, se non si puo' stampare.
 * Va costruito dopo {@link PreparazioneAvviso#completa}, che valorizza le dimensioni.
 *
 * @param documento tipo di documento, per il log
 * @param ente codice fiscale dell'ente creditore, per il log
 */
public record LoghiDocumento(String documento, String ente, List<Logo> loghi) {

	/**
	 * @param campo nome del campo nel JSON della richiesta
	 * @param valore URI data del logo, {@code null} se il logo e' assente
	 * @param rimuovi toglie il logo e le sue dimensioni dal modello
	 */
	public record Logo(String campo, String valore, Runnable rimuovi) {}

	public static LoghiDocumento di(String documento, it.govpay.stampe.model.v1.AvvisoPagamentoInput avviso) {
		return new LoghiDocumento(documento, avviso.getCfEnte(), List.of(
				new Logo("first_logo", avviso.getLogoEnte(), () -> {
					avviso.setLogoEnte(null);
					avviso.setLogoEntePx(null);
				}),
				new Logo("second_logo", avviso.getLogoEnteSecondario(), () -> {
					avviso.setLogoEnteSecondario(null);
					avviso.setLogoEnteSecondarioPx(null);
				})));
	}

	public static LoghiDocumento di(String documento, it.govpay.stampe.model.v2.AvvisoPagamentoInput avviso) {
		return new LoghiDocumento(documento, avviso.getCfEnte(), List.of(
				new Logo("first_logo", avviso.getLogoEnte(), () -> {
					avviso.setLogoEnte(null);
					avviso.setLogoEntePx(null);
				}),
				new Logo("second_logo", avviso.getLogoEnteSecondario(), () -> {
					avviso.setLogoEnteSecondario(null);
					avviso.setLogoEnteSecondarioPx(null);
				})));
	}

	public static LoghiDocumento di(String documento, RicevutaTelematicaInput ricevuta) {
		return new LoghiDocumento(documento, ricevuta.getCfEnte(), List.of(
				new Logo("creditor_logo", ricevuta.getLogoEnte(), () -> {
					ricevuta.setLogoEnte(null);
					ricevuta.setLogoEntePx(null);
				}),
				new Logo("pagopa_logo", ricevuta.getLogoPagopa(), () -> {
					ricevuta.setLogoPagopa(null);
					ricevuta.setLogoPagopaPx(null);
				})));
	}
}
