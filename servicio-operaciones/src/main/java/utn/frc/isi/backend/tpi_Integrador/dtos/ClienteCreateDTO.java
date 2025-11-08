package utn.frc.isi.backend.tpi_Integrador.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear un nuevo Cliente
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteCreateDTO {
    
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    private String email;
    
    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;
    
    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;
    
    @NotBlank(message = "El CUIT es obligatorio")
    private String cuit;
}
