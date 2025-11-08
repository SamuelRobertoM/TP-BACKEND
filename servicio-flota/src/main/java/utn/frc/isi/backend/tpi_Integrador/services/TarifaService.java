package utn.frc.isi.backend.tpi_Integrador.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utn.frc.isi.backend.tpi_Integrador.dtos.TarifaCreateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.TarifaUpdateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.TarifaDTO;
import utn.frc.isi.backend.tpi_Integrador.mappers.TarifaMapper;
import utn.frc.isi.backend.tpi_Integrador.models.Tarifa;
import utn.frc.isi.backend.tpi_Integrador.repositories.TarifaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TarifaService {

    private static final Logger logger = LoggerFactory.getLogger(TarifaService.class);

    private final TarifaRepository tarifaRepository;
    private final TarifaMapper tarifaMapper;

    public TarifaService(TarifaRepository tarifaRepository, TarifaMapper tarifaMapper) {
        this.tarifaRepository = tarifaRepository;
        this.tarifaMapper = tarifaMapper;
    }

    /**
     * Obtener la tarifa activa del sistema
     * @return Optional con la tarifa activa si existe
     */
    @Transactional(readOnly = true)
    public Optional<Tarifa> obtenerTarifaActiva() {
        logger.info("Obteniendo tarifa activa del sistema");
        Optional<Tarifa> tarifaOpt = tarifaRepository.findByActiva(true);
        if (tarifaOpt.isEmpty()) {
            logger.warn("No se encontró tarifa activa en el sistema");
        }
        return tarifaOpt;
    }

    /**
     * Obtener la tarifa activa del sistema como DTO
     * @return Optional con el DTO de la tarifa activa si existe
     */
    @Transactional(readOnly = true)
    public Optional<TarifaDTO> obtenerTarifaActivaDTO() {
        logger.info("Obteniendo tarifa activa del sistema (DTO)");
        return tarifaRepository.findByActiva(true)
                .map(tarifaMapper::toDTO);
    }

    /**
     * Obtener todas las tarifas (históricas y activa) ordenadas por vigencia
     * @return Lista de todas las tarifas
     */
    @Transactional(readOnly = true)
    public List<Tarifa> obtenerTodas() {
        logger.info("Obteniendo todas las tarifas");
        List<Tarifa> tarifas = tarifaRepository.findAllByOrderByVigenciaDesdeDesc();
        logger.info("Se encontraron {} tarifas", tarifas.size());
        return tarifas;
    }

    /**
     * Obtener todas las tarifas como DTOs ordenadas por vigencia
     * @return Lista de DTOs de todas las tarifas
     */
    @Transactional(readOnly = true)
    public List<TarifaDTO> obtenerTodasDTO() {
        logger.info("Obteniendo todas las tarifas (DTO)");
        return tarifaRepository.findAllByOrderByVigenciaDesdeDesc().stream()
                .map(tarifaMapper::toDTO)
                .toList();
    }

    /**
     * Obtener tarifa por ID
     * @param id ID de la tarifa
     * @return Optional con la tarifa si existe
     */
    @Transactional(readOnly = true)
    public Optional<Tarifa> obtenerPorId(Long id) {
        logger.info("Buscando Tarifa con ID: {}", id);
        Optional<Tarifa> tarifaOpt = tarifaRepository.findById(id);
        if (tarifaOpt.isEmpty()) {
            logger.warn("Tarifa con ID: {} no encontrada", id);
        }
        return tarifaOpt;
    }

    /**
     * Obtener tarifa por ID como DTO
     * @param id ID de la tarifa
     * @return Optional con el DTO de la tarifa si existe
     */
    @Transactional(readOnly = true)
    public Optional<TarifaDTO> obtenerPorIdDTO(Long id) {
        logger.info("Buscando Tarifa con ID: {} (DTO)", id);
        return tarifaRepository.findById(id)
                .map(tarifaMapper::toDTO);
    }

    /**
     * Crear nueva tarifa
     * IMPORTANTE: Al crear una tarifa nueva, se puede optar por desactivar las demás
     * @param tarifa la tarifa a crear
     * @return la tarifa creada
     */
    public Tarifa crearTarifa(Tarifa tarifa) {
        logger.info("Creando nueva tarifa");
        // Establecer fecha de vigencia si no está presente
        if (tarifa.getVigenciaDesde() == null) {
            tarifa.setVigenciaDesde(LocalDateTime.now());
        }
        
        // Lógica de negocio: Solo puede haber una tarifa activa a la vez
        if (tarifa.isActiva()) {
            logger.info("Desactivando tarifas activas previas antes de crear nueva tarifa activa");
            desactivarTarifasActivas();
        }
        
        Tarifa tarifaGuardada = tarifaRepository.save(tarifa);
        logger.info("Tarifa creada exitosamente con ID: {}", tarifaGuardada.getId());
        return tarifaGuardada;
    }

    /**
     * Crear nueva tarifa desde DTO
     * @param createDTO los datos para crear la tarifa
     * @return DTO de la tarifa creada
     */
    public TarifaDTO crearTarifaFromDTO(TarifaCreateDTO createDTO) {
        logger.info("Creando nueva tarifa desde DTO");
        Tarifa tarifa = tarifaMapper.toEntity(createDTO);
        
        // Lógica de negocio: Solo puede haber una tarifa activa a la vez
        logger.info("Desactivando tarifas activas previas");
        desactivarTarifasActivas();
        
        Tarifa tarifaGuardada = tarifaRepository.save(tarifa);
        logger.info("Tarifa creada exitosamente con ID: {}", tarifaGuardada.getId());
        return tarifaMapper.toDTO(tarifaGuardada);
    }

    /**
     * Actualizar tarifa existente
     * @param id ID de la tarifa a actualizar
     * @param tarifaActualizada datos de la tarifa actualizada
     * @return Optional con la tarifa actualizada si existe
     */
    public Optional<Tarifa> actualizarTarifa(Long id, Tarifa tarifaActualizada) {
        logger.info("Actualizando tarifa con ID: {}", id);
        return tarifaRepository.findById(id)
                .map(tarifaExistente -> {
                    // Actualizar campos si están presentes
                    if (tarifaActualizada.getPrecioLitroCombustible() > 0) {
                        tarifaExistente.setPrecioLitroCombustible(tarifaActualizada.getPrecioLitroCombustible());
                    }
                    if (tarifaActualizada.getCargoGestionPorTramo() > 0) {
                        tarifaExistente.setCargoGestionPorTramo(tarifaActualizada.getCargoGestionPorTramo());
                    }
                    if (tarifaActualizada.getVigenciaHasta() != null) {
                        tarifaExistente.setVigenciaHasta(tarifaActualizada.getVigenciaHasta());
                    }
                    
                    // Lógica especial para activar/desactivar
                    if (tarifaActualizada.isActiva() && !tarifaExistente.isActiva()) {
                        logger.info("Activando tarifa ID: {} y desactivando otras tarifas activas", id);
                        desactivarTarifasActivas();
                        tarifaExistente.setActiva(true);
                    } else if (!tarifaActualizada.isActiva()) {
                        logger.debug("Desactivando tarifa ID: {}", id);
                        tarifaExistente.setActiva(false);
                    }
                    
                    Tarifa guardada = tarifaRepository.save(tarifaExistente);
                    logger.info("Tarifa con ID: {} actualizada exitosamente", id);
                    return guardada;
                });
    }

    /**
     * Actualizar tarifa existente desde DTO
     * @param id ID de la tarifa a actualizar
     * @param updateDTO datos de actualización
     * @return Optional con DTO de la tarifa actualizada si existe
     */
    public Optional<TarifaDTO> actualizarTarifaFromDTO(Long id, TarifaUpdateDTO updateDTO) {
        logger.info("Actualizando tarifa con ID: {} desde DTO", id);
        return tarifaRepository.findById(id)
                .map(tarifaExistente -> {
                    // Lógica especial para activar/desactivar
                    if (updateDTO.getActiva() != null && updateDTO.getActiva() && !tarifaExistente.isActiva()) {
                        logger.info("Activando tarifa ID: {} y desactivando otras tarifas activas", id);
                        desactivarTarifasActivas();
                    }
                    
                    // Actualizar usando el mapper
                    tarifaMapper.updateEntityFromDTO(tarifaExistente, updateDTO);
                    
                    Tarifa tarifaGuardada = tarifaRepository.save(tarifaExistente);
                    logger.info("Tarifa con ID: {} actualizada exitosamente", id);
                    return tarifaMapper.toDTO(tarifaGuardada);
                });
    }

    /**
     * Eliminar tarifa por ID
     * NOTA: Solo se puede eliminar si no está activa
     * @param id ID de la tarifa a eliminar
     * @return true si se eliminó, false si no existe o está activa
     */
    public boolean eliminarTarifa(Long id) {
        logger.info("Intentando eliminar tarifa con ID: {}", id);
        return tarifaRepository.findById(id)
                .map(tarifa -> {
                    if (tarifa.isActiva()) {
                        logger.error("No se puede eliminar la tarifa ID: {} porque está activa", id);
                        throw new IllegalStateException("No se puede eliminar una tarifa activa");
                    }
                    tarifaRepository.deleteById(id);
                    logger.info("Tarifa con ID: {} eliminada exitosamente", id);
                    return true;
                })
                .orElseGet(() -> {
                    logger.warn("Tarifa con ID: {} no encontrada para eliminar", id);
                    return false;
                });
    }

    /**
     * Desactivar todas las tarifas activas
     * Método de utilidad para asegurar que solo haya una tarifa activa
     */
    private void desactivarTarifasActivas() {
        logger.debug("Desactivando todas las tarifas activas");
        tarifaRepository.findByActiva(true)
                .ifPresent(tarifaActiva -> {
                    logger.info("Desactivando tarifa ID: {}", tarifaActiva.getId());
                    tarifaActiva.setActiva(false);
                    tarifaActiva.setVigenciaHasta(LocalDateTime.now());
                    tarifaRepository.save(tarifaActiva);
                });
    }

    /**
     * Verificar si existe una tarifa activa en el sistema
     * @return true si existe una tarifa activa
     */
    @Transactional(readOnly = true)
    public boolean existeTarifaActiva() {
        logger.debug("Verificando si existe tarifa activa");
        boolean existe = tarifaRepository.existsByActiva(true);
        logger.info("Existe tarifa activa: {}", existe);
        return existe;
    }
}