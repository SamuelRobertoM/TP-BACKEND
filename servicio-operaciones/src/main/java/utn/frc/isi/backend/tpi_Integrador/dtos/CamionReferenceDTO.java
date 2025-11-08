package utn.frc.isi.backend.tpi_Integrador.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar una referencia a un Camión
 * Usado en TramoDTO para mostrar información básica del camión asignado
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CamionReferenceDTO {
    private Long id;
    private String dominio;
    private String nombreTransportista;
    private boolean disponible;
    private double capacidadPeso;
    private double capacidadVolumen;
}
