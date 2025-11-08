package utn.frc.isi.backend.tpi_Integrador.mappers;

import org.springframework.stereotype.Component;
import utn.frc.isi.backend.tpi_Integrador.dtos.ClienteCreateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.ClienteDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.ClienteUpdateDTO;
import utn.frc.isi.backend.tpi_Integrador.models.Cliente;

/**
 * Mapper para convertir entre entidades Cliente y sus DTOs
 */
@Component
public class ClienteMapper {
    
    /**
     * Convierte un ClienteCreateDTO a una entidad Cliente
     */
    public Cliente toEntity(ClienteCreateDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefono(dto.getTelefono());
        cliente.setDireccion(dto.getDireccion());
        cliente.setCuit(dto.getCuit());
        return cliente;
    }
    
    /**
     * Convierte una entidad Cliente a ClienteDTO
     */
    public ClienteDTO toDTO(Cliente entity) {
        if (entity == null) {
            return null;
        }
        ClienteDTO dto = new ClienteDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setEmail(entity.getEmail());
        dto.setTelefono(entity.getTelefono());
        dto.setDireccion(entity.getDireccion());
        dto.setCuit(entity.getCuit());
        return dto;
    }
    
    /**
     * Actualiza una entidad Cliente existente con los datos de ClienteUpdateDTO
     * Solo actualiza los campos que no sean null en el DTO
     */
    public void updateEntity(ClienteUpdateDTO dto, Cliente entity) {
        if (dto.getNombre() != null) {
            entity.setNombre(dto.getNombre());
        }
        if (dto.getEmail() != null) {
            entity.setEmail(dto.getEmail());
        }
        if (dto.getTelefono() != null) {
            entity.setTelefono(dto.getTelefono());
        }
        if (dto.getDireccion() != null) {
            entity.setDireccion(dto.getDireccion());
        }
        if (dto.getCuit() != null) {
            entity.setCuit(dto.getCuit());
        }
    }
}
