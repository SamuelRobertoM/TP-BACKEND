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
public class Contenedor {

    @Id // Define el campo 'id' como la clave primaria de la tabla
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Indica que el ID será autogenerado por la base de datos
    private Long id;

    private String numero; // Número identificador único del contenedor

    private String tipo; // Tipo de contenedor (ej: STANDARD, REFRIGERADO, etc.)

    private double peso; // Peso del contenedor en kilogramos

    private double volumen; // Volumen del contenedor en metros cúbicos

    private String estado; // Estado del contenedor (ej: EN_ORIGEN, EN_VIAJE, EN_DEPOSITO, ENTREGADO)

    @ManyToOne
    @JoinColumn(name = "cliente_id") // Así se llamará la columna en la BD
    private Cliente cliente; // Cliente asociado al contenedor
}