package it.govpay.stampe.fop;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.xml.namespace.QName;

import org.apache.fop.apps.MimeConstants;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.ActiveProfiles;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import jakarta.xml.bind.JAXBElement;

import it.govpay.stampe.Application;
import it.govpay.stampe.beans.PaymentNotice;
import it.govpay.stampe.config.LabelAvvisiConfiguration.LabelAvvisiProperties;
import it.govpay.stampe.mapper.AvvisoPagamentoMapper;
import it.govpay.stampe.model.v1.AvvisoPagamentoInput;
import it.govpay.stampe.model.v1.RataAvviso;
import it.govpay.stampe.test.utils.AvvisiPagamentoFactory;

/**
 * QR code e DataMatrix dell'avviso postale a rata unica stampato con FOP (issue #37): la pagina e'
 * resa come immagine dal motore e i codici, letti nei riquadri del layout RataUnicaPostale, devono
 * contenere i dati dell'avviso.
 */
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
class MotoreFopCodiciTest {

	private static final int DPI = 300;

	@Autowired
	MotoreFop motoreFop;

	@Autowired
	AvvisoPagamentoMapper avvisoPagamentoMapper;

	@Autowired
	@Qualifier("labelAvvisiProperties")
	LabelAvvisiProperties labelAvvisiProperties;

	@Autowired
	@Qualifier("jaxb2MarshallerAvvisoPagamento")
	Jaxb2Marshaller jaxb2Marshaller;

	@Autowired
	AvvisiPagamentoFactory avvisiPagamentoFactory;

	@Test
	void codiciLeggibiliNelBollettinoPostale() throws Exception {
		PaymentNotice avviso = this.avvisiPagamentoFactory.creaPaymentNoticeFull();
		avviso.setPostal(true);
		avviso.setSecondLanguage(null);
		AvvisoPagamentoInput input = this.avvisoPagamentoMapper.toPaymentNoticeAvvisoPagamentoInput(
				LoggerFactory.getLogger(MotoreFopCodiciTest.class), avviso, this.labelAvvisiProperties);
		PreparazioneAvviso.completa(input);
		RataAvviso rata = PreparazioneAvviso.rate(input).get(0);

		byte[] png = this.motoreFop.genera("avviso-postale.xsl", this.jaxb2Marshaller.getJaxbContext(),
				new JAXBElement<>(new QName("", "AvvisoPagamentoInput"), AvvisoPagamentoInput.class, input),
				MimeConstants.MIME_PNG, DPI);
		BufferedImage pagina = ImageIO.read(new ByteArrayInputStream(png));

		// riquadri del layout RataUnicaPostale in punti (x, y, lato), con un margine bianco intorno
		assertEquals(rata.getQrCode().trim(), leggi(pagina, 9, 445, 105, BarcodeFormat.QR_CODE));
		assertEquals(rata.getDataMatrix(), leggi(pagina, 490, 711, 85, BarcodeFormat.DATA_MATRIX));
	}

	private static String leggi(BufferedImage pagina, int x, int y, int lato, BarcodeFormat formato) throws Exception {
		double s = DPI / 72.0;
		int m = 10;
		int x0 = (int) Math.max(0, (x - m) * s);
		int y0 = (int) Math.max(0, (y - m) * s);
		int lato0 = (int) ((lato + 2 * m) * s);
		BufferedImage riquadro = pagina.getSubimage(x0, y0, Math.min(lato0, pagina.getWidth() - x0), Math.min(lato0, pagina.getHeight() - y0));
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(riquadro)));
		return new MultiFormatReader().decode(bitmap, Map.of(
				DecodeHintType.POSSIBLE_FORMATS, java.util.List.of(formato),
				DecodeHintType.TRY_HARDER, Boolean.TRUE)).getText();
	}
}
