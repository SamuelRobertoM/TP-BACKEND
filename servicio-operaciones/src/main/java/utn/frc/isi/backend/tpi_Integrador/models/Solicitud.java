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
public class Solicitud {

    @Id // Define el campo 'id' como la clave primaria de la tabla
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Indica que el ID será autogenerado por la base de datos
    private Long id;

    private String fechaSolicitud; // Fecha cuando se realizó la solicitud

    private String estado; // Estado de la solicitud (ej: BORRADOR, PROGRAMADA, EN_TRANSITO, ENTREGADA)

    private String observaciones; // Observaciones adicionales de la solicitud

    @ManyToOne
    @JoinColumn(name = "contenedor_id") // Así se llamará la columna en la BD
    private Contenedor contenedor; // Contenedor asociado a la solicitud

    @ManyToOne
    @JoinColumn(name = "cliente_id") // Así se llamará la columna en la BD
    private Cliente cliente; // Cliente asociado a la solicitud

    @ManyToOne
    @JoinColumn(name = "ruta_id") // Así se llamará la columna en la BD
    private Ruta ruta; // Ruta asociada a la solicitud

    private double costoEstimado; // Costo estimado de la operación

    private double tiempoEstimado; // Tiempo estimado en horas

    private double costoFinal; // Costo final real de la operación

    private double tiempoReal; // Tiempo real en horas
}