package utn.frc.isi.backend.tpi_Integrador.dtos;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualizar un Cliente existente
 * Todos los campos son opcionales para permitir actualizaciones parciales
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteUpdateDTO {
    
    private String nombre;
    
    @Email(message = "El email debe ser válido")
    private String email;
    
    private String telefono;
    
    private String direccion;
    
    private String cuit;
}
