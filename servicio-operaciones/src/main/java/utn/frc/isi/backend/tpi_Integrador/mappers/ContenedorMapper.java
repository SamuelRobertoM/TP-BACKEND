package utn.frc.isi.backend.tpi_Integrador.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import utn.frc.isi.backend.tpi_Integrador.dtos.ContenedorCreateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.ContenedorDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.ContenedorUpdateDTO;
import utn.frc.isi.backend.tpi_Integrador.models.Cliente;
import utn.frc.isi.backend.tpi_Integrador.models.Contenedor;
import utn.frc.isi.backend.tpi_Integrador.repositories.ClienteRepository;

/**
 * Mapper para convertir entre entidades Contenedor y sus DTOs
 */
@Component
public class ContenedorMapper {
    
    @Autowired
    private ClienteMapper clienteMapper;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    /**
     * Convierte un ContenedorCreateDTO a una entidad Contenedor
     */
    public Contenedor toEntity(ContenedorCreateDTO dto) {
        Contenedor contenedor = new Contenedor();
        contenedor.setNumero(dto.getNumero());
        contenedor.setTipo(dto.getTipo());
        contenedor.setPeso(dto.getPeso());
        contenedor.setVolumen(dto.getVolumen());
        contenedor.setEstado(dto.getEstado());
        
        // Cargar el cliente si se proporciona el ID
        if (dto.getClienteId() != null) {
            Cliente cliente = clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + dto.getClienteId()));
            contenedor.setCliente(cliente);
        }
        
        return contenedor;
    }
    
    /**
     * Convierte una entidad Contenedor a ContenedorDTO
     */
    public ContenedorDTO toDTO(Contenedor entity) {
        if (entity == null) {
            return null;
        }
        ContenedorDTO dto = new ContenedorDTO();
        dto.setId(entity.getId());
        dto.setNumero(entity.getNumero());
        dto.setTipo(entity.getTipo());
        dto.setPeso(entity.getPeso());
        dto.setVolumen(entity.getVolumen());
        dto.setEstado(entity.getEstado());
        dto.setCliente(clienteMapper.toDTO(entity.getCliente()));
        return dto;
    }
    
    /**
     * Actualiza una entidad Contenedor existente con los datos de ContenedorUpdateDTO
     */
    public void updateEntity(ContenedorUpdateDTO dto, Contenedor entity) {
        if (dto.getNumero() != null) {
            entity.setNumero(dto.getNumero());
        }
        if (dto.getTipo() != null) {
            entity.setTipo(dto.getTipo());
        }
        if (dto.getPeso() != null) {
            entity.setPeso(dto.getPeso());
        }
        if (dto.getVolumen() != null) {
            entity.setVolumen(dto.getVolumen());
        }
        if (dto.getEstado() != null) {
            entity.setEstado(dto.getEstado());
        }
        if (dto.getClienteId() != null) {
            Cliente cliente = clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + dto.getClienteId()));
            entity.setCliente(cliente);
        }
    }
}
