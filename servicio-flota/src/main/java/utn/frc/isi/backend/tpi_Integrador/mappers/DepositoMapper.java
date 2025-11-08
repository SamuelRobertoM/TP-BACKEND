package utn.frc.isi.backend.tpi_Integrador.mappers;

import org.springframework.stereotype.Component;
import utn.frc.isi.backend.tpi_Integrador.dtos.DepositoCreateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.DepositoDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.DepositoUpdateDTO;
import utn.frc.isi.backend.tpi_Integrador.models.Deposito;

/**
 * Mapper para convertir entre entidades Deposito y sus DTOs
 */
@Component
public class DepositoMapper {
    
    /**
     * Convierte un DepositoCreateDTO a una entidad Deposito
     */
    public Deposito toEntity(DepositoCreateDTO dto) {
        Deposito deposito = new Deposito();
        deposito.setNombre(dto.getNombre());
        deposito.setDireccion(dto.getDireccion());
        deposito.setLatitud(dto.getLatitud());
        deposito.setLongitud(dto.getLongitud());
        return deposito;
    }
    
    /**
     * Convierte una entidad Deposito a DepositoDTO
     */
    public DepositoDTO toDTO(Deposito entity) {
        DepositoDTO dto = new DepositoDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setDireccion(entity.getDireccion());
        dto.setLatitud(entity.getLatitud());
        dto.setLongitud(entity.getLongitud());
        return dto;
    }
    
    /**
     * Actualiza una entidad Deposito existente con los datos de DepositoUpdateDTO
     * Solo actualiza los campos que no sean null en el DTO
     */
    public void updateEntity(DepositoUpdateDTO dto, Deposito entity) {
        if (dto.getNombre() != null) {
            entity.setNombre(dto.getNombre());
        }
        if (dto.getDireccion() != null) {
            entity.setDireccion(dto.getDireccion());
        }
        if (dto.getLatitud() != null) {
            entity.setLatitud(dto.getLatitud());
        }
        if (dto.getLongitud() != null) {
            entity.setLongitud(dto.getLongitud());
        }
    }
}
