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
public class AvvisoPostaleService extends AvvisoPagamentoService {
	
	private Resource templateAvvisoPostale = null;
	private Resource templateBollettinoRataPostale = null;
	private Resource templateBollettinoTriRataPostale = null;
	private Resource templateDoppiaRataPostale = null;
	private Resource templateDoppioFormatoPostale = null;
	private Resource templateRataUnicaPostale = null;
	private Resource templateTriplaRataPostale = null;
	private Resource templateTriploFormatoPostale = null;
	
	@Value("${stampe.debugInputJasper:false}")
	private Boolean debugInputJasper;
	
	private String xmlRootName = Costanti.AVVISO_PAGAMENTO_ROOT_ELEMENT_NAME;

    @Autowired
    public AvvisoPostaleService(Jaxb2Marshaller jaxb2MarshallerAvvisoPagamento) {
    	super(jaxb2MarshallerAvvisoPagamento);
    }
	
	@PostConstruct
	public void loadResources(){
		templateAvvisoPostale = new ClassPathResource(Costanti.AVVISO_PAGAMENTO_POSTALE_TEMPLATE_JASPER); 
		templateBollettinoRataPostale = new ClassPathResource(Costanti.BOLLETTINO_RATA_TEMPLATE_JASPER);
		templateBollettinoTriRataPostale = new ClassPathResource(Costanti.BOLLETTINO_TRIRATA_TEMPLATE_JASPER);
		templateDoppiaRataPostale = new ClassPathResource(Costanti.RATA_DOPPIA_POSTALE_TEMPLATE_JASPER);
		templateDoppioFormatoPostale = new ClassPathResource(Costanti.DOPPIO_FORMATO_POSTALE_TEMPLATE_JASPER);
		templateRataUnicaPostale = new ClassPathResource(Costanti.RATA_UNICA_POSTALE_TEMPLATE_JASPER);
		templateTriplaRataPostale = new ClassPathResource(Costanti.RATA_TRIPLA_POSTALE_TEMPLATE_JASPER);
		templateTriploFormatoPostale = new ClassPathResource(Costanti.TRIPLO_FORMATO_POSTALE_TEMPLATE_JASPER);
	}
	
	public byte[] creaAvviso(AvvisoPagamentoInput input) {
		try {
			Map<String, Object> parameters = new HashMap<>();
			
			parameters.put("BollettinoRataPostale", this.templateBollettinoRataPostale.getInputStream());
			parameters.put("BollettinoTriRataPostale", this.templateBollettinoTriRataPostale.getInputStream());
			parameters.put("DoppiaRataPostalePostale", this.templateDoppiaRataPostale.getInputStream());
			parameters.put("DoppioFormatoPostale", this.templateDoppioFormatoPostale.getInputStream());
			parameters.put("RataUnicaPostalePostale", this.templateRataUnicaPostale.getInputStream());
			parameters.put("TriplaRataPostalePostale", this.templateTriplaRataPostale.getInputStream());
			parameters.put("TriploFormatoPostale", this.templateTriploFormatoPostale.getInputStream());
			
			JAXBElement<AvvisoPagamentoInput> jaxbElement = new JAXBElement<>(new QName("", this.getXmlRootName()), AvvisoPagamentoInput.class, null, input);
			
			return creaAvvisoInner(jaxbElement, parameters, this.templateAvvisoPostale, this.debugInputJasper);
		}catch (JAXBException | IOException | JRException e) {
			throw new GenerazioneAvvisoException(e);
		}
	}
	
	@Override
	protected String getXmlRootName() {
		return this.xmlRootName;
	}
}

