package utn.frc.isi.backend.tpi_Integrador.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import utn.frc.isi.backend.tpi_Integrador.dtos.ContenedorCreateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.ContenedorDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.ContenedorEstadoDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.ContenedorPendienteDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.ContenedorUpdateDTO;
import utn.frc.isi.backend.tpi_Integrador.mappers.ContenedorMapper;
import utn.frc.isi.backend.tpi_Integrador.models.Contenedor;
import utn.frc.isi.backend.tpi_Integrador.repositories.ContenedorRepository;
import utn.frc.isi.backend.tpi_Integrador.repositories.SolicitudRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service // Marca esta clase como un componente de servicio de Spring
public class ContenedorService {

    private static final Logger logger = LoggerFactory.getLogger(ContenedorService.class);

    private final ContenedorRepository contenedorRepository;
    private final SolicitudRepository solicitudRepository;
    private final ContenedorMapper contenedorMapper;

    // Inyección de dependencias a través del constructor (práctica recomendada)
    public ContenedorService(ContenedorRepository contenedorRepository,
                           SolicitudRepository solicitudRepository,
                           ContenedorMapper contenedorMapper) {
        this.contenedorRepository = contenedorRepository;
        this.solicitudRepository = solicitudRepository;
        this.contenedorMapper = contenedorMapper;
    }

    public List<ContenedorDTO> obtenerTodos() {
        logger.info("Obteniendo todos los contenedores");
        List<ContenedorDTO> contenedores = contenedorRepository.findAll()
                .stream()
                .map(contenedorMapper::toDTO)
                .collect(Collectors.toList());
        logger.info("Se encontraron {} contenedores", contenedores.size());
        return contenedores;
    }

    public Optional<ContenedorDTO> obtenerPorId(Long id) {
        logger.info("Buscando Contenedor con ID: {}", id);
        Optional<ContenedorDTO> contenedorOpt = contenedorRepository.findById(id)
                .map(contenedorMapper::toDTO);
        if (contenedorOpt.isEmpty()) {
            logger.warn("Contenedor con ID: {} no encontrado", id);
        }
        return contenedorOpt;
    }

    public ContenedorDTO crearContenedor(ContenedorCreateDTO dto) {
        logger.info("Creando nuevo contenedor con numero: {}", dto.getNumero());
        // Aquí podríamos agregar lógica de negocio.
        // Por ejemplo: validar que peso y volumen sean positivos, establecer estado inicial, etc.
        Contenedor contenedor = contenedorMapper.toEntity(dto);
        Contenedor contenedorGuardado = contenedorRepository.save(contenedor);
        logger.info("Contenedor creado exitosamente con ID: {}", contenedorGuardado.getId());
        return contenedorMapper.toDTO(contenedorGuardado);
    }

    public ContenedorDTO actualizarContenedor(Long id, ContenedorUpdateDTO dto) {
        logger.info("Actualizando contenedor con ID: {}", id);
        // Buscar el contenedor existente
        Optional<Contenedor> contenedorOpt = contenedorRepository.findById(id);
        
        if (contenedorOpt.isEmpty()) {
            logger.warn("Contenedor con ID: {} no encontrado para actualizar", id);
            return null; // Retorna null si no existe
        }
        
        Contenedor contenedor = contenedorOpt.get();
        contenedorMapper.updateEntity(dto, contenedor);
        Contenedor contenedorActualizado = contenedorRepository.save(contenedor);
        logger.info("Contenedor con ID: {} actualizado exitosamente", id);
        return contenedorMapper.toDTO(contenedorActualizado);
    }

    public void eliminarContenedor(Long id) {
        logger.info("Eliminando contenedor con ID: {}", id);
        contenedorRepository.deleteById(id);
        logger.info("Contenedor con ID: {} eliminado exitosamente", id);
    }
    
