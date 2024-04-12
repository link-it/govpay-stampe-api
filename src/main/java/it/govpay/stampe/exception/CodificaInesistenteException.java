package it.govpay.stampe.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**	
 * Contiene la definizione di una eccezione lanciata in caso di argomento non riconosciuto in fase di decodifica del valore di una enum.
 *
 * @author Pintori Giuliano (pintori@link.it)
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CodificaInesistenteException extends RuntimeException {

	public CodificaInesistenteException(String message, Throwable cause)	{
		super(message, cause);
	}
	
	public CodificaInesistenteException(Throwable cause)	{
		super(cause);
	}
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	public CodificaInesistenteException() {
		super();
	}
	public CodificaInesistenteException(String msg) {
		super(msg);
	}

}
