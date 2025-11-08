package utn.frc.isi.backend.tpi_Integrador.dtos;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para actualizar una tarifa existente
 * Según el documento de diseño de API - todos los campos son opcionales
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarifaUpdateDTO {

    // OPCIONAL - Precio actual del litro de combustible
    @Positive(message = "El precio del litro de combustible debe ser positivo")
    private Double precioLitroCombustible;

    // OPCIONAL - Cargo fijo por tramo  
    @Positive(message = "El cargo de gestión por tramo debe ser positivo")
    private Double cargoGestionPorTramo;
    
    // OPCIONAL - Costo de estadía diaria en depósito
    @Positive(message = "El costo de estadía diaria debe ser positivo")
    private Double costoEstadiaDiaria;

    // OPCIONAL - Para cerrar vigencia de la tarifa
    private LocalDateTime vigenciaHasta;

    // OPCIONAL - Para activar/desactivar la tarifa
    private Boolean activa;
}