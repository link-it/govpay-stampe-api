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
public class ViolazioneCdsService extends AvvisoPagamentoService{
	
	private Resource templateViolazioneCDS;
	private Resource templateRidottoScontato;
	private Resource templateSanzione;
	private Resource templateFormato;
	
	@Value("${stampe.debugInputJasper:false}")
	private Boolean debugInputJasper;
	
	private String xmlRootName = Costanti.VIOLAZIONE_CDS_ROOT_ELEMENT_NAME; 

    @Autowired
    public ViolazioneCdsService(Jaxb2Marshaller jaxb2MarshallerAvvisoPagamento) {
    	super(jaxb2MarshallerAvvisoPagamento);
    }
	
	@PostConstruct
	public void loadResources(){
		this.templateViolazioneCDS = new ClassPathResource(Costanti.VIOLAZIONE_CDS_TEMPLATE_JASPER);
		this.templateRidottoScontato = new ClassPathResource(Costanti.RIDOTTOSCONTATO_TEMPLATE_JASPER);
		this.templateSanzione = new ClassPathResource(Costanti.SANZIONE_TEMPLATE_JASPER);
		this.templateFormato = new ClassPathResource(Costanti.FORMATO_TEMPLATE_JASPER);
	}
	
	public byte[] creaAvviso(AvvisoPagamentoInput input) {
		try {
			Map<String, Object> parameters = new HashMap<>();
			
			parameters.put("RidottoScontato", this.templateRidottoScontato.getInputStream());
			parameters.put("Sanzione", this.templateSanzione.getInputStream());
			parameters.put("Formato", this.templateFormato.getInputStream());
			
			JAXBElement<AvvisoPagamentoInput> jaxbElement = new JAXBElement<>(new QName("", this.getXmlRootName()), AvvisoPagamentoInput.class, null, input);
			
			return creaAvvisoInner(jaxbElement, parameters, this.templateViolazioneCDS, this.debugInputJasper);
		}catch (JAXBException | IOException | JRException e) {
			throw new GenerazioneAvvisoException(e);
		}
	}
	
	@Override
	protected String getXmlRootName() {
		return this.xmlRootName;
	}
}

