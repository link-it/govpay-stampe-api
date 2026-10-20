package it.govpay.stampe.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;

import it.govpay.stampe.costanti.Costanti;
import it.govpay.stampe.exception.GenerazioneAvvisoException;
import it.govpay.stampe.fop.LayoutMigrati;
import it.govpay.stampe.fop.Motore;
import it.govpay.stampe.fop.MotoreFop;
import it.govpay.stampe.fop.PreparazioneAvviso;
import it.govpay.stampe.model.v1.AvvisoPagamentoInput;
import net.sf.jasperreports.engine.JRException;

@Service
public class AvvisoSempliceService extends AvvisoPagamentoService {

	private Resource templateAvviso = null;
	private Resource templateDoppiaRata = null;
	private Resource templateDoppioFormato = null;
	private Resource templateRataMultipla = null;
	private Resource templateRataUnica = null;
	private Resource templateTriplaRata = null;
	private Resource templateTriploFormato = null;

	@Value("${stampe.debugInputJasper:false}")
	private Boolean debugInputJasper;

	private static final String PROPRIETA_MOTORE = "stampe.motore.avviso-semplice";
	private static final String FOGLIO_FOP = "avviso-semplice.xsl";

	@Value("${" + PROPRIETA_MOTORE + ":jasper}")
	private String motoreConfigurato;

	private Motore motore;

	private final MotoreFop motoreFop;

	private String xmlRootName = Costanti.AVVISO_PAGAMENTO_ROOT_ELEMENT_NAME; 

	@Autowired
	public AvvisoSempliceService(Jaxb2Marshaller jaxb2MarshallerAvvisoPagamento, MotoreFop motoreFop) {
		super(jaxb2MarshallerAvvisoPagamento);
		this.motoreFop = motoreFop;
	}

	@PostConstruct
	public void loadResources(){
		this.templateAvviso = new ClassPathResource(Costanti.AVVISO_PAGAMENTO_TEMPLATE_JASPER);
		this.templateDoppiaRata = new ClassPathResource(Costanti.RATA_DOPPIA_TEMPLATE_JASPER);
		this.templateDoppioFormato = new ClassPathResource(Costanti.DOPPIO_FORMATO_TEMPLATE_JASPER);
		this.templateRataMultipla = new ClassPathResource(Costanti.RATA_MULTIPLA_TEMPLATE_JASPER);
		this.templateRataUnica = new ClassPathResource(Costanti.RATA_UNICA_TEMPLATE_JASPER);
		this.templateTriplaRata = new ClassPathResource(Costanti.RATA_TRIPLA_TEMPLATE_JASPER);
		this.templateTriploFormato = new ClassPathResource(Costanti.TRIPLO_FORMATO_TEMPLATE_JASPER);
		this.motore = Motore.da(PROPRIETA_MOTORE, this.motoreConfigurato);
		if (this.motore == Motore.FOP) {
			this.motoreFop.prepara(FOGLIO_FOP);
		}
	}

	public byte[] creaAvviso(AvvisoPagamentoInput input) {
		if (this.motore == Motore.FOP && LayoutMigrati.avvisoSemplice(input)) {
			return creaAvvisoConFop(input);
		}
		try {
			Map<String, Object> parameters = new HashMap<>();

			parameters.put("DoppiaRata", this.templateDoppiaRata.getInputStream());
			parameters.put("DoppioFormato", this.templateDoppioFormato.getInputStream());
			parameters.put("RataMultipla", this.templateRataMultipla.getInputStream());
			parameters.put("RataUnica", this.templateRataUnica.getInputStream());
			parameters.put("TriplaRata", this.templateTriplaRata.getInputStream());
			parameters.put("TriploFormato", this.templateTriploFormato.getInputStream());

			JAXBElement<AvvisoPagamentoInput> jaxbElement = new JAXBElement<>(new QName("", this.getXmlRootName()), AvvisoPagamentoInput.class, null, input);

			return creaAvvisoInner(jaxbElement, parameters, this.templateAvviso, this.debugInputJasper);
		}catch (JAXBException | IOException | JRException e) {
			throw new GenerazioneAvvisoException(e);
		}
	}

	private byte[] creaAvvisoConFop(AvvisoPagamentoInput input) {
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

