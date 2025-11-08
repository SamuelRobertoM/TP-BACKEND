package utn.frc.isi.backend.tpi_Integrador.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity // Marca esta clase como una entidad que se mapeará a una tabla en la BD
@Data   // Genera automáticamente getters, setters, toString, etc.
public class DepositoReference {

    @Id // Define el campo 'id' como la clave primaria de la tabla
    private Long id; // ID del Deposito en el servicio-flota

    private String nombre; // Cache del nombre para consultas rápidas
}