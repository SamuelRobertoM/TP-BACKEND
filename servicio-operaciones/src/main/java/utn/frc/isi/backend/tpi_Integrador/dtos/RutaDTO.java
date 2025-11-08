package utn.frc.isi.backend.tpi_Integrador.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * DTO para mostrar información completa de una ruta
 * Incluye la lista de tramos que componen la ruta
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RutaDTO {
    
    private Long id; // ID de la ruta
    
    private Long solicitudId; // ID de la solicitud asociada
    
    private int cantidadTramos; // Cantidad de tramos en la ruta
    
    private int cantidadDepositos; // Cantidad de depósitos intermedios
    
    private double distanciaTotal; // Distancia total en kilómetros
    
    private double tiempoEstimadoTotal; // Tiempo estimado total en horas
    
    private double costoEstimado; // Costo estimado total
    
    private List<TramoDTO> tramos; // Lista de tramos que componen la ruta
}
