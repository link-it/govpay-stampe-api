package it.govpay.stampe.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import it.govpay.stampe.Application;
import it.govpay.stampe.test.costanti.Costanti;

/**
 * Verifica end-to-end della tracciatura di govpay-common (BP-LOG-3) su
 * stampe-api: ogni risposta porta un transaction id generato dal componente e un
 * correlation id che riusa quello del chiamante quando presente.
 * <p>
 * Il contesto Spring si avvia con le autoconfigurazioni DataSource/JPA escluse
 * in {@link Application}: govpay-common porta {@code spring-boot-starter-data-jpa}
 * ma stampe-api non ha base dati. Che questo test parta e' anche la verifica che
 * l'esclusione sia sufficiente.
 */
@SpringBootTest(classes = Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Test Tracciatura Transaction ID / Correlation ID")
class TracciaturaIntegrationTest {

    private static final String HEADER_TRANSACTION_ID = "X-Transaction-ID";
    private static final String HEADER_TRANSACTION_ID_LEGACY = "X-Govpay-IdTransazione";
    private static final String HEADER_CORRELATION_ID = "X-Correlation-ID";
    private static final String HEADER_REQUEST_ID = "X-Request-ID";

    @LocalServerPort
    private int serverPort;

    @Value("${spring.mvc.servlet.path:}")
    private String servletPath;

    private final HttpClient http = HttpClient.newHttpClient();

    @Test
    @DisplayName("La risposta espone transaction id e correlation id generati")
    void rispostaEsponeIdentificativiGenerati() throws Exception {
        HttpResponse<String> response = get(null, null);

        String transactionId = header(response, HEADER_TRANSACTION_ID);
        String correlationId = header(response, HEADER_CORRELATION_ID);

        assertNotNull(UUID.fromString(transactionId));
        assertNotNull(UUID.fromString(correlationId));
        assertNotEquals(transactionId, correlationId);

        assertEquals(transactionId, header(response, HEADER_TRANSACTION_ID_LEGACY));
        assertEquals(correlationId, header(response, HEADER_REQUEST_ID));
    }

    @Test
    @DisplayName("Il correlation id del chiamante viene riusato")
    void correlationIdDelChiamanteRiusato() throws Exception {
        HttpResponse<String> response = get(HEADER_CORRELATION_ID, "flusso-esterno-1");

        assertEquals("flusso-esterno-1", header(response, HEADER_CORRELATION_ID));
        assertEquals("flusso-esterno-1", header(response, HEADER_REQUEST_ID));
    }

    @Test
    @DisplayName("Anche X-Request-ID e' accettato come correlation id")
    void requestIdAccettatoComeCorrelationId() throws Exception {
        HttpResponse<String> response = get(HEADER_REQUEST_ID, "da-gateway");

        assertEquals("da-gateway", header(response, HEADER_CORRELATION_ID));
    }

    @Test
    @DisplayName("Il transaction id non e' imponibile dal client")
    void transactionIdNonImponibileDalClient() throws Exception {
        HttpResponse<String> response = get(HEADER_TRANSACTION_ID, "imposto-dal-client");

        String transactionId = header(response, HEADER_TRANSACTION_ID);
        assertNotEquals("imposto-dal-client", transactionId);
        assertNotNull(UUID.fromString(transactionId));
    }

    @Test
    @DisplayName("Un correlation id con caratteri non ammessi viene scartato")
    void correlationIdNonConformeScartato() throws Exception {
        HttpResponse<String> response = get(HEADER_CORRELATION_ID, "valore con spazi");

        assertNotEquals("valore con spazi", header(response, HEADER_CORRELATION_ID));
    }

    private static String header(HttpResponse<String> response, String nome) {
        String valore = response.headers().firstValue(nome).orElse(null);
        assertTrue(valore != null && !valore.isBlank(),
                "Header " + nome + " assente o vuoto nella risposta");
        return valore;
    }

    /**
     * Gli endpoint di stampe-api sono tutti POST: una GET risponde 405, ma il
     * filtro di tracciatura gira prima del dispatch, quindi gli header ci sono
     * comunque. E' la verifica che la tracciatura copra anche le risposte di
     * errore, dove serve di piu' per la diagnosi.
     */
    private HttpResponse<String> get(String headerNome, String headerValore) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + serverPort + servletPath + Costanti.STANDARD_PATH))
                .GET();
        if (headerNome != null) {
            builder.header(headerNome, headerValore);
        }
        return http.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }
}
