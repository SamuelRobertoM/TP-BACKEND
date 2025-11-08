package utn.frc.isi.backend.tpi_Integrador.dtos;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualizar un Contenedor existente
 * Todos los campos son opcionales para permitir actualizaciones parciales
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContenedorUpdateDTO {
    
    private String numero;
    
    private String tipo;
    
    @Positive(message = "El peso debe ser positivo")
    private Double peso;
    
    @Positive(message = "El volumen debe ser positivo")
    private Double volumen;
    
    private String estado;
    
    private Long clienteId;
}
