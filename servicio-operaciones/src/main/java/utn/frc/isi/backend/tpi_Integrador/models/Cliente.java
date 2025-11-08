package utn.frc.isi.backend.tpi_Integrador.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity // Marca esta clase como una entidad que se mapeará a una tabla en la BD
@Data   // Genera automáticamente getters, setters, toString, etc.
public class Cliente {

    @Id // Define el campo 'id' como la clave primaria de la tabla
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Indica que el ID será autogenerado por la base de datos
    private Long id;

    private String nombre; // Nombre completo del cliente (datos personales y de contacto)

    private String email; // Dirección de correo electrónico (datos personales y de contacto)

    private String telefono; // Número de teléfono de contacto (datos personales y de contacto)

    private String direccion; // Dirección física del cliente

    private String cuit; // CUIT del cliente para facturación
}