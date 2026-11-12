package it.govpay.stampe.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;

/** Loghi come byte dell'immagine, con il tipo riconosciuto dal contenuto, e forme precedenti (issue #38 e #67). */
class LogoUtilsTest {

	// PNG 1x1
	private static final String PNG_BASE64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==";
	private static final byte[] PNG = Base64.getDecoder().decode(PNG_BASE64);
	private static final byte[] JPEG = { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0, 0x10, 'J', 'F', 'I', 'F' };
	private static final byte[] GIF = "GIF89a\u0001\u0000\u0001\u0000".getBytes(StandardCharsets.ISO_8859_1);
	private static final byte[] SVG = "<?xml version=\"1.0\"?>\n<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"1\" height=\"1\"/>".getBytes(StandardCharsets.UTF_8);

	@Test
	void immaginiRiconosciuteDalContenuto() {
		assertEquals("data:image/png;base64," + PNG_BASE64, LogoUtils.dataUri(PNG));
		assertTrue(LogoUtils.dataUri(JPEG).startsWith("data:image/jpeg;base64,"));
		assertTrue(LogoUtils.dataUri(GIF).startsWith("data:image/gif;base64,"));
		assertTrue(LogoUtils.dataUri(SVG).startsWith("data:image/svg+xml;base64,"));
		assertEquals("image/svg+xml", LogoUtils.tipo(("﻿<svg xmlns=\"http://www.w3.org/2000/svg\"/>").getBytes(StandardCharsets.UTF_8)));
	}

	@Test
	void uriDataPrecedente() {
		// gli avvisi mandavano i byte della URI data: restano accettati cosi' come sono
		String uri = "data:image/png;base64," + PNG_BASE64;
		assertEquals(uri, LogoUtils.dataUri(uri.getBytes(StandardCharsets.US_ASCII)));
	}

	@Test
	void base64SenzaPrefisso() {
		assertEquals("data:image/png;base64," + PNG_BASE64, LogoUtils.dataUri(PNG_BASE64.getBytes(StandardCharsets.US_ASCII)));
	}

	@Test
	void logoAssenteOVuoto() {
		assertNull(LogoUtils.dataUri((ByteArrayResource) null, "first_logo"));
		assertNull(LogoUtils.dataUri(new byte[0]));
		assertNull(LogoUtils.dataUri("  ".getBytes(StandardCharsets.US_ASCII)));
	}

	@Test
	void bmpETiff() {
		assertEquals("image/bmp", LogoUtils.tipo("BM6\u0000\u0000\u0000".getBytes(StandardCharsets.ISO_8859_1)));
		assertEquals("image/tiff", LogoUtils.tipo(new byte[] { 'I', 'I', 42, 0, 8, 0, 0, 0 }));
		assertEquals("image/tiff", LogoUtils.tipo(new byte[] { 'M', 'M', 0, 42, 0, 0, 0, 8 }));
	}

	@Test
	void formatoNonRiconosciuto() {
		// non e' un errore della richiesta: FOP non lo stampa e il documento esce senza logo (issue #67)
		byte[] testo = "non e' un'immagine".getBytes(StandardCharsets.UTF_8);
		assertEquals("data:application/octet-stream;base64," + Base64.getEncoder().encodeToString(testo),
				LogoUtils.dataUri(new ByteArrayResource(testo), "creditor_logo"));
		// base64 valido ma non di un'immagine
		assertTrue(LogoUtils.dataUri("QUJDRA==".getBytes(StandardCharsets.US_ASCII)).startsWith("data:application/octet-stream;base64,"));
	}
}
