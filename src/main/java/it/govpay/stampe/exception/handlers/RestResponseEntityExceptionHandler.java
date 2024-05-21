package it.govpay.stampe.exception.handlers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.AbstractMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

import com.fasterxml.jackson.databind.exc.ValueInstantiationException;

import it.govpay.stampe.beans.Problem;
import it.govpay.stampe.exception.BadRequestException;
import it.govpay.stampe.exception.CodificaInesistenteException;
import it.govpay.stampe.exception.GenerazioneAvvisoException;
import it.govpay.stampe.exception.InternalException;
import it.govpay.stampe.exception.UnprocessableEntityException;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
	
	private static Logger restLogger = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

	public static final Map<HttpStatus, String> problemTypes = Map.ofEntries(
			new AbstractMap.SimpleEntry<HttpStatus, String>(HttpStatus.CONFLICT,  "https://www.rfc-editor.org/rfc/rfc9110.html#name-409-conflict"),
			new AbstractMap.SimpleEntry<HttpStatus, String>(HttpStatus.NOT_FOUND, "https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found"),
			new AbstractMap.SimpleEntry<HttpStatus, String>(HttpStatus.BAD_REQUEST,"https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request"),
			new AbstractMap.SimpleEntry<HttpStatus, String>(HttpStatus.UNPROCESSABLE_ENTITY,"https://www.rfc-editor.org/rfc/rfc9110.html#name-422-unprocessable-content"),
			new AbstractMap.SimpleEntry<HttpStatus, String>(HttpStatus.INTERNAL_SERVER_ERROR, "https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error"),
			new AbstractMap.SimpleEntry<HttpStatus, String>(HttpStatus.OK, "https://www.rfc-editor.org/rfc/rfc9110.html#name-200-ok"),
			new AbstractMap.SimpleEntry<HttpStatus, String>(HttpStatus.UNAUTHORIZED, "https://www.rfc-editor.org/rfc/rfc9110.html#name-401-unauthorized"),
			new AbstractMap.SimpleEntry<HttpStatus, String>(HttpStatus.FORBIDDEN, "https://www.rfc-editor.org/rfc/rfc9110.html#name-403-forbidden"),
			new AbstractMap.SimpleEntry<HttpStatus, String>(HttpStatus.NOT_ACCEPTABLE, "https://www.rfc-editor.org/rfc/rfc9110.html#name-406-not-acceptable"),
			new AbstractMap.SimpleEntry<HttpStatus, String>(HttpStatus.BAD_GATEWAY, "https://www.rfc-editor.org/rfc/rfc9110.html#name-502-bad-gateway"),
			new AbstractMap.SimpleEntry<HttpStatus, String>(HttpStatus.SERVICE_UNAVAILABLE, "https://www.rfc-editor.org/rfc/rfc9110.html#name-503-service-unavailable"),
			new AbstractMap.SimpleEntry<HttpStatus, String>(HttpStatus.TOO_MANY_REQUESTS, "https://www.rfc-editor.org/rfc/rfc6585#section-4")
		);
	
	public static ResponseEntity<Object> buildResponseProblem(HttpStatus status, String detail, String accept) {
		return buildResponseProblem(status, status.getReasonPhrase(), detail, accept);
	}
	
	public static ResponseEntity<Object> buildResponseProblem(HttpStatus status, String title, String detail, String accept) {
					return ResponseEntity.
							status(status).
			                contentType(MediaType.APPLICATION_PROBLEM_JSON).
							body(buildProblem(status, title, detail));
	}
	
	public static Problem buildProblem(HttpStatus status, String title, String detail) {
		Problem ret = new Problem();
		ret.setStatus(status.value());
		ret.setTitle(title);
		try {
			ret.setType(new URI(problemTypes.get(status)));
		} catch (URISyntaxException e) {
			restLogger.error("Errore nell'impostare la URI del problem", e);
		}
		ret.setDetail(detail);
		
		return ret;
	}
	
	@ExceptionHandler({UnprocessableEntityException.class})
	public ResponseEntity<Object> handleUnprocessableEntity(UnprocessableEntityException ex, WebRequest request) {
		return buildResponseProblem(HttpStatus.UNPROCESSABLE_ENTITY, ex.getLocalizedMessage(), request.getHeader(HttpHeaders.ACCEPT));
	}
	
	@ExceptionHandler({BadRequestException.class, CodificaInesistenteException.class})
	public ResponseEntity<Object> handleBadRequest(Throwable ex, WebRequest request) {
		return buildResponseProblem(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), request.getHeader(HttpHeaders.ACCEPT));
	}

	@ExceptionHandler({Throwable.class, RuntimeException.class, InternalException.class, GenerazioneAvvisoException.class})
	public final ResponseEntity<Object> handleAllInternalExceptions(Throwable ex, WebRequest request) {
		restLogger.error("Handling Internal Server Error: " + ex.getMessage(), ex);
		ex.printStackTrace();
		return buildResponseProblem(HttpStatus.SERVICE_UNAVAILABLE, "Request can't be satisfaied at the moment", request.getHeader(HttpHeaders.ACCEPT)) ;
	}
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		var error = ex.getBindingResult().getAllErrors().get(0);
		return 	buildResponseProblem(HttpStatus.BAD_REQUEST, RestResponseEntityExceptionHandler.extractValidationError(error),request.getHeader(HttpHeaders.ACCEPT));
	}
	
	
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(
			HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request)  {
		
		String msg;
		if (ex.getCause() instanceof ValueInstantiationException) {
			msg = ex.getCause().getLocalizedMessage();			
		} else {
			msg = ex.getLocalizedMessage();
		}
		return 	buildResponseProblem(HttpStatus.BAD_REQUEST,msg, request.getHeader(HttpHeaders.ACCEPT));
	}
	
	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
			HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		return 	buildResponseProblem(HttpStatus.BAD_REQUEST,ex.getLocalizedMessage(), request.getHeader(HttpHeaders.ACCEPT));
	}

	
	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(
			MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		return buildResponseProblem(HttpStatus.BAD_REQUEST,ex.getLocalizedMessage(), request.getHeader(HttpHeaders.ACCEPT));
	}
	
	/**
	 * Quanto il client ci manda un header Accept non supportato.
	 * 
	 * 
	 */
	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(
			HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		
		return buildResponseProblem(HttpStatus.NOT_ACCEPTABLE, ex.getLocalizedMessage(), request.getHeader(HttpHeaders.ACCEPT));
	}
	
	
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(
			Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {

		if (HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.ordinal()).equals(statusCode)) {
			request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, RequestAttributes.SCOPE_REQUEST);
		}
		return 	buildResponseProblem(HttpStatus.INTERNAL_SERVER_ERROR,ex.getLocalizedMessage(), request.getHeader(HttpHeaders.ACCEPT));
	}
	
	/**
	 * Crea un messaggio che descrive un errore di validazione è più leggibile 
	 * per una API rispetto a quello restituito di default.
	 * 	
	 */
	public static String extractValidationError(ObjectError error) {
		if (error instanceof FieldError) {			
			var ferror = (FieldError) error;
			
			return "Field error in object '" + error.getObjectName() + "' on field '" + ferror.getField() +
					"': rejected value [" + ObjectUtils.nullSafeToString(ferror.getRejectedValue()) + "]; " +
					error.getDefaultMessage();
		}
		return error.toString();
	}

}
