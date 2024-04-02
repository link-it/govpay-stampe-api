package it.govpay.stampe.mapper;

import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import it.govpay.stampe.beans.PaymentNotice;
import it.govpay.stampe.config.LabelAvvisiConfiguration.LabelAvvisiProperties;
import it.govpay.stampe.model.v2.PagineAvviso;
import it.govpay.stampe.model.v2.AvvisoPagamentoInput;

@Mapper(componentModel = "spring")
public interface AvvisoPagamentoBilingueMapper extends BaseAvvisoMapper{
	
	public default String nomePdf(PaymentNotice paymentNotice) {
		String noticeNumber = null;
		
		if(paymentNotice.getFull() != null) {
			noticeNumber = paymentNotice.getFull().getNoticeNumber();
		} else {
			noticeNumber = paymentNotice.getInstalments().get(0).getNoticeNumber();
		}
		
		return paymentNotice.getCreditor().getFiscalCode() + "_" + noticeNumber + ".pdf";
	}
	
	public default AvvisoPagamentoInput toPaymentNoticeAvvisoPagamentoInput(PaymentNotice paymentNotice, LabelAvvisiProperties labelAvvisiProperties) {
		Map<String, String> labelItaliano = getLabelLingua(paymentNotice.getLanguage(), labelAvvisiProperties);
		
		AvvisoPagamentoInput avvisoPagamentoInput = toPaymentNoticeAvvisoPagamentoInput(paymentNotice);

		if(avvisoPagamentoInput != null) {
			
			
			if(avvisoPagamentoInput.getPagine() == null)
				avvisoPagamentoInput.setPagine(new PagineAvviso());
		}
		
		return avvisoPagamentoInput;
	}
	
	@Mapping(target = "logoEnte", source="firstLogo", qualifiedByName = "mapLogo")
	@Mapping(target = "logoEnteSecondario", source="secondLogo", qualifiedByName = "mapLogo")
//	@Mapping(target = "oggettoDelPagamento", source="title")
	@Mapping(target = "cfEnte", source="paymentNotice.creditor.fiscalCode")
	@Mapping(target = "enteCreditore", source="paymentNotice.creditor.businessName")
	@Mapping(target = "settoreEnte", source="paymentNotice.creditor.departmentName")
	@Mapping(target = "infoEnte", source="paymentNotice.creditor", qualifiedByName = "mapInfoEnte")
	@Mapping(target = "cbill", source="paymentNotice.creditor.cbillCode")
	@Mapping(target = "cfDestinatario", source="paymentNotice.debtor.fiscalCode")
	@Mapping(target = "nomeCognomeDestinatario", source="paymentNotice.debtor.fullName")
	@Mapping(target = "indirizzoDestinatario1", source="paymentNotice.debtor.addressLine1")
	@Mapping(target = "indirizzoDestinatario2", source="paymentNotice.debtor.addressLine2")
//	@Mapping(target = "diPoste", source="paymentNotice.postal", qualifiedByName = "mapPostale")
//	@Mapping(target = "delTuoEnte", source="paymentNotice.postal", qualifiedByName = "mapDelTuoEnte")
	public AvvisoPagamentoInput toPaymentNoticeAvvisoPagamentoInput(PaymentNotice paymentNotice);
}
