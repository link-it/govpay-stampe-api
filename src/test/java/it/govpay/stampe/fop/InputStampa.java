package it.govpay.stampe.fop;

import javax.xml.transform.Source;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;

import it.govpay.stampe.model.v1.AvvisoPagamentoInput;
import it.govpay.stampe.model.v1.RicevutaTelematicaInput;

/**
 * Lettura dell'XML di input di un foglio, come lo prepara il servizio: il modello dipende dal foglio
 * (v2 per l'avviso bilingue, la ricevuta per {@code ricevuta.xsl}, v1 per gli altri) e l'oggetto e'
 * completato da {@link PreparazioneAvviso}. La radice resta quella dell'XML: AvvisoPagamentoInput,
 * ViolazioneInput per la violazione CdS, root per la ricevuta.
 */
final class InputStampa {

	private InputStampa() {
	}

	static JAXBContext contesto(String foglio) throws JAXBException {
		return "avviso-bilingue.xsl".equals(foglio) ? JAXBContext.newInstance(it.govpay.stampe.model.v2.AvvisoPagamentoInput.class)
				: JAXBContext.newInstance(AvvisoPagamentoInput.class, RicevutaTelematicaInput.class);
	}

	static JAXBElement<?> leggi(String foglio, JAXBContext contesto, Source sorgente) throws JAXBException {
		if ("avviso-bilingue.xsl".equals(foglio)) {
			JAXBElement<it.govpay.stampe.model.v2.AvvisoPagamentoInput> v2 = contesto.createUnmarshaller()
					.unmarshal(sorgente, it.govpay.stampe.model.v2.AvvisoPagamentoInput.class);
			PreparazioneAvviso.completa(v2.getValue());
			return v2;
		}
		if ("ricevuta.xsl".equals(foglio)) {
			JAXBElement<RicevutaTelematicaInput> rt = contesto.createUnmarshaller().unmarshal(sorgente, RicevutaTelematicaInput.class);
			PreparazioneAvviso.completa(rt.getValue());
			return rt;
		}
		JAXBElement<AvvisoPagamentoInput> avviso = contesto.createUnmarshaller().unmarshal(sorgente, AvvisoPagamentoInput.class);
		PreparazioneAvviso.completa(avviso.getValue());
		return avviso;
	}
}
