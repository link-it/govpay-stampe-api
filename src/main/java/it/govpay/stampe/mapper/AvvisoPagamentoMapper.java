package it.govpay.stampe.mapper;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.slf4j.Logger;

import it.govpay.stampe.beans.Instalment;
import it.govpay.stampe.beans.PaymentNotice;
import it.govpay.stampe.config.LabelAvvisiConfiguration.LabelAvvisiProperties;
import it.govpay.stampe.model.v1.AvvisoPagamentoInput;
import it.govpay.stampe.model.v1.PaginaAvvisoDoppia;
import it.govpay.stampe.model.v1.PaginaAvvisoMultipla;
import it.govpay.stampe.model.v1.PaginaAvvisoSingola;
import it.govpay.stampe.model.v1.PaginaAvvisoTripla;
import it.govpay.stampe.model.v1.PagineAvviso;
import it.govpay.stampe.model.v1.RataAvviso;

@Mapper(componentModel = "spring")
public interface AvvisoPagamentoMapper extends BaseAvvisoMapper {

	public default String nomePdf(PaymentNotice paymentNotice) {
		String noticeNumber = null;

		if(paymentNotice.getFull() != null) {
			noticeNumber = paymentNotice.getFull().getNoticeNumber();
		} else {
			noticeNumber = paymentNotice.getInstalments().get(0).getNoticeNumber();
		}

		return paymentNotice.getCreditor().getFiscalCode() + "_" + noticeNumber + ".pdf";
	}

	public default AvvisoPagamentoInput toPaymentNoticeAvvisoPagamentoInput(Logger logger, PaymentNotice paymentNotice, LabelAvvisiProperties labelAvvisiProperties) {
		Map<String, String> labelItaliano = getLabelLingua(paymentNotice.getLanguage(), labelAvvisiProperties);

		AvvisoPagamentoInput avvisoPagamentoInput = toPaymentNoticeAvvisoPagamentoInput(paymentNotice);

		if(avvisoPagamentoInput != null) {
			Boolean postal = paymentNotice.getPostal();

			if(avvisoPagamentoInput.getPagine() == null)
				avvisoPagamentoInput.setPagine(new PagineAvviso());

			if(postal != null && postal.booleanValue()) { // avviso postale
				creaRatePerAvvisoPostale(logger, paymentNotice, avvisoPagamentoInput);
			} else { // avviso semplice
				creaRatePerAvvisoSemplice(logger, paymentNotice, avvisoPagamentoInput);
			}
		}

		return avvisoPagamentoInput;
	}
	public default void creaRatePerAvvisoPostale(Logger logger, PaymentNotice paymentNotice, AvvisoPagamentoInput avvisoPagamentoInput) {
		// rata unica
		RataAvviso rataUnica = null;
		if(paymentNotice.getFull() != null) {
			rataUnica = amountToRata(paymentNotice.getFull());

			PaginaAvvisoSingola pagina = new PaginaAvvisoSingola();
			pagina.setRata(rataUnica);

			avvisoPagamentoInput.getPagine().getSingolaOrDoppiaOrTripla().add(pagina);
		}

		// rate
		List<Instalment> instalments = paymentNotice.getInstalments();
		if(instalments != null && !instalments.isEmpty()) {
			while(instalments.size() > 1 && instalments.size()%3 != 0) {
				Instalment v1 = instalments.remove(0);
				Instalment v2 = instalments.remove(0);
				PaginaAvvisoDoppia pagina = new PaginaAvvisoDoppia();
				pagina.getRata().add(instalmentToRata(v1));
				pagina.getRata().add(instalmentToRata(v2));
				avvisoPagamentoInput.getPagine().getSingolaOrDoppiaOrTripla().add(pagina);
			}

			while(instalments.size() > 1) {
				Instalment v1 = instalments.remove(0);
				Instalment v2 = instalments.remove(0);
				Instalment v3 = instalments.remove(0);

				PaginaAvvisoTripla pagina = new PaginaAvvisoTripla();
				pagina.getRata().add(instalmentToRata(v1));
				pagina.getRata().add(instalmentToRata(v2));
				pagina.getRata().add(instalmentToRata(v3));
				avvisoPagamentoInput.getPagine().getSingolaOrDoppiaOrTripla().add(pagina);
			}

			if(instalments.size() == 1) {
				Instalment v1 = instalments.remove(0);
				PaginaAvvisoSingola pagina = new PaginaAvvisoSingola();
				pagina.setRata(instalmentToRata(v1));
				avvisoPagamentoInput.getPagine().getSingolaOrDoppiaOrTripla().add(pagina);
			}
		}
	}

