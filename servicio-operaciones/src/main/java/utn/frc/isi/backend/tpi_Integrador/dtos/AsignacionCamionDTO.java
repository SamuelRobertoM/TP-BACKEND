package utn.frc.isi.backend.tpi_Integrador.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para asignar un camión a un tramo
 * Usado en RF#6 para vincular un vehículo a un segmento de ruta
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignacionCamionDTO {
    
    @NotNull(message = "El ID del camión es requerido")
    private Long camionId; // ID del camión en el servicio-flota (referenciado en CamionReference)
}
