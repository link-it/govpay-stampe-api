package it.govpay.stampe.utils;

import java.text.MessageFormat;
import java.text.Normalizer;

import org.apache.commons.lang3.StringUtils;

import it.govpay.stampe.costanti.Costanti;

public class AvvisoPagamentoUtils {
	
	private AvvisoPagamentoUtils() {}

	/***
	 * Crea il codice DataMatrix da stampare sul bollettino postale
	 * 
	 * @param numeroAvviso
	 * @param numeroCC
	 * @param importo
	 * @param codDominio
	 * @param cfDebitore
	 * @param denominazioneDebitore
	 * @param causale
	 * @return
	 */
	public static String creaDataMatrix(String numeroAvviso, String numeroCC, double importo, String codDominio, String cfDebitore, String denominazioneDebitore, String causale) {

		String importoInCentesimi = getImportoInCentesimi(importo);
		String codeLine = createCodeLine(numeroAvviso, numeroCC, importoInCentesimi);

		String cfDebitoreFilled = getCfDebitoreFilled(cfDebitore);

		String denominazioneDebitoreASCII = Normalizer.normalize(denominazioneDebitore, Normalizer.Form.NFD);
		denominazioneDebitoreASCII = denominazioneDebitoreASCII.replaceAll("[^\\x00-\\x7F]", "");
		String denominazioneDebitoreFilled = getDenominazioneDebitoreFilled(denominazioneDebitoreASCII);

		String causaleASCII = Normalizer.normalize(causale, Normalizer.Form.NFD);
		causaleASCII = causaleASCII.replaceAll("[^\\x00-\\x7F]", "");
		String causaleFilled = getCausaleFilled(causaleASCII);

		String dataMatrix = MessageFormat.format(Costanti.PATTERN_DATAMATRIX, codeLine, codDominio, cfDebitoreFilled, denominazioneDebitoreFilled, causaleFilled, Costanti.FILLER_DATAMATRIX);
		return dataMatrix;
	}

	public static String createCodeLine(String numeroAvviso, String numeroCC, String importoInCentesimi) {
		return MessageFormat.format(Costanti.PATTERN_CODELINE, numeroAvviso,numeroCC,importoInCentesimi);
	}

	public static String fillSx(String start, String charToFillWith, int lunghezza) {
		int iterazioni = lunghezza - start.length();

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < iterazioni; i++) {
			sb.append(charToFillWith);
		}
		sb.append(start);

		return sb.toString();
	}

	public static String fillDx(String start, String charToFillWith, int lunghezza) {
		int iterazioni = lunghezza - start.length();

		StringBuilder sb = new StringBuilder();

		sb.append(start);
		for (int i = 0; i < iterazioni; i++) {
			sb.append(charToFillWith);
		}

		return sb.toString();
	}

	public static String getNumeroCCDaIban(String iban) {
		return iban.substring(iban.length() - 12, iban.length());
	}

	public static String getImportoInCentesimi(double importo) {
		int tmpImporto = (int) (importo  * 100);
		String stringImporto = Integer.toString(tmpImporto);

		if(stringImporto.length() == Costanti.DATAMATRIX_LUNGHEZZA_CAMPO_IMPORTO)
			return stringImporto.toUpperCase();

		if(stringImporto.length() > Costanti.DATAMATRIX_LUNGHEZZA_CAMPO_IMPORTO) {
			return stringImporto.substring(0, Costanti.DATAMATRIX_LUNGHEZZA_CAMPO_IMPORTO).toUpperCase();
		}


		return fillSx(stringImporto, "0", Costanti.DATAMATRIX_LUNGHEZZA_CAMPO_IMPORTO).toUpperCase();
	}

	public static String getCfDebitoreFilled(String cfDebitore) {
		if(cfDebitore.length() == Costanti.DATAMATRIX_LUNGHEZZA_CAMPO_CF_DEBITORE)
			return cfDebitore.toUpperCase();

		if(cfDebitore.length() > Costanti.DATAMATRIX_LUNGHEZZA_CAMPO_CF_DEBITORE) {
			return cfDebitore.substring(0, Costanti.DATAMATRIX_LUNGHEZZA_CAMPO_CF_DEBITORE).toUpperCase();
		}


		return fillDx(cfDebitore, " ", Costanti.DATAMATRIX_LUNGHEZZA_CAMPO_CF_DEBITORE).toUpperCase();
	}

	/***
	 * numero caratteri denominazione debitore 40
	 * @param denominazioneDebitore
	 * @return
	 */
	public static String getDenominazioneDebitoreFilled(String denominazioneDebitore) {
		if(denominazioneDebitore.length() == Costanti.DATAMATRIX_LUNGHEZZA_CAMPO_ANAGRAFICA_DEBITORE)
			return denominazioneDebitore.toUpperCase();

		if(denominazioneDebitore.length() > Costanti.DATAMATRIX_LUNGHEZZA_CAMPO_ANAGRAFICA_DEBITORE) {
			return denominazioneDebitore.substring(0, Costanti.DATAMATRIX_LUNGHEZZA_CAMPO_ANAGRAFICA_DEBITORE).toUpperCase();
		}


		return fillDx(denominazioneDebitore, " ", Costanti.DATAMATRIX_LUNGHEZZA_CAMPO_ANAGRAFICA_DEBITORE).toUpperCase();
	}

	/**
	 * numero caratteri del campo causale 110
	 * @param causale
	 * @return
	 */
	public static String getCausaleFilled(String causale) {
		if(causale.length() == Costanti.DATAMATRIX_LUNGHEZZA_CAMPO_CAUSALE)
			return causale.toUpperCase();

		if(causale.length() > Costanti.DATAMATRIX_LUNGHEZZA_CAMPO_CAUSALE) {
			return causale.substring(0, Costanti.DATAMATRIX_LUNGHEZZA_CAMPO_CAUSALE).toUpperCase();
		}


		return fillDx(causale, " ", Costanti.DATAMATRIX_LUNGHEZZA_CAMPO_CAUSALE).toUpperCase();
	}


	/***
	 * Restituisce la stringa con l'autorizzazione da includere nel bollettino postale
	 * 
	 * @param autDominio
	 * @param autIban
	 * @return
	 */
	public static String getAutorizzazionePoste(String autDominio, String autIban) {
		if(StringUtils.isNotBlank(autIban))
			return autIban;

		return autDominio;

	}
	
	/***
	 * Divide il numero avviso in gruppi di 4 cifre
	 * 
	 * @param numeroAvviso
	 * @return
	 */
	public static String splitNumeroAvviso(String numeroAvviso) {
		// se non e' gia' diviso in gruppi di 4 cifre
		if(numeroAvviso != null && !numeroAvviso.contains(" ")) {
			// split del numero avviso a gruppi di 4 cifre
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < numeroAvviso.length(); i++) {
				if(sb.length() > 0 && (i % 4 == 0)) {
					sb.append(" ");
				}

				sb.append(numeroAvviso.charAt(i));
			}

			return sb.toString();
		}

		return null;
	}
}
