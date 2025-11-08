package utn.frc.isi.backend.tpi_Integrador.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar coordenadas geográficas (GPS)
 * Usado en rutas y tramos para indicar ubicaciones específicas
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coordenada {
    
    private Double latitud;  // Latitud (ej: -31.4135)
    private Double longitud; // Longitud (ej: -64.1810)
}
