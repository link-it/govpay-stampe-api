package it.govpay.stampe.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import it.govpay.stampe.beans.Receipt;
import it.govpay.stampe.beans.ReceiptItem;
import it.govpay.stampe.beans.ReceiptItemStatus;
import it.govpay.stampe.beans.ReceiptStatus;
import it.govpay.stampe.beans.ReceiptVersion;
import it.govpay.stampe.model.v1.RicevutaTelematicaInput;

@Mapper(componentModel = "spring")
public interface RicevutaMapper {

	public default String nomePdf(Receipt receipt) {
		return receipt.getOrganization().getFiscalCode() + "_" + receipt.getCreditorReferenceId() + "_ricevuta.pdf";
	}

	@Mapping(target = "logoEnte", source = "creditorLogo")
	@Mapping(target = "logoPagopa", source = "pagopaLogo")
	@Mapping(target = "oggettoDelPagamento", source = "paymentSubject")
	@Mapping(target = "enteDenominazione", source = "organization.businessName")
	@Mapping(target = "indirizzoEnte", source = "organization.address")
	@Mapping(target = "luogoEnte", source = "organization.location")
	@Mapping(target = "cfEnte", source = "organization.fiscalCode")
	@Mapping(target = "soggetto", source = "payer.fullName")
	@Mapping(target = "indirizzoSoggetto", source = "payer.address")
	@Mapping(target = "luogoSoggetto", source = "payer.location")
	@Mapping(target = "cfSoggetto", source = "payer.fiscalCode")
	@Mapping(target = "istituto", source = "psp")
	@Mapping(target = "importo", source = "amount")
	@Mapping(target = "dataOperazione", source = "operationDate")
	@Mapping(target = "dataApplicativa", source = "applicationDate")
	@Mapping(target = "stato", source = "status", qualifiedByName = "receiptStatusToString")
	@Mapping(target = "IUV", source = "creditorReferenceId")
	@Mapping(target = "CCP", source = "receiptId")
	@Mapping(target = "versioneOggetto", source = "objectVersion", qualifiedByName = "receiptVersionToString")
	@Mapping(target = "elencoVoci", source = "items")
	RicevutaTelematicaInput toRicevutaTelematicaInput(Receipt receipt);

	@Mapping(target = "descrizione", source = "description")
	@Mapping(target = "idRiscossione", source = "iur")
	@Mapping(target = "importo", source = "amount")
	@Mapping(target = "stato", source = "status", qualifiedByName = "receiptItemStatusToString")
	it.govpay.stampe.model.v1.VoceRicevutaTelematicaInput toVoceRicevutaTelematicaInput(ReceiptItem item);

	List<it.govpay.stampe.model.v1.VoceRicevutaTelematicaInput> toVoceRicevutaTelematicaInputList(List<ReceiptItem> items);

	default RicevutaTelematicaInput.ElencoVoci toElencoVoci(List<ReceiptItem> items) {
		if (items == null) {
			return null;
		}
		RicevutaTelematicaInput.ElencoVoci elencoVoci = new RicevutaTelematicaInput.ElencoVoci();
		elencoVoci.getVoce().addAll(toVoceRicevutaTelematicaInputList(items));
		return elencoVoci;
	}

	@Named("receiptStatusToString")
	default String receiptStatusToString(ReceiptStatus status) {
		if (status == null) {
			return null;
		}
		return switch (status) {
			case EXECUTED -> "Eseguito";
			case NOT_EXECUTED -> "Non eseguito";
			case PARTIALLY_EXECUTED -> "Parzialmente eseguito";
			case EXPIRED -> "Decorrenza termini";
			case PARTIALLY_EXPIRED -> "Decorrenza termini parziale";
		};
	}

	@Named("receiptItemStatusToString")
	default String receiptItemStatusToString(ReceiptItemStatus status) {
		if (status == null) {
			return null;
		}
		return switch (status) {
			case EXECUTED -> "Eseguito";
			case NOT_EXECUTED -> "Non eseguito";
		};
	}

	@Named("receiptVersionToString")
	default String receiptVersionToString(ReceiptVersion version) {
		if (version == null) {
			return null;
		}
		return version.getValue();
	}
}
