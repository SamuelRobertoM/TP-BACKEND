package utn.frc.isi.backend.tpi_Integrador.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para consultar el estado y ubicación actual de un contenedor
 * Usado para el seguimiento (tracking) de contenedores por parte del cliente
 * RF#2: Consultar estado del transporte
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContenedorEstadoDTO {
    
    private Long id; // ID del contenedor
    
    private String numero; // Número identificador del contenedor (ej: "CONT-001")
    
    private String estado; // Estado actual (EN_ORIGEN, EN_VIAJE, EN_DEPOSITO, ENTREGADO)
    
    private String ubicacionActual; // Descripción textual de la ubicación actual
    
    // Información del cliente propietario
    private String nombreCliente;
    
    // Información de la solicitud asociada
    private Long solicitudId;
}
