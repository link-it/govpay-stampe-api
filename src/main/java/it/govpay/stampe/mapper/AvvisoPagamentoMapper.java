package it.govpay.stampe.mapper;

import java.util.Map;

import org.mapstruct.Mapper;

import it.govpay.stampe.beans.PaymentNotice;
import it.govpay.stampe.config.LabelAvvisiConfiguration.LabelAvvisiProperties;
import it.govpay.stampe.model.v1.AvvisoPagamentoInput;

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

	public default AvvisoPagamentoInput toViolazioneAvvisoPagamentoInput(PaymentNotice paymentNotice, LabelAvvisiProperties labelAvvisiProperties) {
		Map<String, String> labelItaliano = getLabelLingua(paymentNotice.getLanguage(), labelAvvisiProperties);
		
		return null;
	}
}
