package it.govpay.stampe.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;

import it.govpay.stampe.costanti.Costanti;
import it.govpay.stampe.exception.GenerazioneRicevutaException;
import it.govpay.stampe.fop.MotoreFop;
import it.govpay.stampe.fop.PreparazioneAvviso;
import it.govpay.stampe.model.v1.RicevutaTelematicaInput;
import jakarta.annotation.PostConstruct;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;

@Service
public class RicevutaService {

	private Logger logger = LoggerFactory.getLogger(RicevutaService.class);

	private final Jaxb2Marshaller jaxb2Marshaller;

	private String xmlRootName = Costanti.RICEVUTA_TELEMATICA_ROOT_ELEMENT_NAME;

	@Value("${stampe.debugInputJasper:false}")
	private Boolean debugInputJasper;

	private static final String FOGLIO_FOP = "ricevuta.xsl";

	private final MotoreFop motoreFop;

	@Autowired
	public RicevutaService(Jaxb2Marshaller jaxb2MarshallerAvvisoPagamento, MotoreFop motoreFop) {
		this.jaxb2Marshaller = jaxb2MarshallerAvvisoPagamento;
		this.motoreFop = motoreFop;
	}

	@PostConstruct
	public void loadResources(){
		this.motoreFop.prepara(FOGLIO_FOP);
	}

	public byte[] creaRicevuta(RicevutaTelematicaInput input) {
		try {
			PreparazioneAvviso.completa(input);
			JAXBElement<RicevutaTelematicaInput> jaxbElement = new JAXBElement<>(new QName("", this.getXmlRootName()), RicevutaTelematicaInput.class, null, input);
			if(Boolean.TRUE.equals(this.debugInputJasper)) {
				try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
					jaxb2Marshaller.createMarshaller().marshal(jaxbElement, baos);
					logger.debug(Costanti.LOG_MSG_AVVISO_PAGAMENTO_INPUT, baos.toString(StandardCharsets.UTF_8));
				}
			}
			return this.motoreFop.genera(FOGLIO_FOP, jaxb2Marshaller.getJaxbContext(), jaxbElement);
		} catch (JAXBException | IOException | IllegalStateException e) {
			throw new GenerazioneRicevutaException(e);
		}
	}

	private String getXmlRootName() {
		return this.xmlRootName;
	}
}
