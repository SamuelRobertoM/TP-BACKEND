package utn.frc.isi.backend.tpi_Integrador.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import utn.frc.isi.backend.tpi_Integrador.dtos.SolicitudCreateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.SolicitudDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.SolicitudUpdateDTO;
import utn.frc.isi.backend.tpi_Integrador.models.Solicitud;
import utn.frc.isi.backend.tpi_Integrador.repositories.ClienteRepository;
import utn.frc.isi.backend.tpi_Integrador.repositories.ContenedorRepository;
import utn.frc.isi.backend.tpi_Integrador.repositories.RutaRepository;

/**
 * Mapper para convertir entre entidades Solicitud y DTOs
 * Nota: La creación de Solicitud se maneja en el Service (SolicitudService.crearNuevaSolicitud)
 * debido a la complejidad de la orquestación (RF#1)
 */
@Component
public class SolicitudMapper {

    @Autowired
    private ClienteMapper clienteMapper;
    
    @Autowired
    private ContenedorMapper contenedorMapper;
    
    @Autowired
    private RutaMapper rutaMapper;

    /**
     * Convierte una entidad Solicitud a SolicitudDTO
     * @param solicitud entidad a convertir
     * @return SolicitudDTO o null si la solicitud es null
     */
    public SolicitudDTO toDTO(Solicitud solicitud) {
        if (solicitud == null) {
            return null;
        }

        SolicitudDTO dto = new SolicitudDTO();
        dto.setId(solicitud.getId());
        dto.setFechaSolicitud(solicitud.getFechaSolicitud());
        dto.setEstado(solicitud.getEstado());
        dto.setObservaciones(solicitud.getObservaciones());
        dto.setCostoEstimado(solicitud.getCostoEstimado());
        dto.setTiempoEstimado(solicitud.getTiempoEstimado());
        dto.setCostoFinal(solicitud.getCostoFinal());
        dto.setTiempoReal(solicitud.getTiempoReal());
        
        // Convertir relaciones
        dto.setContenedor(contenedorMapper.toDTO(solicitud.getContenedor()));
        dto.setCliente(clienteMapper.toDTO(solicitud.getCliente()));
        dto.setRuta(rutaMapper.toDTO(solicitud.getRuta()));

        return dto;
    }

    /**
     * Actualiza una entidad Solicitud existente con datos de SolicitudUpdateDTO
     * Solo actualiza campos que no son null en el DTO (actualización parcial)
     * @param dto DTO con los campos a actualizar
     * @param solicitud entidad existente a actualizar
     */
    public void updateEntity(SolicitudUpdateDTO dto, Solicitud solicitud) {
        if (dto.getEstado() != null) {
            solicitud.setEstado(dto.getEstado());
        }
        if (dto.getObservaciones() != null) {
            solicitud.setObservaciones(dto.getObservaciones());
        }
        if (dto.getCostoFinal() != null) {
            solicitud.setCostoFinal(dto.getCostoFinal());
        }
        if (dto.getTiempoReal() != null) {
            solicitud.setTiempoReal(dto.getTiempoReal());
        }
    }
}
