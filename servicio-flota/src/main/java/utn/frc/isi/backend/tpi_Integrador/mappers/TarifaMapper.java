package utn.frc.isi.backend.tpi_Integrador.mappers;

import org.springframework.stereotype.Component;
import utn.frc.isi.backend.tpi_Integrador.dtos.TarifaCreateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.TarifaUpdateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.TarifaDTO;
import utn.frc.isi.backend.tpi_Integrador.models.Tarifa;

import java.time.LocalDateTime;

/**
 * Mapper para convertir entre entidades Tarifa y DTOs
 */
@Component
public class TarifaMapper {

    /**
     * Convierte TarifaCreateDTO a entidad Tarifa
     */
    public Tarifa toEntity(TarifaCreateDTO createDTO) {
        Tarifa tarifa = new Tarifa();
        tarifa.setCostoKmBase(createDTO.getCostoKmBase());
        tarifa.setPrecioLitroCombustible(createDTO.getPrecioLitroCombustible());
        tarifa.setCargoGestionPorTramo(createDTO.getCargoGestionPorTramo());
        tarifa.setCostoEstadiaDiaria(createDTO.getCostoEstadiaDiaria());
        tarifa.setVigenciaDesde(createDTO.getVigenciaDesde() != null ? 
                               createDTO.getVigenciaDesde() : LocalDateTime.now());
        tarifa.setActiva(true); // Por defecto nueva tarifa es activa
        return tarifa;
    }

    /**
     * Convierte entidad Tarifa a TarifaDTO
     */
    public TarifaDTO toDTO(Tarifa tarifa) {
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

    /**
     * Actualiza una entidad Tarifa existente con datos de TarifaUpdateDTO
     * Solo actualiza campos no nulos del DTO
     */
    public void updateEntityFromDTO(Tarifa tarifa, TarifaUpdateDTO updateDTO) {
        if (updateDTO.getPrecioLitroCombustible() != null && updateDTO.getPrecioLitroCombustible() > 0) {
            tarifa.setPrecioLitroCombustible(updateDTO.getPrecioLitroCombustible());
        }
        
        if (updateDTO.getCargoGestionPorTramo() != null && updateDTO.getCargoGestionPorTramo() > 0) {
            tarifa.setCargoGestionPorTramo(updateDTO.getCargoGestionPorTramo());
        }
        
        if (updateDTO.getCostoEstadiaDiaria() != null && updateDTO.getCostoEstadiaDiaria() > 0) {
            tarifa.setCostoEstadiaDiaria(updateDTO.getCostoEstadiaDiaria());
        }
        
        if (updateDTO.getVigenciaHasta() != null) {
            tarifa.setVigenciaHasta(updateDTO.getVigenciaHasta());
        }
        
        if (updateDTO.getActiva() != null) {
            tarifa.setActiva(updateDTO.getActiva());
        }
    }

    /**
     * Crea una entidad Tarifa temporal para operaciones de actualización
     * Usado por el servicio para realizar validaciones
     */
    public Tarifa createTempEntityFromUpdateDTO(TarifaUpdateDTO updateDTO) {
        Tarifa tempTarifa = new Tarifa();
        tempTarifa.setPrecioLitroCombustible(updateDTO.getPrecioLitroCombustible() != null ? 
                                           updateDTO.getPrecioLitroCombustible() : 0);
        tempTarifa.setCargoGestionPorTramo(updateDTO.getCargoGestionPorTramo() != null ? 
                                         updateDTO.getCargoGestionPorTramo() : 0);
        tempTarifa.setVigenciaHasta(updateDTO.getVigenciaHasta());
        tempTarifa.setActiva(updateDTO.getActiva() != null ? updateDTO.getActiva() : false);
        return tempTarifa;
    }
}