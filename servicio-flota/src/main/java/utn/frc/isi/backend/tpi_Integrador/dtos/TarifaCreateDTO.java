package utn.frc.isi.backend.tpi_Integrador.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para crear una nueva tarifa
 * Según el documento de diseño de API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarifaCreateDTO {

    @NotNull(message = "El costo base por km es requerido")
    @Positive(message = "El costo base por km debe ser positivo")
    private double costoKmBase;

    @NotNull(message = "El precio del litro de combustible es requerido")
    @Positive(message = "El precio del litro de combustible debe ser positivo")
    private double precioLitroCombustible;

    @NotNull(message = "El cargo de gestión por tramo es requerido")
    @Positive(message = "El cargo de gestión por tramo debe ser positivo")
    private double cargoGestionPorTramo;
    
    @NotNull(message = "El costo de estadía diaria es requerido")
    @Positive(message = "El costo de estadía diaria debe ser positivo")
    private double costoEstadiaDiaria;

    @NotNull(message = "La fecha de vigencia desde es requerida")
    private LocalDateTime vigenciaDesde;
}