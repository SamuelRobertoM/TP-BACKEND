package utn.frc.isi.backend.tpi_Integrador.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utn.frc.isi.backend.tpi_Integrador.dtos.CamionCreateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.CamionDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.CamionUpdateDTO;
import utn.frc.isi.backend.tpi_Integrador.mappers.CamionMapper;
import utn.frc.isi.backend.tpi_Integrador.models.Camion;
import utn.frc.isi.backend.tpi_Integrador.repositories.CamionRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service // Marca esta clase como un componente de servicio de Spring
public class CamionService {

    private static final Logger logger = LoggerFactory.getLogger(CamionService.class);

    private final CamionRepository camionRepository;
    private final CamionMapper camionMapper;

    // Inyección de dependencias a través del constructor (práctica recomendada)
    public CamionService(CamionRepository camionRepository, CamionMapper camionMapper) {
        this.camionRepository = camionRepository;
        this.camionMapper = camionMapper;
    }

    public List<CamionDTO> obtenerTodos() {
        logger.info("Obteniendo todos los camiones");
        List<CamionDTO> camiones = camionRepository.findAll()
                .stream()
                .map(camionMapper::toDTO)
                .collect(Collectors.toList());
        logger.info("Se encontraron {} camiones", camiones.size());
        return camiones;
    }

    public Optional<CamionDTO> obtenerPorId(Long id) {
        logger.info("Buscando Camion con ID: {}", id);
        Optional<CamionDTO> camionOpt = camionRepository.findById(id)
                .map(camionMapper::toDTO);
        if (camionOpt.isEmpty()) {
            logger.warn("Camion con ID: {} no encontrado", id);
        }
        return camionOpt;
    }

    public CamionDTO crearCamion(CamionCreateDTO dto) {
        logger.info("Creando nuevo camion con dominio: {}", dto.getDominio());
        // Aquí podríamos agregar lógica de negocio.
        // Por ejemplo: antes de guardar, verificar que la patente no exista.
        Camion camion = camionMapper.toEntity(dto);
        Camion camionGuardado = camionRepository.save(camion);
        logger.info("Camion creado exitosamente con ID: {}", camionGuardado.getId());
        return camionMapper.toDTO(camionGuardado);
    }

    public CamionDTO actualizarCamion(Long id, CamionUpdateDTO dto) {
        logger.info("Actualizando camion con ID: {}", id);
        // Buscar el camión existente
        Optional<Camion> camionOpt = camionRepository.findById(id);
        
        if (camionOpt.isEmpty()) {
            logger.warn("Camion con ID: {} no encontrado para actualizar", id);
            return null; // Retorna null si no existe
        }
        
        Camion camion = camionOpt.get();
        camionMapper.updateEntity(dto, camion);
        Camion camionActualizado = camionRepository.save(camion);
        logger.info("Camion con ID: {} actualizado exitosamente", id);
        return camionMapper.toDTO(camionActualizado);
    }

    public void eliminarCamion(Long id) {
        logger.info("Eliminando camion con ID: {}", id);
        camionRepository.deleteById(id);
        logger.info("Camion con ID: {} eliminado exitosamente", id);
    }

    /**
     * Buscar camiones disponibles con filtros opcionales de capacidad
     * @param pesoMinimo filtro opcional para capacidad mínima de peso
     * @param volumenMinimo filtro opcional para capacidad mínima de volumen
     * @return lista de camiones que cumplen los criterios
     */
    public List<CamionDTO> buscarDisponibles(Double pesoMinimo, Double volumenMinimo) {
        logger.info("Buscando camiones disponibles - pesoMinimo: {}, volumenMinimo: {}", pesoMinimo, volumenMinimo);
        // Crear especificación base: camión debe estar disponible
        Specification<Camion> spec = (root, query, cb) -> 
            cb.isTrue(root.get("disponible"));

        // Agregar filtro de peso mínimo si se proporciona
        if (pesoMinimo != null) {
            spec = spec.and((root, query, cb) -> 
                cb.greaterThanOrEqualTo(root.get("capacidadPeso"), pesoMinimo)
            );
        }

        // Agregar filtro de volumen mínimo si se proporciona
        if (volumenMinimo != null) {
            spec = spec.and((root, query, cb) -> 
                cb.greaterThanOrEqualTo(root.get("capacidadVolumen"), volumenMinimo)
            );
        }

        List<CamionDTO> camiones = camionRepository.findAll(spec)
                .stream()
                .map(camionMapper::toDTO)
                .collect(Collectors.toList());
        logger.info("Se encontraron {} camiones disponibles con los criterios especificados", camiones.size());
        return camiones;
    }
    
    /**
     * Actualiza la disponibilidad de un camión
     * Usado cuando servicio-operaciones libera un camión al finalizar un tramo
     * 
     * @param id ID del camión a actualizar
     * @param disponible true para marcar como disponible, false para ocupado
     * @return Optional con CamionDTO actualizado, o vacío si no se encuentra
     */
    @Transactional
    public Optional<CamionDTO> actualizarDisponibilidad(Long id, boolean disponible) {
        logger.info("Actualizando disponibilidad del camion ID: {} a {}", id, disponible);
        Optional<Camion> camionOpt = camionRepository.findById(id);
        if (camionOpt.isPresent()) {
            Camion camion = camionOpt.get();
            camion.setDisponible(disponible);
            Camion camionGuardado = camionRepository.save(camion);
            logger.info("Disponibilidad del camion ID: {} actualizada exitosamente", id);
            return Optional.of(camionMapper.toDTO(camionGuardado));
        } else {
            logger.warn("Camion con ID: {} no encontrado para actualizar disponibilidad", id);
            return Optional.empty(); // Camión no encontrado
        }
    }
}