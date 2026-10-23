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
import it.govpay.stampe.fop.Motore;
import it.govpay.stampe.fop.MotoreFop;
import it.govpay.stampe.fop.PreparazioneAvviso;
import it.govpay.stampe.model.v2.AvvisoPagamentoInput;
import net.sf.jasperreports.engine.JRException;

@Service
public class AvvisoBilingueService extends AvvisoPagamentoService {
	
	private Resource templateAvvisoV2 = null;
	private Resource templateMonoBandV2 = null;
	private Resource templateRataUnicaV2 = null;
	private Resource templateDoppiaRataV2 = null;
	private Resource templateDoppioFormatoV2 = null;
	private Resource templateBollettinoRataV2 = null;
	private Resource templateDualBandV2 = null;
	
	@Value("${stampe.debugInputJasper:false}")
	private Boolean debugInputJasper;
	
	private String xmlRootName = Costanti.AVVISO_PAGAMENTO_ROOT_ELEMENT_NAME;

	private static final String PROPRIETA_MOTORE = "stampe.motore.avviso-bilingue";
	private static final String FOGLIO_FOP = "avviso-bilingue.xsl";

	@Value("${" + PROPRIETA_MOTORE + ":jasper}")
	private String motoreConfigurato;

	private Motore motore;

	private final MotoreFop motoreFop;

    @Autowired
    public AvvisoBilingueService(Jaxb2Marshaller jaxb2MarshallerAvvisoPagamentoBilingue, MotoreFop motoreFop) {
    	super(jaxb2MarshallerAvvisoPagamentoBilingue);
    	this.motoreFop = motoreFop;
    }
	
	@PostConstruct
	public void loadResources(){
		this.templateAvvisoV2 = new ClassPathResource(Costanti.AVVISO_PAGAMENTO_TEMPLATE_JASPER_V2);
		this.templateMonoBandV2 = new ClassPathResource(Costanti.MONOBAND_TEMPLATE_JASPER_V2);
		this.templateRataUnicaV2 = new ClassPathResource(Costanti.RATAUNICA_TEMPLATE_JASPER_V2);
		this.templateDoppiaRataV2 = new ClassPathResource(Costanti.RATADOPPIA_TEMPLATE_JASPER_V2);
		this.templateDoppioFormatoV2 = new ClassPathResource(Costanti.DOPPIOFORMATO_TEMPLATE_JASPER_V2);
		this.templateBollettinoRataV2 = new ClassPathResource(Costanti.BOLLETTINORATA_TEMPLATE_JASPER_V2);
		this.templateDualBandV2 = new ClassPathResource(Costanti.DUALBAND_TEMPLATE_JASPER_V2);
		this.motore = Motore.da(PROPRIETA_MOTORE, this.motoreConfigurato);
		if (this.motore == Motore.FOP) {
			this.motoreFop.prepara(FOGLIO_FOP);
		}
	}
	
	public byte[] creaAvviso(AvvisoPagamentoInput input) {
		if (this.motore == Motore.FOP) {
			return creaAvvisoConFop(input);
		}
		try {
			Map<String, Object> parameters = new HashMap<>();
			
			parameters.put("MonoBandV2", this.templateMonoBandV2.getInputStream());
			parameters.put("RataUnicaV2", this.templateRataUnicaV2.getInputStream());
			parameters.put("DoppiaRataV2", this.templateDoppiaRataV2.getInputStream());
			parameters.put("DoppioFormatoV2", this.templateDoppioFormatoV2.getInputStream());
			parameters.put("BollettinoRataV2", this.templateBollettinoRataV2.getInputStream());
			parameters.put("DualBandV2", this.templateDualBandV2.getInputStream());
			
			JAXBElement<AvvisoPagamentoInput> jaxbElement = new JAXBElement<>(new QName("", this.getXmlRootName()), AvvisoPagamentoInput.class, null, input);
			
			return creaAvvisoInner(jaxbElement, parameters, this.templateAvvisoV2, this.debugInputJasper);
		}catch (JAXBException | IOException | JRException e) {
			throw new GenerazioneAvvisoException(e);
		}
	}
	
	/** Tutti i layout bilingue (pagine singole e doppie, con e senza bollettino) sono migrati a XSL-FO. */
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

