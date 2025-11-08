package utn.frc.isi.backend.tpi_Integrador.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO para crear un nuevo tramo en una ruta
 * Usado en RF#4 para asignar una ruta definitiva a una solicitud
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TramoCreateDTO {
    
    private int orden; // Orden del tramo en la ruta (1, 2, 3...)
    
    private String tipo; // Tipo de tramo (ej: "ORIGEN-DESTINO", "ORIGEN-DEPOSITO", "DEPOSITO-DESTINO")
    
    private double latitudInicio; // Latitud del punto de inicio
    
    private double longitudInicio; // Longitud del punto de inicio
    
    private double latitudFin; // Latitud del punto de fin
    
    private double longitudFin; // Longitud del punto de fin
    
    private Long depositoOrigenId; // ID del depósito de origen (si aplica)
    
    private Long depositoDestinoId; // ID del depósito de destino (si aplica)
    
    private LocalDateTime fechaEstimadaInicio; // Fecha estimada de inicio del tramo
    
    private LocalDateTime fechaEstimadaFin; // Fecha estimada de finalización del tramo
}
