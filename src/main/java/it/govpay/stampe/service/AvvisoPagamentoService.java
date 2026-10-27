package it.govpay.stampe.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import it.govpay.stampe.costanti.Costanti;
import it.govpay.stampe.fop.MotoreFop;

public abstract class AvvisoPagamentoService {
	
	private Logger logger = LoggerFactory.getLogger(AvvisoPagamentoService.class);

	private final Jaxb2Marshaller jaxb2Marshaller;
	
	protected abstract String getXmlRootName();
	
	protected AvvisoPagamentoService(Jaxb2Marshaller jaxb2Marshaller) {
        this.jaxb2Marshaller = jaxb2Marshaller;
    }

	/**
	 * Stampa con FOP: l'oggetto JAXB e' passato direttamente alla trasformazione XSLT, senza
	 * serializzarlo. Con il debug attivo l'XML di input viene scritto nel log.
	 */
	protected byte[] creaAvvisoFop(MotoreFop motoreFop, String foglio, JAXBElement<?> jaxbElement, boolean debug) throws JAXBException {
		if(debug) {
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
				jaxb2Marshaller.createMarshaller().marshal(jaxbElement, baos);
				logger.debug(Costanti.LOG_MSG_AVVISO_PAGAMENTO_INPUT, baos.toString(StandardCharsets.UTF_8));
			} catch (IOException e) {
				// ByteArrayOutputStream non lancia eccezioni in chiusura
			}
		}
		return motoreFop.genera(foglio, jaxb2Marshaller.getJaxbContext(), jaxbElement);
	}
}