    /**
     * RF#2: Consultar estado del contenedor para seguimiento
     * Retorna información detallada del estado y ubicación actual del contenedor
     * 
     * @param id ID del contenedor
     * @return Optional con ContenedorEstadoDTO si el contenedor existe
     */
    public Optional<ContenedorEstadoDTO> consultarEstado(Long id) {
        logger.info("Consultando estado del contenedor ID: {}", id);
        Optional<ContenedorEstadoDTO> resultado = contenedorRepository.findById(id).map(contenedor -> {
            ContenedorEstadoDTO dto = new ContenedorEstadoDTO();
            dto.setId(contenedor.getId());
            dto.setNumero(contenedor.getNumero());
            dto.setEstado(contenedor.getEstado());
            
            // Obtener información del cliente
            if (contenedor.getCliente() != null) {
                dto.setNombreCliente(contenedor.getCliente().getNombre());
            }
            
            // Obtener ID de la solicitud asociada
            solicitudRepository.findByContenedor(contenedor).ifPresent(solicitud -> {
                dto.setSolicitudId(solicitud.getId());
            });
            
            // Determinar ubicación actual según el estado
            String ubicacion = determinarUbicacionPorEstado(contenedor.getEstado());
            dto.setUbicacionActual(ubicacion);
            
            logger.debug("Estado del contenedor ID: {} - Estado: {}, Ubicacion: {}", id, contenedor.getEstado(), ubicacion);
            return dto;
        });
        
        if (resultado.isEmpty()) {
            logger.warn("Contenedor con ID: {} no encontrado al consultar estado", id);
        }
        
        return resultado;
    }
    
    /**
     * Método auxiliar para determinar la descripción de ubicación según el estado
     */
    private String determinarUbicacionPorEstado(String estado) {
        if (estado == null) {
            return "Estado desconocido";
        }
        
        switch (estado.toUpperCase()) {
            case "EN_ORIGEN":
                return "El contenedor se encuentra en la dirección de origen, listo para ser recogido.";
            case "EN_DEPOSITO":
                return "El contenedor está almacenado en un depósito intermedio de la ruta.";
            case "EN_VIAJE":
                return "El contenedor está en tránsito hacia el siguiente punto de la ruta.";
            case "ENTREGADO":
                return "El contenedor ha sido entregado exitosamente en la dirección de destino.";
            default:
                return "Estado: " + estado;
        }
    }
    
    /**
     * RF#5: Consultar contenedores pendientes de asignación
     * Retorna la lista de contenedores que aún no han sido entregados
     * 
     * @return Lista de ContenedorPendienteDTO con información básica de contenedores pendientes
     */
    public List<ContenedorPendienteDTO> consultarPendientes() {
        logger.info("Consultando contenedores pendientes");
        List<ContenedorPendienteDTO> pendientes = contenedorRepository.findContenedoresPendientes()
                .stream()
                .map(this::mapToPendienteDTO)
                .collect(Collectors.toList());
        logger.info("Se encontraron {} contenedores pendientes", pendientes.size());
        return pendientes;
    }
    
    /**
     * Método helper para mapear de Contenedor a ContenedorPendienteDTO
     */
    private ContenedorPendienteDTO mapToPendienteDTO(Contenedor contenedor) {
        ContenedorPendienteDTO dto = new ContenedorPendienteDTO();
        dto.setId(contenedor.getId());
        dto.setNumero(contenedor.getNumero());
        dto.setEstado(contenedor.getEstado());
        
        // Obtener nombre del cliente
        if (contenedor.getCliente() != null) {
            dto.setCliente(contenedor.getCliente().getNombre());
        }
        
        // Determinar ubicación según estado
        dto.setUbicacionActual(determinarUbicacionPorEstado(contenedor.getEstado()));
        
        // Buscar solicitud asociada
        solicitudRepository.findByContenedor(contenedor).ifPresent(solicitud -> {
            dto.setSolicitudId(solicitud.getId());
        });
        
        return dto;
    }
    
    // Aquí se podrían agregar más métodos de negocio en el futuro,
    // como actualizarEstado(Long id, String nuevoEstado), buscarContenedoresPorCliente(Long clienteId), etc.
}