	public default void creaRatePerAvvisoSemplice(Logger log, PaymentNotice paymentNotice, AvvisoPagamentoInput avvisoPagamentoInput) {
		// rata unica
		RataAvviso rataUnica = null;
		if(paymentNotice.getFull() != null) {
			rataUnica = amountToRata(paymentNotice.getFull());
		}

		// rate
		List<Instalment> instalments = paymentNotice.getInstalments();
		if(instalments != null && !instalments.isEmpty()) {
			log.debug("Numero rate: [{}]", instalments.size());

			if(instalments.size() > 1) {

				if(this.isMultipla(instalments)) { // numero rate > 3
					log.debug("Abilito pagine multiple");	

					// pagina 1 con due rate se (#rate - 4 ) mod 9 ==0 || (#rate -8) mod 9 == 0
					if(isPaginaPrincipaleDoppia(instalments)) {
						log.debug("Selezionato layout pagina principale a 2 colonne");
						creaPaginaPrincipaleDoppia(avvisoPagamentoInput, rataUnica, instalments);
					} else {
						log.debug("Selezionato layout pagina principale a 3 colonne");
						creaPaginaPrincipaleTripla(avvisoPagamentoInput, rataUnica, instalments);
					}


					while(instalments.size() > 8) {
						log.debug("Inserisco una pagina con 9 rate");
						Instalment v1 = instalments.remove(0);
						Instalment v2 = instalments.remove(0);
						Instalment v3 = instalments.remove(0);
						Instalment v4 = instalments.remove(0);
						Instalment v5 = instalments.remove(0);
						Instalment v6 = instalments.remove(0);
						Instalment v7 = instalments.remove(0);
						Instalment v8 = instalments.remove(0);
						Instalment v9 = instalments.remove(0);

						PaginaAvvisoMultipla pagina = new PaginaAvvisoMultipla();
						// layout a 3 colonne
						pagina.setColonne(BigInteger.valueOf(3l));

						pagina.getRata().add(instalmentToRata(v1));
						pagina.getRata().add(instalmentToRata(v2));
						pagina.getRata().add(instalmentToRata(v3));
						pagina.getRata().add(instalmentToRata(v4));
						pagina.getRata().add(instalmentToRata(v5));
						pagina.getRata().add(instalmentToRata(v6));
						pagina.getRata().add(instalmentToRata(v7));
						pagina.getRata().add(instalmentToRata(v8));
						pagina.getRata().add(instalmentToRata(v9));
						avvisoPagamentoInput.getPagine().getSingolaOrDoppiaOrTripla().add(pagina);
					}

					// qui creo l'ultima pagina multipla sono rimasti un numero di pendenze da 2 a 8
					// quando ho 2 o 4 metto il layout a 2 colonne altrimenti a 3
					if(!instalments.isEmpty()) {
						log.debug("Inserisco pagina finale con {} rate", instalments.size());
						PaginaAvvisoMultipla pagina = new PaginaAvvisoMultipla();
						if(instalments.size() < 6 && instalments.size() %3 != 0) {
							log.debug("Selezionato layout pagina multipla finale a 2 colonne");
							// layout a 2 colonne
							pagina.setColonne(BigInteger.valueOf(2l));
						} else {
							log.debug("Selezionato layout pagina multipla finale a 3 colonne");
							// layout a 3 colonne
							pagina.setColonne(BigInteger.valueOf(3l));
						}

						while(!instalments.isEmpty()) {
							Instalment v1 = instalments.remove(0);
							pagina.getRata().add(instalmentToRata(v1));
						}

						avvisoPagamentoInput.getPagine().getSingolaOrDoppiaOrTripla().add(pagina);
					}
				} else {
					log.debug("Layout in pagina singola");
					// 2/3 rate in una sola pagina
					if(instalments.size()%3 != 0) { // 2 rate
						log.debug("Selezionato layout a 2 colonne");
						creaPaginaPrincipaleDoppia(avvisoPagamentoInput, rataUnica, instalments);
					} else { // 3 rate
						log.debug("Selezionato layout a 3 colonne");
						creaPaginaPrincipaleTripla(avvisoPagamentoInput, rataUnica, instalments);
					}
				}
			} else {
				Instalment v1 = instalments.remove(0);
				PaginaAvvisoSingola pagina = new PaginaAvvisoSingola();
				pagina.setRata(instalmentToRata(v1));
				avvisoPagamentoInput.getPagine().getSingolaOrDoppiaOrTripla().add(pagina);
				log.debug("Aggiunta rata unica [idPendenza: "+v1.getNoticeNumber()+"]");
			}
		} else {
			PaginaAvvisoSingola pagina = new PaginaAvvisoSingola();
			pagina.setRata(rataUnica);

			avvisoPagamentoInput.getPagine().getSingolaOrDoppiaOrTripla().add(pagina);
		}
	}

