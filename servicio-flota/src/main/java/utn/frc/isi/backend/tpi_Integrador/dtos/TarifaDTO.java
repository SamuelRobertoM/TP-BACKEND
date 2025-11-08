package utn.frc.isi.backend.tpi_Integrador.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de salida para la entidad Tarifa
 * Según el documento de diseño de API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarifaDTO {

    private Long id;
    private double costoKmBase;
    private double precioLitroCombustible;
    private double cargoGestionPorTramo;
    private double costoEstadiaDiaria;
    private LocalDateTime vigenciaDesde;
    private LocalDateTime vigenciaHasta;
    private boolean activa;
    
    /**
     * Constructor de conveniencia para crear DTO desde entidad
     */
    public static TarifaDTO fromEntity(utn.frc.isi.backend.tpi_Integrador.models.Tarifa tarifa) {
        return new TarifaDTO(
            tarifa.getId(),
            tarifa.getCostoKmBase(),
            tarifa.getPrecioLitroCombustible(),
            tarifa.getCargoGestionPorTramo(),
            tarifa.getCostoEstadiaDiaria(),
            tarifa.getVigenciaDesde(),
            tarifa.getVigenciaHasta(),
            tarifa.isActiva()
        );
    }
}