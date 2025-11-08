package utn.frc.isi.backend.tpi_Integrador.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para actualizar la disponibilidad de un camión
 * Usado por el endpoint PATCH /api/camiones/{id}/disponibilidad
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisponibilidadDTO {
    
    @NotNull(message = "El estado de disponibilidad es obligatorio")
    private Boolean disponible;
}
