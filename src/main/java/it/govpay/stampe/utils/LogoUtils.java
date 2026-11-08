package it.govpay.stampe.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Pattern;

import org.springframework.core.io.Resource;

import it.govpay.stampe.exception.BadRequestException;

/**
 * Loghi di avviso e ricevuta (issue #38): il JSON porta i byte dell'immagine (campi {@code format: binary}),
 * il layout vuole una URI data. Il tipo dell'immagine e' riconosciuto dal contenuto: PNG, JPEG, GIF e SVG.
 * <p>
 * Per gli avvisi sono ancora accettate le forme precedenti, che i client mandano oggi: i byte di una URI data
 * ({@code data:image/png;base64,...}) e i byte del solo base64 dell'immagine, senza prefisso.
 */
public class LogoUtils {

	private static final byte[] PNG = { (byte) 0x89, 'P', 'N', 'G' };
	private static final byte[] JPEG = { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF };
	private static final byte[] GIF = { 'G', 'I', 'F', '8' };
	private static final Pattern BASE64 = Pattern.compile("[A-Za-z0-9+/=\\s]+");

	private LogoUtils() {}

	/**
	 * URI data del logo, {@code null} se il logo e' assente o vuoto.
	 *
	 * @param logo contenuto del campo binario
	 * @param campo nome del campo nel JSON, per il messaggio d'errore
	 * @throws BadRequestException se il contenuto non e' un'immagine PNG, JPEG, GIF o SVG
	 */
	public static String dataUri(Resource logo, String campo) {
		if (logo == null) {
			return null;
		}
		try {
			return dataUri(logo.getContentAsByteArray(), campo);
		} catch (IOException e) {
			throw new BadRequestException("Logo non leggibile: " + campo);
		}
	}

	public static String dataUri(byte[] dati, String campo) {
		if (dati == null || dati.length == 0) {
			return null;
		}
		String mime = tipo(dati);
		if (mime != null) {
			return "data:" + mime + ";base64," + Base64.getEncoder().encodeToString(dati);
		}
		// forme precedenti, solo testo: URI data, o base64 dell'immagine senza prefisso
		String testo = new String(dati, StandardCharsets.US_ASCII).trim();
		if (testo.isEmpty()) {
			return null;
		}
		if (testo.startsWith("data:image/")) {
			return testo;
		}
		if (BASE64.matcher(testo).matches()) {
			try {
				byte[] immagine = Base64.getMimeDecoder().decode(testo);
				String mimeDecodificato = tipo(immagine);
				if (mimeDecodificato != null) {
					return "data:" + mimeDecodificato + ";base64," + Base64.getEncoder().encodeToString(immagine);
				}
			} catch (IllegalArgumentException e) {
				// non e' base64: formato non supportato
			}
		}
		throw new BadRequestException("Formato del logo non supportato (attesi PNG, JPEG, GIF o SVG): " + campo);
	}

	/** Tipo MIME dell'immagine dal contenuto, {@code null} se non e' PNG, JPEG, GIF o SVG. */
	static String tipo(byte[] dati) {
		if (inizia(dati, PNG)) {
			return "image/png";
		}
		if (inizia(dati, JPEG)) {
			return "image/jpeg";
		}
		if (inizia(dati, GIF)) {
			return "image/gif";
		}
		// SVG: testo XML con l'elemento svg, eventualmente preceduto da BOM, dichiarazione XML o commenti
		String inizio = new String(dati, 0, Math.min(dati.length, 1024), StandardCharsets.UTF_8).replace("﻿", "").trim();
		if ((inizio.startsWith("<?xml") || inizio.startsWith("<svg") || inizio.startsWith("<!--") || inizio.startsWith("<!DOCTYPE svg"))
				&& inizio.contains("<svg")) {
			return "image/svg+xml";
		}
		return null;
	}

	private static boolean inizia(byte[] dati, byte[] firma) {
		return dati.length >= firma.length && Arrays.equals(dati, 0, firma.length, firma, 0, firma.length);
	}
}
