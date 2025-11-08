package utn.frc.isi.backend.tpi_Integrador.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualizar un Tramo existente
 * Todos los campos son opcionales
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TramoUpdateDTO {
    private String estado;
    private String observaciones;
}
