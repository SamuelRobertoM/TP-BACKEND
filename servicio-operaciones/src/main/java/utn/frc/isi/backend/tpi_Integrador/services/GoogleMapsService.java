package utn.frc.isi.backend.tpi_Integrador.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import utn.frc.isi.backend.tpi_Integrador.clients.GoogleMapsClient;
import utn.frc.isi.backend.tpi_Integrador.dtos.googlemaps.Element;

import java.util.Optional;

@Service
public class GoogleMapsService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleMapsService.class);

    private final GoogleMapsClient googleMapsClient;

    public GoogleMapsService(GoogleMapsClient googleMapsClient) {
        this.googleMapsClient = googleMapsClient;
    }

    /**
     * Obtiene información de distancia y duración entre dos puntos geográficos
     * usando la API de Google Maps Distance Matrix.
     * 
     * @param origen Coordenadas de origen en formato "lat,lng" (ej: "-31.4201,-64.1888")
     * @param destino Coordenadas de destino en formato "lat,lng" (ej: "-34.6037,-58.3816")
     * @return Optional con Element que contiene distancia y duración, vacío si falla
     */
    public Optional<Element> obtenerInformacionDistancia(String origen, String destino) {
        logger.debug("Solicitando información de distancia de {} a {}", origen, destino);
        return googleMapsClient.getDistance(origen, destino);
    }
}
