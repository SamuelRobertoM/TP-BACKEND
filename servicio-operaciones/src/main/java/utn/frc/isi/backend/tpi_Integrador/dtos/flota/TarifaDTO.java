package utn.frc.isi.backend.tpi_Integrador.dtos.flota;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para recibir información de Tarifa desde servicio-flota
 * Refleja la estructura de TarifaDTO en servicio-flota
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TarifaDTO {

    private Long id;
    private double costoKmBase;
    private double precioLitroCombustible;
    private double cargoGestionPorTramo;
    private double costoEstadiaDiaria; // Costo por día de estadía en depósito
    private LocalDateTime vigenciaDesde;
    private LocalDateTime vigenciaHasta;
    private boolean activa;
}
