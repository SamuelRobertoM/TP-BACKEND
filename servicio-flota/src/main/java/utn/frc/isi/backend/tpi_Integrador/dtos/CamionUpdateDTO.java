package utn.frc.isi.backend.tpi_Integrador.dtos;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualizar un Camión existente
 * Todos los campos son opcionales para permitir actualizaciones parciales
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CamionUpdateDTO {
    
    private String nombreTransportista;
    
    private String telefono;
    
    @Positive(message = "La capacidad de peso debe ser positiva")
    private Double capacidadPeso;
    
    @Positive(message = "La capacidad de volumen debe ser positiva")
    private Double capacidadVolumen;
    
    @Positive(message = "El consumo de combustible debe ser positivo")
    private Double consumoCombustiblePorKm;
    
    private Boolean disponible;
    
    @Positive(message = "El costo por km debe ser positivo")
    private Double costoPorKm;
}
