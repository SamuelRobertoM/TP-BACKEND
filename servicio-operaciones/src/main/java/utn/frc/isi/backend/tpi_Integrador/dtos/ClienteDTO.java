package utn.frc.isi.backend.tpi_Integrador.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar un Cliente en las respuestas de la API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {
    
    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private String direccion;
    private String cuit;
}
