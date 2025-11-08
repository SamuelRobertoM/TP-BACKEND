package utn.frc.isi.backend.tpi_Integrador.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar un Contenedor en las respuestas de la API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContenedorDTO {
    
    private Long id;
    private String numero;
    private String tipo;
    private double peso;
    private double volumen;
    private String estado;
    private ClienteDTO cliente; // Relación con Cliente
}
