package it.govpay.stampe.controller;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import it.govpay.stampe.api.ReceiptApi;
import it.govpay.stampe.beans.Receipt;
import it.govpay.stampe.mapper.RicevutaMapper;
import it.govpay.stampe.model.v1.RicevutaTelematicaInput;
import it.govpay.stampe.service.RicevutaService;

@RestController
public class ReceiptController implements ReceiptApi {

	private static Logger logger = LoggerFactory.getLogger(ReceiptController.class);

	@Autowired
	RicevutaMapper ricevutaMapper;

	@Autowired
	RicevutaService ricevutaService;

	@Override
	public ResponseEntity<Resource> createReceipt(@Valid @RequestBody Receipt receipt) {
		logger.info("Creazione ricevuta di pagamento ...");

		// calcolare il nome prima della conversione
		String nomePdf = this.ricevutaMapper.nomePdf(receipt);

		// conversione Receipt in RicevutaTelematicaInput
		RicevutaTelematicaInput ricevutaTelematicaInput = this.ricevutaMapper.toRicevutaTelematicaInput(receipt);

		logger.debug("Conversione Receipt in RicevutaTelematicaInput completata, generazione del pdf...");

		byte[] creaRicevuta = this.ricevutaService.creaRicevuta(ricevutaTelematicaInput);

		logger.debug("Generazione del pdf [{}] completata.", nomePdf);

		ByteArrayResource resource = new ByteArrayResource(creaRicevuta);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		ContentDisposition contentDisposition = ContentDisposition.attachment().filename(nomePdf).build();
		headers.setContentDisposition(contentDisposition);
		headers.setContentLength(creaRicevuta.length);

		logger.info("Creazione ricevuta di pagamento completata.");

		return ResponseEntity.created(null).headers(headers).body(resource);
	}
}
