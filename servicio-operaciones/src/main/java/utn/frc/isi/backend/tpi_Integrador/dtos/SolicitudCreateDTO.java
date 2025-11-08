package utn.frc.isi.backend.tpi_Integrador.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import utn.frc.isi.backend.tpi_Integrador.models.Cliente;

/**
 * DTO para crear una nueva solicitud de transporte
 * Según especificación del documento de diseño de API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudCreateDTO {

    @NotNull(message = "Los datos del contenedor son requeridos")
    @Valid
    private ContenedorCreateDTO contenedor; // REQUERIDO - Se crea el contenedor

    // Cliente nuevo (si es cliente nuevo)
    @Valid
    private Cliente cliente; // OPCIONAL - Si es cliente nuevo

    // Cliente existente (si cliente ya existe)
    private Long clienteId; // OPCIONAL - Si cliente ya existe

    // Observaciones adicionales
    private String observaciones; // OPCIONAL
    
    // Información de origen y destino para la ruta
    @NotNull(message = "La dirección de origen es requerida")
    private String direccionOrigen; // REQUERIDO
    
    @NotNull(message = "La latitud de origen es requerida")
    private Double latitudOrigen; // REQUERIDO
    
    @NotNull(message = "La longitud de origen es requerida")
    private Double longitudOrigen; // REQUERIDO
    
    @NotNull(message = "La dirección de destino es requerida")
    private String direccionDestino; // REQUERIDO
    
    @NotNull(message = "La latitud de destino es requerida")
    private Double latitudDestino; // REQUERIDO
    
    @NotNull(message = "La longitud de destino es requerida")
    private Double longitudDestino; // REQUERIDO
    
    /**
     * Validación personalizada: debe tener clienteId O cliente, pero no ambos
     */
    public boolean isValidClienteData() {
        return (clienteId != null && cliente == null) || 
               (clienteId == null && cliente != null);
    }
}