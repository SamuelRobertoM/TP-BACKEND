package utn.frc.isi.backend.tpi_Integrador.dtos.flota;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para recibir información de Camión desde servicio-flota
 * Refleja la estructura de CamionDTO en servicio-flota
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CamionDTO {
    
    private Long id;
    private String dominio;
    private String nombreTransportista;
    private String telefono;
    private double capacidadPeso;
    private double capacidadVolumen;
    private double consumoCombustiblePorKm;
    private boolean disponible;
    private double costoPorKm;
}
