package it.govpay.stampe.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class UnprocessableEntityException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UnprocessableEntityException(String msg) {
		super(msg);
	}

	public UnprocessableEntityException(Throwable e) {
		super("Errore di validazione semantica: " + e.getLocalizedMessage() , e);
	}
}
