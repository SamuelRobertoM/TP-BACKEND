package utn.frc.isi.backend.tpi_Integrador.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import java.time.LocalDateTime;

@Entity // Marca esta clase como una entidad que se mapeará a una tabla en la BD
@Data   // Genera automáticamente getters, setters, toString, etc.
public class Tramo {

    @Id // Define el campo 'id' como la clave primaria de la tabla
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Indica que el ID será autogenerado por la base de datos
    private Long id;
    
    private int orden; // Orden del tramo en la ruta (1, 2, 3...)
    
    private String tipo; // Tipo de tramo (ej: "ORIGEN-DESTINO", "ORIGEN-DEPOSITO", "DEPOSITO-DESTINO")
    
    private String estado; // Estado del tramo (ej: "PENDIENTE", "EN_PROCESO", "COMPLETADO")

    private String puntoInicio; // Punto de inicio del tramo

    private String puntoFin; // Punto de finalización del tramo
    
    private Double latitudInicio; // Latitud del punto de inicio
    
    private Double longitudInicio; // Longitud del punto de inicio
    
    private Double latitudFin; // Latitud del punto de fin
    
    private Double longitudFin; // Longitud del punto de fin

    private double distanciaKm; // Distancia del tramo en kilómetros

    private int tiempoEstimadoHoras; // Tiempo estimado en horas para el tramo
    
    private LocalDateTime fechaEstimadaInicio; // Fecha estimada de inicio del tramo
    
    private LocalDateTime fechaEstimadaFin; // Fecha estimada de finalización del tramo
    
    private LocalDateTime fechaRealInicio; // Fecha real de inicio del tramo
    
    private LocalDateTime fechaRealFin; // Fecha real de finalización del tramo
    
    private double costoReal; // Costo real calculado al finalizar el tramo

    @ManyToOne
    @JoinColumn(name = "ruta_id") // Así se llamará la columna en la BD
    private Ruta ruta; // Ruta a la que pertenece este tramo

    @ManyToOne
    @JoinColumn(name = "camion_reference_id") // Así se llamará la columna en la BD
    private CamionReference camionReference; // Referencia al camión asignado al tramo

    @ManyToOne
    @JoinColumn(name = "deposito_origen_id") // Depósito de origen del tramo (opcional)
    private DepositoReference depositoOrigen; // Referencia al depósito de origen

    @ManyToOne
    @JoinColumn(name = "deposito_destino_id") // Depósito de destino del tramo (opcional)
    private DepositoReference depositoDestino; // Referencia al depósito de destino
}