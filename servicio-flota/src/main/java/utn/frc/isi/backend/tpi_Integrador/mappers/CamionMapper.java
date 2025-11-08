package utn.frc.isi.backend.tpi_Integrador.mappers;

import org.springframework.stereotype.Component;
import utn.frc.isi.backend.tpi_Integrador.dtos.CamionCreateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.CamionDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.CamionUpdateDTO;
import utn.frc.isi.backend.tpi_Integrador.models.Camion;

/**
 * Mapper para convertir entre entidades Camion y sus DTOs
 */
@Component
public class CamionMapper {
    
    /**
     * Convierte un CamionCreateDTO a una entidad Camion
     */
    public Camion toEntity(CamionCreateDTO dto) {
        Camion camion = new Camion();
        camion.setDominio(dto.getDominio());
        camion.setNombreTransportista(dto.getNombreTransportista());
        camion.setTelefono(dto.getTelefono());
        camion.setCapacidadPeso(dto.getCapacidadPeso());
        camion.setCapacidadVolumen(dto.getCapacidadVolumen());
        camion.setConsumoCombustiblePorKm(dto.getConsumoCombustiblePorKm());
        camion.setDisponible(dto.getDisponible() != null ? dto.getDisponible() : true);
        camion.setCostoPorKm(dto.getCostoPorKm());
        return camion;
    }
    
    /**
     * Convierte una entidad Camion a CamionDTO
     */
    public CamionDTO toDTO(Camion entity) {
        CamionDTO dto = new CamionDTO();
        dto.setId(entity.getId());
        dto.setDominio(entity.getDominio());
        dto.setNombreTransportista(entity.getNombreTransportista());
        dto.setTelefono(entity.getTelefono());
        dto.setCapacidadPeso(entity.getCapacidadPeso());
        dto.setCapacidadVolumen(entity.getCapacidadVolumen());
        dto.setConsumoCombustiblePorKm(entity.getConsumoCombustiblePorKm());
        dto.setDisponible(entity.isDisponible());
        dto.setCostoPorKm(entity.getCostoPorKm());
        return dto;
    }
    
    /**
     * Actualiza una entidad Camion existente con los datos de CamionUpdateDTO
     * Solo actualiza los campos que no sean null en el DTO
     */
    public void updateEntity(CamionUpdateDTO dto, Camion entity) {
        if (dto.getNombreTransportista() != null) {
            entity.setNombreTransportista(dto.getNombreTransportista());
        }
        if (dto.getTelefono() != null) {
            entity.setTelefono(dto.getTelefono());
        }
        if (dto.getCapacidadPeso() != null) {
            entity.setCapacidadPeso(dto.getCapacidadPeso());
        }
        if (dto.getCapacidadVolumen() != null) {
            entity.setCapacidadVolumen(dto.getCapacidadVolumen());
        }
        if (dto.getConsumoCombustiblePorKm() != null) {
            entity.setConsumoCombustiblePorKm(dto.getConsumoCombustiblePorKm());
        }
        if (dto.getDisponible() != null) {
            entity.setDisponible(dto.getDisponible());
        }
        if (dto.getCostoPorKm() != null) {
            entity.setCostoPorKm(dto.getCostoPorKm());
        }
    }
}
