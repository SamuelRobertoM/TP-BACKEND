package utn.frc.isi.backend.tpi_Integrador.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear un nuevo Camión
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CamionCreateDTO {
    
    @NotBlank(message = "El dominio es obligatorio")
    private String dominio;
    
    @NotBlank(message = "El nombre del transportista es obligatorio")
    private String nombreTransportista;
    
    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;
    
    @NotNull(message = "La capacidad de peso es obligatoria")
    @Positive(message = "La capacidad de peso debe ser positiva")
    private Double capacidadPeso;
    
    @NotNull(message = "La capacidad de volumen es obligatoria")
    @Positive(message = "La capacidad de volumen debe ser positiva")
    private Double capacidadVolumen;
    
    @NotNull(message = "El consumo de combustible es obligatorio")
    @Positive(message = "El consumo de combustible debe ser positivo")
    private Double consumoCombustiblePorKm;
    
    // Disponible es opcional, por defecto será true
    private Boolean disponible = true;
    
    @NotNull(message = "El costo por km es obligatorio")
    @Positive(message = "El costo por km debe ser positivo")
    private Double costoPorKm;
}
