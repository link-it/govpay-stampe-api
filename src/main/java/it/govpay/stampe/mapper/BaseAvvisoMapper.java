package it.govpay.stampe.mapper;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.govpay.stampe.beans.Amount;
import it.govpay.stampe.beans.Creditor;
import it.govpay.stampe.beans.Iban;
import it.govpay.stampe.beans.Instalment;
import it.govpay.stampe.beans.Languages;
import it.govpay.stampe.config.LabelAvvisiConfiguration.LabelAvvisiProperties;
import it.govpay.stampe.costanti.Costanti;
import it.govpay.stampe.exception.CodificaInesistenteException;
import it.govpay.stampe.model.v1.AvvisoPagamentoInput;
import it.govpay.stampe.model.v1.RataAvviso;
import it.govpay.stampe.utils.AvvisoPagamentoUtils;

public interface BaseAvvisoMapper {

	Logger logger = LoggerFactory.getLogger(BaseAvvisoMapper.class);

	@Mapping(target = "importo", source="amount")
	@Mapping(target = "data", source="dueDate", qualifiedByName = "mapData")
	@Mapping(target = "codiceAvviso", source="noticeNumber", qualifiedByName = "mapNumeroAvviso")
	@Mapping(target = "qrCode", source="qrcode")
	public RataAvviso amountToRataBase(Amount amount);


	public default RataAvviso amountToRata(Amount amount, Boolean postale, AvvisoPagamentoInput avvisoPagamentoInput, Creditor creditor) {
		RataAvviso rataAvviso = amountToRataBase(amount);

		if(Boolean.TRUE.equals(postale)){
			impostaDatiPostaliNellaRata(rataAvviso, amount.getNoticeNumber(), amount.getIban(),
					amount.getAmount().doubleValue(), avvisoPagamentoInput, creditor);
		}

		return rataAvviso;
	}

	/***
	 * Imposta nella rata i dati del bollettino postale: datamatrix, numero di conto corrente,
	 * codice avviso, autorizzazione e intestatario del conto corrente.
	 *
	 */
	public default void impostaDatiPostaliNellaRata(RataAvviso rataAvviso, String noticeNumber, Iban iban, double importo,
			AvvisoPagamentoInput avvisoPagamentoInput, Creditor creditor) {
		String numeroCC = AvvisoPagamentoUtils.getNumeroCCDaIban(iban.getIbanCode());
		rataAvviso.setDataMatrix(AvvisoPagamentoUtils.creaDataMatrix(noticeNumber, numeroCC,
					importo,
					avvisoPagamentoInput.getCfEnte(),
					avvisoPagamentoInput.getCfDestinatario(),
					avvisoPagamentoInput.getNomeCognomeDestinatario(),
					avvisoPagamentoInput.getOggettoDelPagamento()));
		rataAvviso.setNumeroCcPostale(numeroCC);
		rataAvviso.setCodiceAvvisoPostale(rataAvviso.getCodiceAvviso());
		rataAvviso.setAutorizzazione(getAutorizzazionePostale(creditor, iban));

		if(StringUtils.isBlank(iban.getOwnerBusinessName()))
			avvisoPagamentoInput.setIntestatarioContoCorrentePostale(avvisoPagamentoInput.getEnteCreditore());
		else
			avvisoPagamentoInput.setIntestatarioContoCorrentePostale(iban.getOwnerBusinessName());
	}

	/***
	 * Restituisce l'autorizzazione delle poste da riportare sul bollettino: viene utilizzata quella
	 * associata all'iban, se presente, altrimenti quella dell'ente creditore.
	 *
	 */
	public default String getAutorizzazionePostale(Creditor creditor, Iban iban) {
		String autorizzazioneEnte = creditor != null ? creditor.getPostalAuthMessage() : null;
		String autorizzazioneIban = iban != null ? iban.getPostalAuthMessage() : null;

		return AvvisoPagamentoUtils.getAutorizzazionePoste(autorizzazioneEnte, autorizzazioneIban);
	}

	/***
	 * Restituisce le label configurate per la lingua indicata.
	 *
	 * Non restituisce mai null: se la lingua non e' indicata o non sono configurate le label
	 * per quella lingua viene sollevata una CodificaInesistenteException.
	 *
	 */
	public default Map<String, String> getLabelLingua(Languages languages, LabelAvvisiProperties labelAvvisiProperties){
		if(languages == null)
			throw new CodificaInesistenteException("Lingua non indicata: impossibile determinare le label dell'avviso");

		Map<String, String> labelLingua = switch (languages) {
			case DE -> labelAvvisiProperties.getDe();
			case EN -> labelAvvisiProperties.getEn();
			case FR -> labelAvvisiProperties.getFr();
			case IT -> labelAvvisiProperties.getIt();
			case SL -> labelAvvisiProperties.getSl();
			default -> throw new CodificaInesistenteException("Label non disponibili per la lingua ["+languages.name()+"]");
		};

		if(labelLingua == null)
			throw new CodificaInesistenteException("Label non disponibili per la lingua ["+languages.name()+"]");

		return labelLingua;
	}
	
	@Named("mapLogo")
	public default String mapLogo(org.springframework.core.io.Resource logo) {
		if(logo == null) return null;
		
		try {
	        byte[] logoBytes = IOUtils.toByteArray(logo.getInputStream());
	        return new String(logoBytes);
	    } catch (IOException e) {
	        logger.error("Errore durante la lettura del logo", e);
	        return null;
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
			if(!sb.isEmpty()) {
				sb.append("<br/>");
			}

			sb.append(enteCreditore.getInfoLine2());
		}

		return sb.toString();
	}

	@Named("mapPostale")
	public default String mapPostale(Boolean postal) {
		if(Boolean.TRUE.equals(postal)) {
			return Costanti.DI_POSTE;
		}

		return null;
	}

	@Named("mapDelTuoEnte")
	public default String mapDelTuoEnte(Boolean postal) {
		if(Boolean.TRUE.equals(postal)) {
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
		return AvvisoPagamentoUtils.splitNumeroAvviso(numeroAvviso);
	}
	
	public default boolean isMultipla(List<Instalment> elencoRate) {
		return elencoRate.size() > 3;
	}
	
	public default boolean isPaginaPrincipaleDoppia(List<Instalment> elencoRate) {
		return ((elencoRate.size() - 4) %9 ==0) ||((elencoRate.size() - 8) %9 == 0);
	}
}
