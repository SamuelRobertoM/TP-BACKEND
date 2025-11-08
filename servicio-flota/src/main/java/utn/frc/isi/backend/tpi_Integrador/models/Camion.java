package utn.frc.isi.backend.tpi_Integrador.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity // Marca esta clase como una entidad que se mapeará a una tabla en la BD
@Data   // Genera automáticamente getters, setters, toString, etc.
public class Camion {

    @Id // Define el campo 'id' como la clave primaria de la tabla
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Indica que el ID será autogenerado por la base de datos
    private Long id;

    private String dominio; // Patente o identificador único del camión 

    private String nombreTransportista;

    private String telefono;

    private double capacidadPeso; // Capacidad máxima en peso que puede transportar 

    private double capacidadVolumen; // Capacidad máxima en volumen que puede transportar 
    
    private double consumoCombustiblePorKm; // Consumo para calcular costos 

    private boolean disponible; // Para saber si está libre u ocupado

    // Podríamos agregar más adelante un campo para los costos de traslado por km
    private double costoPorKm; 
}