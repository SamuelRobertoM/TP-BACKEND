package utn.frc.isi.backend.tpi_Integrador.dtos;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualizar una Ruta existente
 * Todos los campos son opcionales para permitir actualizaciones parciales
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RutaUpdateDTO {
    
    private String origen;
    
    private String destino;
    
    private Double latitudOrigen;
    
    private Double longitudOrigen;
    
    private Double latitudDestino;
    
    private Double longitudDestino;
    
    @Positive(message = "La distancia debe ser mayor a 0")
    private Double distanciaKm;
    
    @Positive(message = "El tiempo estimado debe ser mayor a 0")
    private Integer tiempoEstimadoHoras;
}
