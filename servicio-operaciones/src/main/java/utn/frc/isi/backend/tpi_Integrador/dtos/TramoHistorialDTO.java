package utn.frc.isi.backend.tpi_Integrador.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para mostrar el historial resumido de un tramo
 * Usado en el seguimiento de solicitudes para mostrar el estado de cada tramo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TramoHistorialDTO {
    
    private int orden; // Orden del tramo en la ruta (1, 2, 3...)
    
    private String tipo; // Tipo de tramo (ORIGEN_DEPOSITO, DEPOSITO_DEPOSITO, DEPOSITO_DESTINO)
    
    private String estado; // Estado actual del tramo (PENDIENTE, ASIGNADO, INICIADO, FINALIZADO)
    
    private String puntoInicio; // Descripción del punto de inicio
    
    private String puntoFin; // Descripción del punto de fin
    
    private LocalDateTime fechaHoraInicio; // Fecha/hora de inicio real del tramo
    
    private LocalDateTime fechaHoraFin; // Fecha/hora de finalización real del tramo
    
    private String camion; // Dominio del camión asignado (ej: "ABC-123")
}
