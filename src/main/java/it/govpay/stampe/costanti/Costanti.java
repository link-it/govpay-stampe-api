package it.govpay.stampe.costanti;

public class Costanti {

	private Costanti() {}

	public static final String PROPERY_NAME_COM_SUN_XML_BIND_XML_DECLARATION = "com.sun.xml.bind.xmlDeclaration";
	public static final String LOG_MSG_AVVISO_PAGAMENTO_INPUT = "AvvisoPagamentoInput: {}";
	
	public static final String PATTERN_DATA_GG_MM_AAAA = "dd/MM/yyyy";
	
	// root element elemento di input
	public static final String AVVISO_PAGAMENTO_ROOT_ELEMENT_NAME= "AvvisoPagamentoInput";
	public static final String VIOLAZIONE_CDS_ROOT_ELEMENT_NAME= "ViolazioneInput";

	public static final String VIOLAZIONE_CDS_DIR = "/violazioneCDS";

	public static final String AVVISO_PAGAMENTO_V2_DIR = "/avvisoPagamentoV2";

	public static final String DEL_TUO_ENTE_CREDITORE = "del tuo Ente Creditore";
	public static final String DI_POSTE = "di Poste Italiane";

	public static final String FILLER_DATAMATRIX = "            ";
	public static final String PATTERN_DATAMATRIX = "codfase=NBPA;{0}1P1{1}{2}{3}{4}{5}A";
	public static final String PATTERN_CODELINE = "18{0}12{1}10{2}3896";

	public static final Integer DATAMATRIX_LUNGHEZZA_CAMPO_IMPORTO = 10;
	public static final Integer DATAMATRIX_LUNGHEZZA_CAMPO_CAUSALE = 110;
	public static final Integer DATAMATRIX_LUNGHEZZA_CAMPO_ANAGRAFICA_DEBITORE = 40;
	public static final Integer DATAMATRIX_LUNGHEZZA_CAMPO_CF_DEBITORE = 16;

	public static final Integer AVVISO_LUNGHEZZA_CAMPO_CAUSALE = 60;
	public static final Integer AVVISO_LUNGHEZZA_CAMPO_INDIRIZZO_DESTINATARIO = 40;

	public static final String TIPO_RATA_SCONTATO = "scontato";
	public static final String TIPO_RATA_RIDOTTO = "ridotto";
	public static final String TIPO_RATA_ENTRO = "entro";
	public static final String TIPO_RATA_OLTRE = "oltre";
	
	// Ricevuta 
	// root element elemento di input
	public static final String RICEVUTA_TELEMATICA_ROOT_ELEMENT_NAME= "root";

	
	public static final String PAGAMENTO_ESEGUITO = "Eseguito";
	public static final String PAGAMENTO_NON_ESEGUITO = "Non eseguito";
	public static final String PAGAMENTO_PARZIALMENTE_ESEGUITO = "Parzialmente eseguito";
	public static final String DECORRENZA_TERMINI = "Decorrenza termini";
	public static final String DECORRENZA_TERMINI_PARZIALE = "Decorrenza termini parziale";
}
