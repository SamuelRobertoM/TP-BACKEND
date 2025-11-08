package utn.frc.isi.backend.tpi_Integrador.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear un nuevo Depósito
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositoCreateDTO {
    
    @NotBlank(message = "El nombre del depósito es obligatorio")
    private String nombre;
    
    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;
    
    @NotNull(message = "La latitud es obligatoria")
    private Double latitud;
    
    @NotNull(message = "La longitud es obligatoria")
    private Double longitud;
}
