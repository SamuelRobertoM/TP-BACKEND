package utn.frc.isi.backend.tpi_Integrador.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * DTO para representar una ruta tentativa completa con todos sus tramos
 * RF#3: Consultar rutas tentativas con cálculos de costo, tiempo y distancia
 * Permite al operador evaluar diferentes opciones antes de asignar una ruta definitiva
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RutaTentativaDTO {
    
    private List<TramoTentativoDTO> tramos; // Lista ordenada de tramos que componen la ruta
    
    private double costoEstimadoTotal; // Costo total estimado de la ruta completa
    
    private double tiempoEstimadoTotal; // Tiempo total estimado en horas
    
    private double distanciaTotal; // Distancia total en kilómetros
    
    private int cantidadTramos; // Cantidad de tramos que componen la ruta
    
    private int cantidadDepositos; // Cantidad de depósitos intermedios (0 = directo)
    
    private String tipoRuta; // Tipo de ruta: "DIRECTA", "CON_DEPOSITOS", "MULTIPLE_TRAMOS"
    
    private String descripcion; // Descripción general de la ruta
}
