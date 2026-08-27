package it.govpay.stampe.controller;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import it.govpay.stampe.api.PaymentNoticeApi;
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
import it.govpay.stampe.validator.SemanticValidator;

@RestController
public class PaymentNoticeController implements PaymentNoticeApi {

	private static Logger logger = LoggerFactory.getLogger(PaymentNoticeController.class);

	private final LabelAvvisiProperties labelAvvisiProperties;

	private final ViolazioneCdsMapperImpl violazioneCdsMapper;

	private final AvvisoPagamentoMapperImpl avvisoPagamentoMapper;

	private final AvvisoPagamentoBilingueMapperImpl avvisoPagamentoBilingueMapper;

	private final ViolazioneCdsService violazioneCdsService;

	private final AvvisoSempliceService avvisoSempliceService;

	private final AvvisoPostaleService avvisoPostaleService;

	private final AvvisoBilingueService avvisoBilingueService;

	private final SemanticValidator semanticValidator;

	@Autowired
	public PaymentNoticeController(@Qualifier("labelAvvisiProperties") LabelAvvisiProperties labelAvvisiProperties,
			ViolazioneCdsMapperImpl violazioneCdsMapper,
			AvvisoPagamentoMapperImpl avvisoPagamentoMapper,
			AvvisoPagamentoBilingueMapperImpl avvisoPagamentoBilingueMapper,
			ViolazioneCdsService violazioneCdsService,
			AvvisoSempliceService avvisoSempliceService,
			AvvisoPostaleService avvisoPostaleService,
			AvvisoBilingueService avvisoBilingueService,
			SemanticValidator semanticValidator) {
		this.labelAvvisiProperties = labelAvvisiProperties;
		this.violazioneCdsMapper = violazioneCdsMapper;
		this.avvisoPagamentoMapper = avvisoPagamentoMapper;
		this.avvisoPagamentoBilingueMapper = avvisoPagamentoBilingueMapper;
		this.violazioneCdsService = violazioneCdsService;
		this.avvisoSempliceService = avvisoSempliceService;
		this.avvisoPostaleService = avvisoPostaleService;
		this.avvisoBilingueService = avvisoBilingueService;
		this.semanticValidator = semanticValidator;
	}

	@Override
	public ResponseEntity<Resource> createCdsViolationNotice(@Valid @RequestBody CdsViolation cdsViolation) {
		logger.info("Creazione avviso di violazione codice della strada ...");

		// validazione semantica input
		this.semanticValidator.validazioneSemanticaViolazioneCds(cdsViolation);

		// calcolare il nome prima della conversione l'algoritmo attuale elimina le rate inserite nell'input jasper
		String nomePdf = this.violazioneCdsMapper.nomePdf(cdsViolation);

		AvvisoPagamentoInput avvisoPagamentoInput = this.violazioneCdsMapper.toViolazioneAvvisoPagamentoInput(cdsViolation, this.labelAvvisiProperties);

		logger.debug("Conversione CdsViolation in AvvisoPagamentoInput completata, generazione del pdf...");

		byte[] creaAvviso = null;
		if(avvisoPagamentoInput.getDiPoste() != null) {
			logger.debug("Conversione CdsViolation in AvvisoPagamentoInput completata, generazione del pdf con bollettino postale...");
			creaAvviso = this.avvisoPostaleService.creaAvviso(avvisoPagamentoInput);
		} else {
			logger.debug("Conversione CdsViolation in AvvisoPagamentoInput completata, generazione del pdf senza bollettino postale...");
			creaAvviso = this.violazioneCdsService.creaAvviso(avvisoPagamentoInput);
		}

		logger.debug("Generazione del pdf [{}] completata.", nomePdf);

        ByteArrayResource resource = new ByteArrayResource(creaAvviso);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
		ContentDisposition contentDisposition = ContentDisposition.attachment().filename(nomePdf).build();
		headers.setContentDisposition(contentDisposition );
        headers.setContentLength(creaAvviso.length);

		logger.info("Creazione avviso di violazione codice della strada completata.");

        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(resource);
	}

	@Override
	public ResponseEntity<Resource> createPaymentNotice(@Valid PaymentNotice paymentNotice) {
		logger.info("Creazione avviso standard ...");

		// validazione semantica input
		this.semanticValidator.validazioneSemanticaPaymentNotice(paymentNotice);

		byte[] creaAvviso = null;
		String nomePdf = null;
		// attualmente c'e' una divisione dei template senza bilinguismo
		if(paymentNotice.getSecondLanguage() == null) {
			// calcolare il nome prima della conversione l'algoritmo attuale elimina le rate inserite nell'input jasper
			nomePdf = this.avvisoPagamentoMapper.nomePdf(paymentNotice);

			AvvisoPagamentoInput avvisoPagamentoInput = this.avvisoPagamentoMapper.toPaymentNoticeAvvisoPagamentoInput(logger, paymentNotice, this.labelAvvisiProperties);

			if(avvisoPagamentoInput.getDiPoste() != null) {
				logger.debug("Conversione PaymentNotice in AvvisoPagamentoInput completata, generazione del pdf con bollettino postale...");
				creaAvviso = this.avvisoPostaleService.creaAvviso(avvisoPagamentoInput);
			} else {
				logger.debug("Conversione PaymentNotice in AvvisoPagamentoInput completata, generazione del pdf senza bollettino postale...");
				creaAvviso = this.avvisoSempliceService.creaAvviso(avvisoPagamentoInput);
			}
		} else {
			// calcolare il nome prima della conversione l'algoritmo attuale elimina le rate inserite nell'input jasper
			nomePdf = this.avvisoPagamentoBilingueMapper.nomePdf(paymentNotice);

			it.govpay.stampe.model.v2.AvvisoPagamentoInput avvisoPagamentoInput = this.avvisoPagamentoBilingueMapper.toPaymentNoticeAvvisoPagamentoInput(logger, paymentNotice, this.labelAvvisiProperties);

			logger.debug("Conversione PaymentNotice in AvvisoPagamentoInput completata, generazione del pdf bilingue...");

			creaAvviso = this.avvisoBilingueService.creaAvviso(avvisoPagamentoInput);
		}

		logger.debug("Generazione del pdf [{}] completata.", nomePdf);

        ByteArrayResource resource = new ByteArrayResource(creaAvviso);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
		ContentDisposition contentDisposition = ContentDisposition.attachment().filename(nomePdf).build();
		headers.setContentDisposition(contentDisposition );
        headers.setContentLength(creaAvviso.length);

		logger.info("Creazione avviso standard completata.");

		 return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(resource);
	}
}
