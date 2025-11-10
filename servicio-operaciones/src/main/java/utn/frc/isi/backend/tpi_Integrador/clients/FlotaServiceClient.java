package utn.frc.isi.backend.tpi_Integrador.clients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import utn.frc.isi.backend.tpi_Integrador.dtos.flota.CamionDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.flota.TarifaDTO;

import java.util.Optional;

/**
 * Cliente para comunicarse con el microservicio servicio-flota
 * Obtiene información de Tarifas y Camiones usando RestClient
 */
@Component
public class FlotaServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(FlotaServiceClient.class);
    private final RestClient restClient;

    public FlotaServiceClient(@Qualifier("flotaRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Obtiene la tarifa activa vigente desde servicio-flota
     * @return Optional con TarifaDTO si existe una tarifa activa
     */
    public Optional<TarifaDTO> obtenerTarifaActiva() {
        String uri = "/api/tarifas/actual";
        try {
            logger.debug("Consultando tarifa activa a servicio-flota: {}", uri);
            
            ResponseEntity<TarifaDTO> response = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .toEntity(TarifaDTO.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                logger.info("Tarifa activa obtenida exitosamente: ID {}", response.getBody().getId());
                return Optional.of(response.getBody());
            } else {
                logger.error("Error al obtener tarifa activa de servicio-flota. Status: {}", response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            logger.error("Error HTTP al obtener tarifa activa: {} - {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            logger.error("Error inesperado al obtener tarifa activa desde servicio-flota", e);
        }
        return Optional.empty();
    }

    /**
     * Obtiene información de un camión específico desde servicio-flota
     * @param camionId ID del camión a consultar
     * @return Optional con CamionDTO si el camión existe
     */
    public Optional<CamionDTO> obtenerCamionPorId(Long camionId) {
        String uri = "/api/camiones/{camionId}";
        try {
            logger.debug("Consultando camión {} a servicio-flota", camionId);
            
            ResponseEntity<CamionDTO> response = restClient.get()
                    .uri(uri, camionId)
                    .retrieve()
                    .toEntity(CamionDTO.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                logger.info("Camión {} obtenido exitosamente: {}", camionId, response.getBody().getDominio());
                return Optional.of(response.getBody());
            } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                logger.warn("Camión con ID {} no encontrado en servicio-flota.", camionId);
            } else {
                logger.error("Error al obtener camión {} de servicio-flota. Status: {}", camionId, response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() != HttpStatus.NOT_FOUND) {
                logger.error("Error HTTP al obtener camión {}: {} - {}", camionId, e.getStatusCode(), e.getResponseBodyAsString(), e);
            } else {
                logger.warn("Camión con ID {} no encontrado en servicio-flota (404).", camionId);
            }
        } catch (Exception e) {
            logger.error("Error inesperado al obtener camión {} desde servicio-flota", camionId, e);
        }
        return Optional.empty();
    }
    
    /**
     * Actualiza la disponibilidad de un camión en servicio-flota
     * Se llama cuando un tramo finaliza para liberar el camión
     * 
     * @param camionId ID del camión a actualizar
     * @param disponible true para marcar como disponible, false para ocupado
     * @throws RuntimeException si la actualización falla
     */
    public void actualizarDisponibilidadCamion(Long camionId, boolean disponible) {
        String uri = "/api/camiones/{camionId}/disponibilidad";
        try {
            logger.debug("Actualizando disponibilidad del camión {} a: {}", camionId, disponible);
            
            // Crear el body con la nueva disponibilidad
            var requestBody = new java.util.HashMap<String, Boolean>();
            requestBody.put("disponible", disponible);
            
            ResponseEntity<Void> response = restClient.patch()
                    .uri(uri, camionId)
                    .body(requestBody)
                    .retrieve()
                    .toBodilessEntity();

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Disponibilidad del camión {} actualizada exitosamente a: {}", camionId, disponible);
            } else {
                logger.error("Error al actualizar disponibilidad del camión {}. Status: {}", camionId, response.getStatusCode());
                throw new RuntimeException("No se pudo actualizar disponibilidad del camión " + camionId);
            }
        } catch (HttpClientErrorException e) {
            logger.error("Error HTTP al actualizar disponibilidad del camión {}: {} - {}", 
                        camionId, e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("Error al actualizar disponibilidad del camión " + camionId + ": " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado al actualizar disponibilidad del camión {} en servicio-flota", camionId, e);
            throw new RuntimeException("Error inesperado al actualizar disponibilidad del camión " + camionId, e);
        }
    }
}
