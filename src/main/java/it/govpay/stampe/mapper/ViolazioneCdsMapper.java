package it.govpay.stampe.mapper;

import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import it.govpay.stampe.beans.Amount;
import it.govpay.stampe.beans.CdsViolation;
import it.govpay.stampe.config.LabelAvvisiConfiguration.LabelAvvisiProperties;
import it.govpay.stampe.costanti.Costanti;
import it.govpay.stampe.costanti.LabelAvvisiCostanti;
import it.govpay.stampe.model.v1.AvvisoPagamentoInput;
import it.govpay.stampe.model.v1.PaginaAvvisoSingola;
import it.govpay.stampe.model.v1.PagineAvviso;
import it.govpay.stampe.model.v1.RataAvviso;

@Mapper(componentModel = "spring")
public interface ViolazioneCdsMapper extends BaseAvvisoMapper{

	public default String nomePdf(CdsViolation cdsViolation) {
		return cdsViolation.getCreditor().getFiscalCode() + "_" + cdsViolation.getDiscountedAmount().getNoticeNumber() + ".pdf";
	}


	public default AvvisoPagamentoInput toViolazioneAvvisoPagamentoInput(CdsViolation cdsViolation, LabelAvvisiProperties labelAvvisiProperties) {
		Map<String, String> labelItaliano = getLabelLingua(cdsViolation.getLanguage(), labelAvvisiProperties);

		AvvisoPagamentoInput avvisoPagamentoInput = toViolazioneAvvisoPagamentoInputBase(cdsViolation);

		// importo ridotto
		Amount reducedAmount = cdsViolation.getReducedAmount();
		RataAvviso rataRidotto = amountToRata(reducedAmount);
		rataRidotto.setTipo(Costanti.TIPO_RATA_RIDOTTO);

		// importo scontato
		Amount discountedAmount = cdsViolation.getDiscountedAmount();
		RataAvviso rataScontato = amountToRata(discountedAmount);
		rataScontato.setTipo(Costanti.TIPO_RATA_SCONTATO);
		rataScontato.setImportoScontato(rataScontato.getImporto());
		// properties per completare la doppia pagina
		rataScontato.setCodiceAvviso2(rataRidotto.getCodiceAvviso());
		rataScontato.setQrCode2(rataRidotto.getQrCode());
		rataScontato.setImportoRidotto(rataRidotto.getImporto());

		avvisoPagamentoInput.setPagine(new PagineAvviso());

		Boolean postal = cdsViolation.getPostal();
		if(postal != null && postal.booleanValue()) { // avviso postale

			PaginaAvvisoSingola pagina1 = new PaginaAvvisoSingola();
			pagina1.setRata(rataRidotto);
			avvisoPagamentoInput.getPagine().getSingolaOrDoppiaOrTripla().add(pagina1);

			PaginaAvvisoSingola pagina2 = new PaginaAvvisoSingola();
			pagina2.setRata(rataScontato);
			avvisoPagamentoInput.getPagine().getSingolaOrDoppiaOrTripla().add(pagina2);

		} else {

			// label da leggere da properties
			avvisoPagamentoInput.setScadenzaRidotto(labelItaliano.get(LabelAvvisiCostanti.LABEL_VIOLAZIONE_CDS_SCADENZA_RIDOTTO));
			avvisoPagamentoInput.setScadenzaScontato(labelItaliano.get(LabelAvvisiCostanti.LABEL_VIOLAZIONE_CDS_SCADENZA_SCONTATO));


			PaginaAvvisoSingola pagina = new PaginaAvvisoSingola();
			pagina.setRata(rataScontato);

			avvisoPagamentoInput.getPagine().getSingolaOrDoppiaOrTripla().add(pagina);
		}

		return avvisoPagamentoInput;
	}


	@Mapping(target = "logoEnte", source="firstLogo", qualifiedByName = "mapLogo")
	@Mapping(target = "logoEnteSecondario", source="secondLogo", qualifiedByName = "mapLogo")
	@Mapping(target = "oggettoDelPagamento", source="title")
	@Mapping(target = "cfEnte", source="cdsViolation.creditor.fiscalCode")
	@Mapping(target = "enteCreditore", source="cdsViolation.creditor.businessName")
	@Mapping(target = "settoreEnte", source="cdsViolation.creditor.departmentName")
	@Mapping(target = "infoEnte", source="cdsViolation.creditor", qualifiedByName = "mapInfoEnte")
	@Mapping(target = "cbill", source="cdsViolation.creditor.cbillCode")
	@Mapping(target = "cfDestinatario", source="cdsViolation.debtor.fiscalCode")
	@Mapping(target = "nomeCognomeDestinatario", source="cdsViolation.debtor.fullName")
	@Mapping(target = "indirizzoDestinatario1", source="cdsViolation.debtor.addressLine1")
	@Mapping(target = "indirizzoDestinatario2", source="cdsViolation.debtor.addressLine2")
	@Mapping(target = "diPoste", source="cdsViolation.postal", qualifiedByName = "mapPostale")
	@Mapping(target = "delTuoEnte", source="cdsViolation.postal", qualifiedByName = "mapDelTuoEnte")
	public AvvisoPagamentoInput toViolazioneAvvisoPagamentoInputBase(CdsViolation cdsViolation);





}

