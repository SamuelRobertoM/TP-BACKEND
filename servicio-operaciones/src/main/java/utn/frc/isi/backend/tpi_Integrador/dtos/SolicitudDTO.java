package utn.frc.isi.backend.tpi_Integrador.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar una Solicitud en las respuestas de la API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudDTO {
    
    private Long id;
    private String fechaSolicitud;
    private String estado;
    private String observaciones;
    private Long contenedorId; // Solo el ID del contenedor
    private Long clienteId; // Solo el ID del cliente
    private RutaDTO ruta; // Relación con Ruta
    private double costoEstimado;
    private double tiempoEstimado;
    private double costoFinal;
    private double tiempoReal;
}
