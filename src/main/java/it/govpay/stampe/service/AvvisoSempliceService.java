package it.govpay.stampe.service;

import jakarta.annotation.PostConstruct;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;

import it.govpay.stampe.costanti.Costanti;
import it.govpay.stampe.exception.GenerazioneAvvisoException;
import it.govpay.stampe.fop.MotoreFop;
import it.govpay.stampe.fop.PreparazioneAvviso;
import it.govpay.stampe.model.v1.AvvisoPagamentoInput;

@Service
public class AvvisoSempliceService extends AvvisoPagamentoService {

	@Value("${stampe.debugInputJasper:false}")
	private Boolean debugInputJasper;

	private static final String FOGLIO_FOP = "avviso-semplice.xsl";

	private final MotoreFop motoreFop;

	private String xmlRootName = Costanti.AVVISO_PAGAMENTO_ROOT_ELEMENT_NAME;

	@Autowired
	public AvvisoSempliceService(Jaxb2Marshaller jaxb2MarshallerAvvisoPagamento, MotoreFop motoreFop) {
		super(jaxb2MarshallerAvvisoPagamento);
		this.motoreFop = motoreFop;
	}

	@PostConstruct
	public void loadResources() {
		this.motoreFop.prepara(FOGLIO_FOP);
	}

	public byte[] creaAvviso(AvvisoPagamentoInput input) {
		try {
			PreparazioneAvviso.completa(input);
			JAXBElement<AvvisoPagamentoInput> jaxbElement = new JAXBElement<>(new QName("", this.getXmlRootName()), AvvisoPagamentoInput.class, null, input);
			return creaAvvisoFop(this.motoreFop, FOGLIO_FOP, jaxbElement, this.debugInputJasper);
		} catch (JAXBException | IllegalStateException e) {
			throw new GenerazioneAvvisoException(e);
		}
	}

	@Override
	protected String getXmlRootName() {
		return this.xmlRootName;
	}
}
