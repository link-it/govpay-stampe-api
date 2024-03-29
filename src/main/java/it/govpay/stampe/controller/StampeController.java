package it.govpay.stampe.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import it.govpay.stampe.api.DefaultApi;
import it.govpay.stampe.beans.CdsViolation;
import it.govpay.stampe.beans.PaymentNotice;
import it.govpay.stampe.config.LabelAvvisiConfiguration.LabelAvvisiProperties;
import it.govpay.stampe.mapper.AvvisoPagamentoBilingueMapperImpl;
import it.govpay.stampe.mapper.AvvisoPagamentoMapperImpl;
import it.govpay.stampe.mapper.ViolazioneCdsMapperImpl;
import it.govpay.stampe.model.v1.AvvisoPagamentoInput;
import it.govpay.stampe.service.AvvisoBilingueService;
import it.govpay.stampe.service.AvvisoPostaleService;
import it.govpay.stampe.service.AvvisoSempliceService;
import it.govpay.stampe.service.ViolazioneCdsService;

@Controller
public class StampeController implements DefaultApi{
	
	private static Logger logger = LoggerFactory.getLogger(StampeController.class);
	
	@Qualifier("labelAvvisiProperties")
	@Autowired
	LabelAvvisiProperties labelAvvisiProperties;
	
	@Autowired
	ViolazioneCdsMapperImpl violazioneCdsMapper;
	
	@Autowired
	AvvisoPagamentoMapperImpl avvisoPagamentoMapper;
	
	@Autowired
	AvvisoPagamentoBilingueMapperImpl avvisoPagamentoBilingueMapper;
	
	@Autowired
	ViolazioneCdsService violazioneCdsService;
	
	@Autowired
	AvvisoSempliceService avvisoSempliceService;
	
	@Autowired
	AvvisoPostaleService avvisoPostaleService;
	
	@Autowired
	AvvisoBilingueService avvisoBilingueService;

	@Override
	public ResponseEntity<Resource> cdsViolationPost(@Valid CdsViolation cdsViolation) {
		logger.info("Creazione avviso di violazione codice della strada ...");
		
		AvvisoPagamentoInput avvisoPagamentoInput = this.violazioneCdsMapper.toViolazioneAvvisoPagamentoInput(cdsViolation, labelAvvisiProperties);
		
		logger.debug("Conversione CdsViolation in AvvisoPagamentoInput completata, generazione del pdf...");
		
		byte[] creaAvviso = this.violazioneCdsService.creaAvviso(avvisoPagamentoInput);

        String nomePdf = this.violazioneCdsMapper.nomePdf(cdsViolation);
		logger.debug("Generazione del pdf [{}] completata.", nomePdf);
		
        ByteArrayResource resource = new ByteArrayResource(creaAvviso);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
		ContentDisposition contentDisposition = ContentDisposition.attachment().filename(nomePdf).build();
		headers.setContentDisposition(contentDisposition );
        headers.setContentLength(creaAvviso.length);
        
		logger.info("Creazione avviso di violazione codice della strada completata.");

        return ResponseEntity.created(null).headers(headers).body(resource);
	}

	@Override
	public ResponseEntity<Resource> standardPost(@Valid PaymentNotice paymentNotice) {
		logger.info("Creazione avviso standard ...");
		
		byte[] creaAvviso = null;
		String nomePdf = null;
		// attualmente c'e' una divisione dei template senza bilinguismo
		if(paymentNotice.getSecondLanguage() == null) {
			AvvisoPagamentoInput avvisoPagamentoInput = this.avvisoPagamentoMapper.toViolazioneAvvisoPagamentoInput(paymentNotice, labelAvvisiProperties);
			
			if(avvisoPagamentoInput.getDiPoste() != null) {
				logger.debug("Conversione PaymentNotice in AvvisoPagamentoInput completata, generazione del pdf con bollettino postale...");
				creaAvviso = this.avvisoPostaleService.creaAvviso(avvisoPagamentoInput);
			} else {
				logger.debug("Conversione PaymentNotice in AvvisoPagamentoInput completata, generazione del pdf senza bollettino postale...");
				creaAvviso = this.avvisoSempliceService.creaAvviso(avvisoPagamentoInput);
			}
			
			nomePdf = this.avvisoPagamentoMapper.nomePdf(paymentNotice);
		} else {
			it.govpay.stampe.model.v2.AvvisoPagamentoInput avvisoPagamentoInput = this.avvisoPagamentoBilingueMapper.toViolazioneAvvisoPagamentoInput(paymentNotice, labelAvvisiProperties);
			
			logger.debug("Conversione PaymentNotice in AvvisoPagamentoInput completata, generazione del pdf bilingue...");
			
			creaAvviso = this.avvisoBilingueService.creaAvviso(avvisoPagamentoInput);
			
			nomePdf = this.avvisoPagamentoBilingueMapper.nomePdf(paymentNotice);
		}
		
		logger.debug("Generazione del pdf [{}] completata.", nomePdf);
		
        ByteArrayResource resource = new ByteArrayResource(creaAvviso);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
		ContentDisposition contentDisposition = ContentDisposition.attachment().filename(nomePdf).build();
		headers.setContentDisposition(contentDisposition );
        headers.setContentLength(creaAvviso.length);
		
		logger.info("Creazione avviso standard completata.");
		
		 return ResponseEntity.created(null).headers(headers).body(resource);
	}
}
