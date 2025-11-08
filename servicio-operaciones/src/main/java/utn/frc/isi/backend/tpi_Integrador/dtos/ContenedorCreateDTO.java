package utn.frc.isi.backend.tpi_Integrador.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para crear un nuevo contenedor
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContenedorCreateDTO {

    @NotBlank(message = "El número del contenedor es requerido")
    private String numero;

    @NotBlank(message = "El tipo de contenedor es requerido")
    private String tipo; // STANDARD, REFRIGERADO, etc.

    @NotNull(message = "El peso es requerido")
    @Positive(message = "El peso debe ser positivo")
    private Double peso; // en kg

    @NotNull(message = "El volumen es requerido")
    @Positive(message = "El volumen debe ser positivo")
    private Double volumen; // en m³
    
    @NotBlank(message = "El estado es obligatorio")
    private String estado; // EN_ORIGEN, EN_VIAJE, EN_DEPOSITO, ENTREGADO
    
    @NotNull(message = "El cliente es obligatorio")
    private Long clienteId; // ID del cliente asociado
}