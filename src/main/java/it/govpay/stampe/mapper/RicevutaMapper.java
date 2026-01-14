package it.govpay.stampe.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import it.govpay.stampe.beans.Receipt;
import it.govpay.stampe.beans.VoceRicevutaTelematicaInput;
import it.govpay.stampe.model.v1.RicevutaTelematicaInput;

@Mapper(componentModel = "spring")
public interface RicevutaMapper {

	public default String nomePdf(Receipt receipt) {
		return receipt.getCfEnte() + "_" + receipt.getIUV() + "_ricevuta.pdf";
	}

	@Mapping(target = "logoEnte", source = "logoEnte")
	@Mapping(target = "logoPagopa", source = "logoPagopa")
	@Mapping(target = "oggettoDelPagamento", source = "oggettoDelPagamento")
	@Mapping(target = "enteDenominazione", source = "enteDenominazione")
	@Mapping(target = "indirizzoEnte", source = "indirizzoEnte")
	@Mapping(target = "luogoEnte", source = "luogoEnte")
	@Mapping(target = "cfEnte", source = "cfEnte")
	@Mapping(target = "soggetto", source = "soggetto")
	@Mapping(target = "indirizzoSoggetto", source = "indirizzoSoggetto")
	@Mapping(target = "luogoSoggetto", source = "luogoSoggetto")
	@Mapping(target = "cfSoggetto", source = "cfSoggetto")
	@Mapping(target = "istituto", source = "istituto")
	@Mapping(target = "importo", source = "importo")
	@Mapping(target = "dataOperazione", source = "dataOperazione")
	@Mapping(target = "dataApplicativa", source = "dataApplicativa")
	@Mapping(target = "stato", source = "stato")
	@Mapping(target = "IUV", source = "IUV")
	@Mapping(target = "CCP", source = "CCP")
	@Mapping(target = "versioneOggetto", source = "versioneOggetto")
	@Mapping(target = "elencoVoci", source = "elencoVoci")
	RicevutaTelematicaInput toRicevutaTelematicaInput(Receipt receipt);

	@Mapping(target = "descrizione", source = "descrizione")
	@Mapping(target = "idRiscossione", source = "idRiscossione")
	@Mapping(target = "importo", source = "importo")
	@Mapping(target = "stato", source = "stato")
	it.govpay.stampe.model.v1.VoceRicevutaTelematicaInput toVoceRicevutaTelematicaInput(VoceRicevutaTelematicaInput voce);

	List<it.govpay.stampe.model.v1.VoceRicevutaTelematicaInput> toVoceRicevutaTelematicaInputList(List<VoceRicevutaTelematicaInput> voci);

	default RicevutaTelematicaInput.ElencoVoci toElencoVoci(List<VoceRicevutaTelematicaInput> voci) {
		if (voci == null) {
			return null;
		}
		RicevutaTelematicaInput.ElencoVoci elencoVoci = new RicevutaTelematicaInput.ElencoVoci();
		elencoVoci.getVoce().addAll(toVoceRicevutaTelematicaInputList(voci));
		return elencoVoci;
	}
}
