package utn.frc.isi.backend.tpi_Integrador.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualizar una Solicitud existente
 * Todos los campos son opcionales para permitir actualizaciones parciales
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudUpdateDTO {
    
    private String fechaSolicitud;
    private String estado;
    private String observaciones;
    private Long contenedorId;
    private Long clienteId;
    private Long rutaId;
    private Double costoEstimado;
    private Double tiempoEstimado;
    private Double costoFinal;
    private Double tiempoReal;
}
