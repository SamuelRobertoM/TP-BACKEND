package utn.frc.isi.backend.tpi_Integrador.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity // Marca esta clase como una entidad que se mapeará a una tabla en la BD
@Data   // Genera automáticamente getters, setters, toString, etc.
public class Ruta {

    @Id // Define el campo 'id' como la clave primaria de la tabla
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Indica que el ID será autogenerado por la base de datos
    private Long id;

    private String origen; // Punto de origen de la ruta

    private String destino; // Punto de destino de la ruta
    
    private Double latitudOrigen; // Latitud del origen
    
    private Double longitudOrigen; // Longitud del origen
    
    private Double latitudDestino; // Latitud del destino
    
    private Double longitudDestino; // Longitud del destino

    private double distanciaKm; // Distancia total en kilómetros

    private int tiempoEstimadoHoras; // Tiempo estimado en horas para completar la ruta
}