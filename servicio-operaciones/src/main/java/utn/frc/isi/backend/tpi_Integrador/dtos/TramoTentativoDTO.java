package utn.frc.isi.backend.tpi_Integrador.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para representar un tramo tentativo en una ruta propuesta
 * Usado en RF#3 para consultar rutas tentativas antes de asignar una ruta definitiva
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TramoTentativoDTO {
    
    private int orden; // Orden del tramo en la ruta (1, 2, 3...)
    
    private String tipo; // Tipo de tramo (ej: "ORIGEN-DESTINO", "ORIGEN-DEPOSITO", "DEPOSITO-DESTINO")
    
    private Coordenada puntoInicio; // Coordenadas GPS del punto de inicio
    
    private Coordenada puntoFin; // Coordenadas GPS del punto de fin
    
    private double distanciaKm; // Distancia del tramo en kilómetros
    
    private double tiempoEstimadoHoras; // Tiempo estimado para completar el tramo en horas
    
    private double costoAproximado; // Costo aproximado del tramo en pesos
    
    // Información adicional opcional
    private String observaciones; // Notas o advertencias sobre el tramo
}
