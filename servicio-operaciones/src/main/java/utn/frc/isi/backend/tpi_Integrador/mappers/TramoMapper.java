package utn.frc.isi.backend.tpi_Integrador.mappers;

import org.springframework.stereotype.Component;
import utn.frc.isi.backend.tpi_Integrador.dtos.*;
import utn.frc.isi.backend.tpi_Integrador.models.*;

/**
 * Mapper para convertir entre entidades Tramo y sus DTOs
 */
@Component
public class TramoMapper {
    
    /**
     * Convierte una entidad Tramo a TramoDTO
     */
    public TramoDTO toDTO(Tramo entity) {
        if (entity == null) {
            return null;
        }
        
        TramoDTO dto = new TramoDTO();
        dto.setId(entity.getId());
        dto.setRutaId(entity.getRuta() != null ? entity.getRuta().getId() : null);
        dto.setOrden(entity.getOrden());
        dto.setTipo(entity.getTipo());
        dto.setEstado(entity.getEstado());
        dto.setPuntoInicio(entity.getPuntoInicio());
        dto.setLatitudInicio(entity.getLatitudInicio());
        dto.setLongitudInicio(entity.getLongitudInicio());
        dto.setPuntoFin(entity.getPuntoFin());
        dto.setLatitudFin(entity.getLatitudFin());
        dto.setLongitudFin(entity.getLongitudFin());
        dto.setDistanciaKm(entity.getDistanciaKm());
        dto.setTiempoEstimadoHoras(entity.getTiempoEstimadoHoras());
        dto.setFechaEstimadaInicio(entity.getFechaEstimadaInicio());
        dto.setFechaEstimadaFin(entity.getFechaEstimadaFin());
        dto.setFechaRealInicio(entity.getFechaRealInicio());
        dto.setFechaRealFin(entity.getFechaRealFin());
        dto.setCostoReal(entity.getCostoReal());
        
        // Mapear CamionReference a CamionReferenceDTO
        if (entity.getCamionReference() != null) {
            dto.setCamion(toCamionReferenceDTO(entity.getCamionReference()));
        }
        
        // Mapear DepositoReference origen
        if (entity.getDepositoOrigen() != null) {
            dto.setDepositoOrigen(toDepositoReferenceDTO(entity.getDepositoOrigen()));
        }
        
        // Mapear DepositoReference destino
        if (entity.getDepositoDestino() != null) {
            dto.setDepositoDestino(toDepositoReferenceDTO(entity.getDepositoDestino()));
        }
        
        return dto;
    }
    
    /**
     * Convierte un CamionReference a CamionReferenceDTO
     */
    private CamionReferenceDTO toCamionReferenceDTO(CamionReference entity) {
        if (entity == null) {
            return null;
        }
        
        CamionReferenceDTO dto = new CamionReferenceDTO();
        dto.setId(entity.getId());
        dto.setDominio(entity.getDominio());
        dto.setNombreTransportista(null); // No disponible en CamionReference
        dto.setDisponible(entity.isDisponible());
        dto.setCapacidadPeso(entity.getCapacidadPeso());
        dto.setCapacidadVolumen(entity.getCapacidadVolumen());
        return dto;
    }
    
    /**
     * Convierte un DepositoReference a DepositoReferenceDTO
     */
    private DepositoReferenceDTO toDepositoReferenceDTO(DepositoReference entity) {
        if (entity == null) {
            return null;
        }
        
        DepositoReferenceDTO dto = new DepositoReferenceDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setDireccion(null); // No disponible en DepositoReference
        dto.setLatitud(null); // No disponible en DepositoReference
        dto.setLongitud(null); // No disponible en DepositoReference
        return dto;
    }
    
    /**
     * Actualiza una entidad Tramo existente con los datos de TramoUpdateDTO
     * Solo actualiza los campos que no sean null en el DTO
     */
    public void updateEntity(TramoUpdateDTO dto, Tramo entity) {
        if (dto.getEstado() != null) {
            entity.setEstado(dto.getEstado());
        }
        // TramoUpdateDTO solo tiene estado y observaciones
        // El campo observaciones no existe en Tramo, por lo que lo ignoramos
    }
}
