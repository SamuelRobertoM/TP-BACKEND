package utn.frc.isi.backend.tpi_Integrador.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * DTO para crear una ruta completa con sus tramos
 * Usado en RF#4 para asignar una ruta definitiva a una solicitud
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RutaCreateDTO {
    
    @NotEmpty(message = "La ruta debe tener al menos un tramo")
    @Valid
    private List<TramoCreateDTO> tramos; // Lista de tramos que componen la ruta
}
