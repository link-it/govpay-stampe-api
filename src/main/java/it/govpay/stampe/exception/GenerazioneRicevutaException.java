package it.govpay.stampe.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class GenerazioneRicevutaException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public GenerazioneRicevutaException(String message) {
		super(message);
	}
	
	public GenerazioneRicevutaException(Throwable throwable) {
		super(throwable);
	}

}