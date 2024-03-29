package it.govpay.stampe.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class JasperConfig {

	@Bean
	public Jaxb2Marshaller jaxb2MarshallerAvvisoPagamento() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setPackagesToScan("it.govpay.stampe.model.v1");
		marshaller.setMarshallerProperties(Collections.singletonMap(javax.xml.bind.Marshaller.JAXB_FRAGMENT, true));
		return marshaller;
	}
	
	@Bean
	public Jaxb2Marshaller jaxb2MarshallerAvvisoPagamentoBilingue() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setPackagesToScan("it.govpay.stampe.model.v2");
		marshaller.setMarshallerProperties(Collections.singletonMap(javax.xml.bind.Marshaller.JAXB_FRAGMENT, true));
		return marshaller;
	}
}
