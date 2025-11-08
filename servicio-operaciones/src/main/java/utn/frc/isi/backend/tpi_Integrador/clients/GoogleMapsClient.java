package utn.frc.isi.backend.tpi_Integrador.clients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import utn.frc.isi.backend.tpi_Integrador.dtos.googlemaps.Element;
import utn.frc.isi.backend.tpi_Integrador.dtos.googlemaps.GoogleDistanceMatrixResponse;

import java.util.Optional;

@Component
public class GoogleMapsClient {

    private static final Logger logger = LoggerFactory.getLogger(GoogleMapsClient.class);

    private final RestClient restClient;
    private final String apiKey;

    public GoogleMapsClient(@Qualifier("googleMapsRestClient") RestClient restClient,
                           @Value("${google.maps.api-key}") String apiKey) {
        this.restClient = restClient;
        this.apiKey = apiKey;
    }

    public Optional<Element> getDistance(String origen, String destino) {
        String uri = "/distancematrix/json?origins={origen}&destinations={destino}&units=metric&key={apiKey}";

        try {
            // Realizar la llamada GET a la API de Google Maps
            ResponseEntity<GoogleDistanceMatrixResponse> response = restClient
                    .get()
                    .uri(uri, origen, destino, apiKey)
                    .retrieve()
                    .toEntity(GoogleDistanceMatrixResponse.class);

            // Validar que la respuesta HTTP sea OK
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                GoogleDistanceMatrixResponse body = response.getBody();

                // Validar el contenido del body
                if ("OK".equals(body.getStatus()) &&
                    body.getRows() != null &&
                    !body.getRows().isEmpty() &&
                    body.getRows().get(0).getElements() != null &&
                    !body.getRows().get(0).getElements().isEmpty()) {

                    // Obtener el primer elemento
                    Element element = body.getRows().get(0).getElements().get(0);

                    // Validar el estado del elemento
                    if ("OK".equals(element.getStatus())) {
                        logger.info("Distancia obtenida exitosamente de {} a {}", origen, destino);
                        return Optional.of(element);
                    } else {
                        logger.warn("Estado del elemento no OK: {} para ruta {} -> {}", 
                                  element.getStatus(), origen, destino);
                    }
                } else {
                    logger.warn("Respuesta de API inválida: status={}, rows vacíos o elementos vacíos para {} -> {}", 
                              body.getStatus(), origen, destino);
                }
            } else {
                logger.error("Respuesta HTTP no OK: {} para {} -> {}", 
                           response.getStatusCode(), origen, destino);
            }

        } catch (HttpClientErrorException e) {
            logger.error("Error HTTP al llamar a Google Maps API: {} - {}", 
                       e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al llamar a Google Maps API: {}", e.getMessage(), e);
        }

        return Optional.empty();
    }
}
