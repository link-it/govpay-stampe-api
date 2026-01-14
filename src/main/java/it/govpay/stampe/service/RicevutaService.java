package it.govpay.stampe.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;

import it.govpay.stampe.costanti.Costanti;
import it.govpay.stampe.exception.GenerazioneRicevutaException;
import it.govpay.stampe.model.v1.RicevutaTelematicaInput;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.fill.JRGzipVirtualizer;
import net.sf.jasperreports.engine.util.JRLoader;

@Service
public class RicevutaService {

	private Logger logger = LoggerFactory.getLogger(RicevutaService.class);

	private final Jaxb2Marshaller jaxb2Marshaller;

	private String xmlRootName = Costanti.RICEVUTA_TELEMATICA_ROOT_ELEMENT_NAME;

	@Value("${stampe.debugInputJasper:false}")
	private Boolean debugInputJasper;

	private Resource templateRicevuta = null;

	@Autowired
	public RicevutaService(Jaxb2Marshaller jaxb2MarshallerAvvisoPagamento) {
		this.jaxb2Marshaller = jaxb2MarshallerAvvisoPagamento;
	}

	@jakarta.annotation.PostConstruct
	public void loadResources(){
		templateRicevuta = new ClassPathResource(Costanti.RICEVUTA_TELEMATICA_TEMPLATE_JASPER);
	}

	public byte[] creaRicevuta(RicevutaTelematicaInput input) {
		Map<String, Object> parameters = new HashMap<>();

		JRGzipVirtualizer virtualizer = new JRGzipVirtualizer(50);
		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);

		try (InputStream templateIS = templateRicevuta.getInputStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();){

			DefaultJasperReportsContext defaultJasperReportsContext = DefaultJasperReportsContext.getInstance();

			JRPropertiesUtil.getInstance(defaultJasperReportsContext).setProperty(Costanti.PROPERTY_NAME_NET_SF_JASPERREPORTS_XPATH_EXECUTER_FACTORY, Costanti.PROPERTY_VALUE_NET_SF_JASPERREPORTS_ENGINE_UTIL_XML_JAXEN_X_PATH_EXECUTER_FACTORY);
			Marshaller marshaller = jaxb2Marshaller.createMarshaller();
			
			JAXBElement<RicevutaTelematicaInput> jaxbElement = new JAXBElement<>(new QName("", this.getXmlRootName()), RicevutaTelematicaInput.class, null, input);
			
			marshaller.marshal(jaxbElement, baos);
			byte[] byteArray = baos.toByteArray();

			if(Boolean.TRUE.equals(this.debugInputJasper)) {
				String inputString = new String(byteArray);
				logger.debug(Costanti.LOG_MSG_AVVISO_PAGAMENTO_INPUT, inputString);
			}

			return exportToPdf(parameters, templateIS, defaultJasperReportsContext, byteArray);
		}catch (JAXBException | IOException e) {
			throw new GenerazioneRicevutaException(e);
		}
	}

	private byte[] exportToPdf(Map<String, Object> parameters, InputStream templateIS,
			DefaultJasperReportsContext defaultJasperReportsContext, byte[] byteArray) {
		try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);){

			JRDataSource dataSource = new JRXmlDataSource(defaultJasperReportsContext, byteArrayInputStream, this.getXmlRootName());
			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(defaultJasperReportsContext,templateIS);
			JasperPrint jasperPrint = JasperFillManager.getInstance(defaultJasperReportsContext).fill(jasperReport, parameters, dataSource);

			return JasperExportManager.getInstance(defaultJasperReportsContext).exportToPdf(jasperPrint);

		}catch (IOException | JRException e) {
			throw new GenerazioneRicevutaException(e);
		}
	}

	private String getXmlRootName() {
		return this.xmlRootName;
	}
}
