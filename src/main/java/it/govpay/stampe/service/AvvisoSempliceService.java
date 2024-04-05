package it.govpay.stampe.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;

import it.govpay.stampe.costanti.Costanti;
import it.govpay.stampe.exception.GenerazioneAvvisoException;
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
	
	private String xmlRootName = Costanti.AVVISO_PAGAMENTO_ROOT_ELEMENT_NAME; 

    @Autowired
    public AvvisoSempliceService(Jaxb2Marshaller jaxb2MarshallerAvvisoPagamento) {
    	super(jaxb2MarshallerAvvisoPagamento);
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
	}
	
	public byte[] creaAvviso(AvvisoPagamentoInput input) {
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
	
	@Override
	protected String getXmlRootName() {
		return this.xmlRootName;
	}
}

