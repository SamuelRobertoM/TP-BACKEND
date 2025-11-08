package utn.frc.isi.backend.tpi_Integrador.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * DTO para consultar el estado completo de una solicitud de transporte
 * Incluye estado del contenedor e historial de tramos
 * RF#2: Consultar estado del transporte
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudEstadoDTO {
    
    private Long id; // ID de la solicitud
    
    private String numero; // Número de solicitud (generado automáticamente)
    
    private String estado; // Estado de la solicitud (BORRADOR, PROGRAMADA, EN_TRANSITO, ENTREGADA)
    
    private ContenedorEstadoDTO contenedor; // Estado del contenedor asociado
    
    private RutaDTO rutaActual; // Información de la ruta asignada
    
    private List<TramoHistorialDTO> historialTramos; // Historial cronológico de tramos
    
    private double progreso; // Porcentaje de progreso (0-100)
    
    private String etaDestino; // Tiempo estimado de llegada al destino
}
