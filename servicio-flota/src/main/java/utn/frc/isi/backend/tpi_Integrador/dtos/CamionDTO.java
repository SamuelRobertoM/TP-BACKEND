package utn.frc.isi.backend.tpi_Integrador.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar un Camión en las respuestas de la API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
