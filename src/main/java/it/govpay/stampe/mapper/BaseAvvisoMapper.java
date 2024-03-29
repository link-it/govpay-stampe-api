package it.govpay.stampe.mapper;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import it.govpay.stampe.beans.Amount;
import it.govpay.stampe.beans.Creditor;
import it.govpay.stampe.beans.Languages;
import it.govpay.stampe.config.LabelAvvisiConfiguration.LabelAvvisiProperties;
import it.govpay.stampe.costanti.Costanti;
import it.govpay.stampe.exception.CodificaInesistenteException;
import it.govpay.stampe.model.v1.RataAvviso;

public interface BaseAvvisoMapper {
	
	@Mapping(target = "importo", source="amount")
	@Mapping(target = "data", source="dueDate", qualifiedByName = "mapData")
	@Mapping(target = "codiceAvviso", source="noticeNumber", qualifiedByName = "mapNumeroAvviso")
	@Mapping(target = "qrCode", source="qrcode")
	public RataAvviso amountToRata(Amount amount);

	public default Map<String, String> getLabelLingua(Languages languages, LabelAvvisiProperties labelAvvisiProperties){
		if(languages == null) return null;
		
		switch (languages) {
		case DE:
			return labelAvvisiProperties.getDe();
		case EN:
			return labelAvvisiProperties.getEn();
		case FR:
			return labelAvvisiProperties.getFr();
		case IT:
			return labelAvvisiProperties.getIt();
		case SL:
			return labelAvvisiProperties.getSl();
		}
		throw new CodificaInesistenteException("Label non disponibili per la lingua ["+languages.name()+"]");
	}
	
	@Named("mapLogo")
	public default String mapLogo(org.springframework.core.io.Resource logo) {
		if(logo == null) return null;
		
		try {
	        // Leggi i byte dal file di risorsa del logo
	        byte[] logoBytes = IOUtils.toByteArray(logo.getInputStream());

	        // Converti i byte in una stringa
	        return new String(logoBytes);
	    } catch (IOException e) {
	        e.printStackTrace(); // o logga l'errore
	        return null; // o gestisci diversamente l'errore, restituendo una stringa vuota o lanciando un'eccezione
	    }
	}

	// Nella vecchia versione non c'era la distinzione tra le due linee ma venivano concatenate e mandate a capo con <BR/>
	@Named("mapInfoEnte")
	public default String mapInfoEnte(Creditor enteCreditore) {
		if(enteCreditore == null) return "";


		StringBuilder sb = new StringBuilder();

		if(StringUtils.isNotBlank(enteCreditore.getInfoLine1())){
			sb.append(enteCreditore.getInfoLine1());
		}

		if(StringUtils.isNotBlank(enteCreditore.getInfoLine2())){
			if(sb.length() > 0) {
				sb.append("<br/>");
			}

			sb.append(enteCreditore.getInfoLine2());
		}

		return sb.toString();
	}

	@Named("mapPostale")
	public default String mapPostale(Boolean postal) {
		if(postal != null && postal.booleanValue()) {
			return Costanti.DI_POSTE;
		}

		return null;
	}

	@Named("mapDelTuoEnte")
	public default String mapDelTuoEnte(Boolean postal) {
		if(postal != null && postal.booleanValue()) {
			return Costanti.DEL_TUO_ENTE_CREDITORE;
		}

		return null;
	}

	@Named("mapData")
	public default String mapData(LocalDate localDate) {
		if ( localDate != null ) {
			return DateTimeFormatter.ofPattern( Costanti.PATTERN_DATA_GG_MM_AAAA).format( localDate ) ;
		}

		return null;
	}

	@Named("mapNumeroAvviso")
	public default String mapNumeroAvviso(String numeroAvviso) {
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