	public default void creaPaginaPrincipaleTripla(AvvisoPagamentoInput avvisoPagamentoInput, RataAvviso rataUnica, List<Instalment> instalments) {
		Instalment v1 = instalments.remove(0);
		Instalment v2 = instalments.remove(0);
		Instalment v3 = instalments.remove(0);

		PaginaAvvisoTripla pagina = new PaginaAvvisoTripla();
		pagina.getRata().add(instalmentToRata(v1));
		pagina.getRata().add(instalmentToRata(v2));
		pagina.getRata().add(instalmentToRata(v3));

		if(rataUnica != null) {
			pagina.setUnica(rataUnica);
		}

		avvisoPagamentoInput.getPagine().getSingolaOrDoppiaOrTripla().add(pagina);
	}

	public default void creaPaginaPrincipaleDoppia(AvvisoPagamentoInput avvisoPagamentoInput, RataAvviso rataUnica,	List<Instalment> instalments) {
		Instalment v1 = instalments.remove(0);
		Instalment v2 = instalments.remove(0);

		PaginaAvvisoDoppia pagina = new PaginaAvvisoDoppia();
		pagina.getRata().add(instalmentToRata(v1));
		pagina.getRata().add(instalmentToRata(v2));

		if(rataUnica != null) {
			pagina.setUnica(rataUnica);
		}

		avvisoPagamentoInput.getPagine().getSingolaOrDoppiaOrTripla().add(pagina);
	}

	@Mapping(target = "logoEnte", source="firstLogo", qualifiedByName = "mapLogo")
	@Mapping(target = "logoEnteSecondario", source="secondLogo", qualifiedByName = "mapLogo")
	@Mapping(target = "oggettoDelPagamento", source="title")
	@Mapping(target = "cfEnte", source="paymentNotice.creditor.fiscalCode")
	@Mapping(target = "enteCreditore", source="paymentNotice.creditor.businessName")
	@Mapping(target = "settoreEnte", source="paymentNotice.creditor.departmentName")
	@Mapping(target = "infoEnte", source="paymentNotice.creditor", qualifiedByName = "mapInfoEnte")
	@Mapping(target = "cbill", source="paymentNotice.creditor.cbillCode")
	@Mapping(target = "cfDestinatario", source="paymentNotice.debtor.fiscalCode")
	@Mapping(target = "nomeCognomeDestinatario", source="paymentNotice.debtor.fullName")
	@Mapping(target = "indirizzoDestinatario1", source="paymentNotice.debtor.addressLine1")
	@Mapping(target = "indirizzoDestinatario2", source="paymentNotice.debtor.addressLine2")
	@Mapping(target = "diPoste", source="paymentNotice.postal", qualifiedByName = "mapPostale")
	@Mapping(target = "delTuoEnte", source="paymentNotice.postal", qualifiedByName = "mapDelTuoEnte")
	public AvvisoPagamentoInput toPaymentNoticeAvvisoPagamentoInput(PaymentNotice paymentNotice);

	@Mapping(target = "importo", source="amount")
	@Mapping(target = "data", source="dueDate", qualifiedByName = "mapData")
	@Mapping(target = "codiceAvviso", source="noticeNumber", qualifiedByName = "mapNumeroAvviso")
	@Mapping(target = "qrCode", source="qrcode")
	@Mapping(target = "numeroRata", source="instalmentNumber")
	public RataAvviso instalmentToRata(Instalment instalment);
}
