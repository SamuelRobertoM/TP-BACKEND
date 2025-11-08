package utn.frc.isi.backend.tpi_Integrador.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para finalizar una solicitud (RF#9)
 * Actualmente no requiere campos, pero podría extenderse para incluir
 * observaciones finales, confirmaciones, etc.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinalizacionSolicitudDTO {
    // Observaciones opcionales al finalizar la solicitud
    private String observacionesFinales;
}
