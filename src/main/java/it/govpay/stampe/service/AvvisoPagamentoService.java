package it.govpay.stampe.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import it.govpay.stampe.costanti.Costanti;
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

public abstract class AvvisoPagamentoService {
	
	private Logger logger = LoggerFactory.getLogger(AvvisoPagamentoService.class);

	private final Jaxb2Marshaller jaxb2Marshaller;
	
	protected abstract String getXmlRootName();
	
	protected AvvisoPagamentoService(Jaxb2Marshaller jaxb2Marshaller) {
        this.jaxb2Marshaller = jaxb2Marshaller;
    }

	protected byte[] creaAvvisoInner(JAXBElement<?> jaxbElement, Map<String, Object> parameters, Resource templatePrincipale,

			boolean debug) throws JAXBException, IOException, JRException {
		JRGzipVirtualizer virtualizer = new JRGzipVirtualizer(50);
		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);

		try (InputStream templateIS = templatePrincipale.getInputStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();){

			DefaultJasperReportsContext defaultJasperReportsContext = DefaultJasperReportsContext.getInstance();

			JRPropertiesUtil.getInstance(defaultJasperReportsContext).setProperty(Costanti.PROPERTY_NAME_NET_SF_JASPERREPORTS_XPATH_EXECUTER_FACTORY, Costanti.PROPERTY_VALUE_NET_SF_JASPERREPORTS_ENGINE_UTIL_XML_JAXEN_X_PATH_EXECUTER_FACTORY);
			Marshaller marshaller = jaxb2Marshaller.createMarshaller();
			marshaller.marshal(jaxbElement, baos);
			byte[] byteArray = baos.toByteArray();

			if(debug) {
				String inputString = new String(byteArray);
				logger.debug(Costanti.LOG_MSG_AVVISO_PAGAMENTO_INPUT, inputString);
			}

			try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);){

				JRDataSource dataSource = new JRXmlDataSource(defaultJasperReportsContext, byteArrayInputStream,this.getXmlRootName());
				JasperReport jasperReport = (JasperReport) JRLoader.loadObject(defaultJasperReportsContext,templateIS);
				JasperPrint jasperPrint = JasperFillManager.getInstance(defaultJasperReportsContext).fill(jasperReport, parameters, dataSource);

				return JasperExportManager.getInstance(defaultJasperReportsContext).exportToPdf(jasperPrint);
			}finally {
				//donothing
			}
		}finally {
			//donothing
		}
	}
}
