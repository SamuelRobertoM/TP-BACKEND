package utn.frc.isi.backend.tpi_Integrador.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar una referencia a un Depósito
 * Usado en TramoDTO para mostrar información básica del depósito origen/destino
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositoReferenceDTO {
    private Long id;
    private String nombre;
    private String direccion;
    private Double latitud;
    private Double longitud;
}
