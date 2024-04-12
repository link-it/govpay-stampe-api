package it.govpay.stampe.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class GenerazioneAvvisoException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public GenerazioneAvvisoException(String message) {
		super(message);
	}
	
	public GenerazioneAvvisoException(Throwable throwable) {
		super(throwable);
	}

}
