package utn.frc.isi.backend.tpi_Integrador.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar un Depósito en las respuestas de la API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositoDTO {
    
    private Long id;
    private String nombre;
    private String direccion;
    private double latitud;
    private double longitud;
}
