package utn.frc.isi.backend.tpi_Integrador.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity // Marca esta clase como una entidad que se mapeará a una tabla en la BD
@Data   // Genera automáticamente getters, setters, toString, etc.
public class CamionReference {

    @Id // Define el campo 'id' como la clave primaria de la tabla
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Indica que el ID será autogenerado por la base de datos
    private Long id;

    private String dominio; // Patente o identificador único del camión
    
    private double capacidadPeso; // Capacidad de carga en kg
    
    private double capacidadVolumen; // Capacidad de volumen en m³
    
    private boolean disponible; // Indica si el camión está disponible para asignación

    // Esta es una referencia simplificada al camión del servicio-flota
    // En un escenario real de microservicios, esto se manejaría de forma diferente
}