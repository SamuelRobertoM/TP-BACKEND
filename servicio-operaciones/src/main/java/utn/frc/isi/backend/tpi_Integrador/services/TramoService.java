package utn.frc.isi.backend.tpi_Integrador.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utn.frc.isi.backend.tpi_Integrador.clients.FlotaServiceClient;
import utn.frc.isi.backend.tpi_Integrador.dtos.AsignacionCamionDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.TramoDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.flota.CamionDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.flota.TarifaDTO;
import utn.frc.isi.backend.tpi_Integrador.mappers.TramoMapper;
import utn.frc.isi.backend.tpi_Integrador.models.CamionReference;
import utn.frc.isi.backend.tpi_Integrador.models.Contenedor;
import utn.frc.isi.backend.tpi_Integrador.models.Solicitud;
import utn.frc.isi.backend.tpi_Integrador.models.Tramo;
import utn.frc.isi.backend.tpi_Integrador.repositories.CamionReferenceRepository;
import utn.frc.isi.backend.tpi_Integrador.repositories.ContenedorRepository;
import utn.frc.isi.backend.tpi_Integrador.repositories.SolicitudRepository;
import utn.frc.isi.backend.tpi_Integrador.repositories.TramoRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service // Marca esta clase como un componente de servicio de Spring
public class TramoService {

    private static final Logger logger = LoggerFactory.getLogger(TramoService.class);

    private final TramoRepository tramoRepository;
    private final CamionReferenceRepository camionReferenceRepository;
    private final SolicitudRepository solicitudRepository;
    private final ContenedorRepository contenedorRepository;
    private final FlotaServiceClient flotaServiceClient;
    private final TramoMapper tramoMapper;

    // Inyección de dependencias a través del constructor (práctica recomendada)
    public TramoService(TramoRepository tramoRepository, 
                        CamionReferenceRepository camionReferenceRepository, 
                        SolicitudRepository solicitudRepository, 
                        ContenedorRepository contenedorRepository,
                        FlotaServiceClient flotaServiceClient,
                        TramoMapper tramoMapper) {
        this.tramoRepository = tramoRepository;
        this.camionReferenceRepository = camionReferenceRepository;
        this.solicitudRepository = solicitudRepository;
        this.contenedorRepository = contenedorRepository;
        this.flotaServiceClient = flotaServiceClient;
        this.tramoMapper = tramoMapper;
    }

    public List<TramoDTO> obtenerTodos() {
        return tramoRepository.findAll()
                .stream()
                .map(tramoMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<TramoDTO> obtenerPorId(Long id) {
        return tramoRepository.findById(id)
                .map(tramoMapper::toDTO);
    }

    /**
     * RF#7: Obtiene los tramos asignados a un transportista (identificado por camionId)
     * que no están finalizados. Permite al transportista ver sus tramos pendientes.
     * 
     * @param camionId ID del camión asignado al transportista
     * @return Lista de TramoDTO con los tramos asignados que no están finalizados
     */
    public List<TramoDTO> obtenerTramosAsignadosTransportista(Long camionId) {
        // Define los estados que NO queremos (solo los finalizados)
        List<String> estadosExcluidos = Arrays.asList("FINALIZADO");
        
        // Busca tramos asignados al camión que no estén finalizados
        List<Tramo> tramos = tramoRepository.findByCamionReference_IdAndEstadoNotIn(
            camionId, 
            estadosExcluidos
        );
        
        // Mapea las entidades a DTOs usando el mapper
        return tramos.stream()
                .map(tramoMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Tramo crearTramo(Tramo tramo) {
        // Aquí podríamos agregar lógica de negocio.
        // Por ejemplo: validar que el camión esté disponible, calcular costos automáticamente, etc.
        // Por ahora, solo lo guardamos.
        return tramoRepository.save(tramo);
    }

    public Tramo actualizarTramo(Long id, Tramo tramo) {
        // Verificar si el tramo existe
        if (tramoRepository.existsById(id)) {
            tramo.setId(id); // Asegurar que el ID sea el correcto
            return tramoRepository.save(tramo);
        }
        return null; // Retorna null si no existe
    }

    public void eliminarTramo(Long id) {
        tramoRepository.deleteById(id);
    }
    
    /**
     * Asigna un camión a un tramo (RF#6)
     * Valida disponibilidad y capacidad del camión antes de asignar.
     * Actualiza el estado del tramo a "ASIGNADO" y marca el camión como no disponible.
     * 
     * @param tramoId ID del tramo
     * @param dto DTO con el ID del camión a asignar
     * @return TramoDTO actualizado con el camión asignado
     */
    @Transactional
    public TramoDTO asignarCamion(Long tramoId, AsignacionCamionDTO dto) {
        // PASO 1: Buscar el tramo
        Tramo tramo = tramoRepository.findById(tramoId)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado con ID: " + tramoId));
        
        // Validar que el tramo no tenga ya un camión asignado
        if (tramo.getCamionReference() != null) {
            throw new RuntimeException("El tramo ya tiene un camión asignado");
        }
        
        // Validar que el tramo esté en estado PENDIENTE
        if (!"PENDIENTE".equals(tramo.getEstado())) {
            throw new RuntimeException("El tramo debe estar en estado PENDIENTE para asignar un camión");
        }
        
        // PASO 2: Obtener la referencia del camión
        // En el futuro, aquí llamaríamos al servicio-flota para obtener los datos actualizados
        // Por ahora, usamos la referencia local sincronizada
        CamionReference camionRef = camionReferenceRepository.findById(dto.getCamionId())
                .orElseThrow(() -> new RuntimeException("Referencia de Camión no encontrada con ID: " + dto.getCamionId()));
        
        // PASO 3: Validaciones de negocio
        
        // 3.1: Validar que el camión esté disponible
        if (!camionRef.isDisponible()) {
            throw new RuntimeException("El camión con dominio " + camionRef.getDominio() + " no está disponible");
        }
        
        // 3.2: Validar capacidad de peso y volumen
        // Necesitamos obtener el contenedor de la solicitud asociada a la ruta de este tramo
        if (tramo.getRuta() != null) {
            // Buscar la solicitud que tiene esta ruta
            Solicitud solicitud = solicitudRepository.findByRuta(tramo.getRuta())
                    .orElse(null);
            
            if (solicitud != null && solicitud.getContenedor() != null) {
                Contenedor contenedor = solicitud.getContenedor();
                
                // Validar peso
                if (camionRef.getCapacidadPeso() < contenedor.getPeso()) {
                    throw new RuntimeException(
                        String.format("El camión no tiene capacidad de peso suficiente. Requerido: %.2f kg, Disponible: %.2f kg",
                            contenedor.getPeso(), camionRef.getCapacidadPeso())
                    );
                }
                
                // Validar volumen
                if (camionRef.getCapacidadVolumen() < contenedor.getVolumen()) {
                    throw new RuntimeException(
                        String.format("El camión no tiene capacidad de volumen suficiente. Requerido: %.2f m³, Disponible: %.2f m³",
                            contenedor.getVolumen(), camionRef.getCapacidadVolumen())
                    );
                }
            }
        }
        
        // PASO 4: Asignar el camión y actualizar estados
        tramo.setCamionReference(camionRef);
        tramo.setEstado("ASIGNADO");
        
        // PASO 5: Marcar el camión como no disponible
        // En el futuro, aquí llamaríamos a: PATCH /api/camiones/{id}/disponibilidad en servicio-flota
        // Por ahora, actualizamos la referencia local
        camionRef.setDisponible(false);
        camionReferenceRepository.save(camionRef);
        
        // PASO 6: Guardar y retornar el tramo actualizado como DTO
        Tramo tramoGuardado = tramoRepository.save(tramo);
        return tramoMapper.toDTO(tramoGuardado);
    }
    
    /**
     * Inicia un tramo de transporte (RF#8)
     * El transportista marca el inicio del viaje.
     * Actualiza el estado del tramo y del contenedor.
     * 
     * @param tramoId ID del tramo a iniciar
     * @return TramoDTO actualizado
     */
    @Transactional
    public TramoDTO iniciarTramo(Long tramoId) {
        // 1. Buscar el tramo
        Tramo tramo = tramoRepository.findById(tramoId)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado con ID: " + tramoId));
        
        // 2. Validaciones de negocio
        if (!"ASIGNADO".equals(tramo.getEstado())) {
            throw new RuntimeException("El tramo no está en estado 'ASIGNADO'. Estado actual: " + tramo.getEstado());
        }
        
        // 3. Actualizar estado del Tramo
        tramo.setEstado("INICIADO");
        tramo.setFechaRealInicio(LocalDateTime.now());
        
        // 4. Buscar solicitud asociada para actualizar contenedor
        Solicitud solicitud = solicitudRepository.findByRuta(tramo.getRuta())
                .orElseThrow(() -> new RuntimeException("No se encontró solicitud asociada al tramo"));
        
        // 5. Actualizar estado del Contenedor
        Contenedor contenedor = solicitud.getContenedor();
        contenedor.setEstado("EN_VIAJE");
        contenedorRepository.save(contenedor);
        
        // 6. Actualizar estado de la Solicitud si no está ya en tránsito
        if (!"EN_TRANSITO".equals(solicitud.getEstado())) {
            solicitud.setEstado("EN_TRANSITO");
            solicitudRepository.save(solicitud);
        }
        
        // 7. Guardar y retornar el tramo actualizado como DTO
        Tramo tramoGuardado = tramoRepository.save(tramo);
        return tramoMapper.toDTO(tramoGuardado);
    }
    
    /**
     * Finaliza un tramo de transporte (RF#8)
     * El transportista marca el fin del viaje.
     * Calcula el costo real del tramo usando datos de servicio-flota:
     * - Tarifa actual (cargo gestión, precio combustible, costo por km)
     * - Datos del camión (consumo combustible, costo por km)
     * Actualiza el estado del tramo y, si es el último, del contenedor y solicitud.
     * 
     * @param tramoId ID del tramo a finalizar
     * @return TramoDTO actualizado con costo real calculado
     */
    @Transactional
    public TramoDTO finalizarTramo(Long tramoId) {
        // 1. Buscar el tramo
        Tramo tramo = tramoRepository.findById(tramoId)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado con ID: " + tramoId));
        
        // 2. Validaciones de negocio
        if (!"INICIADO".equals(tramo.getEstado())) {
            throw new RuntimeException("El tramo no está en estado 'INICIADO'. Estado actual: " + tramo.getEstado());
        }
        
        // 3. CALCULAR COSTO REAL (RF#8)
        logger.info("Calculando costo real para tramo ID: {}", tramoId);
        
        // 3.1 Obtener tarifa actual desde servicio-flota
        TarifaDTO tarifa = flotaServiceClient.obtenerTarifaActiva()
                .orElseThrow(() -> new RuntimeException("No se pudo obtener la tarifa activa desde servicio-flota"));
        logger.debug("Tarifa obtenida: cargoGestion={}, precioCombustible={}", 
                    tarifa.getCargoGestionPorTramo(), tarifa.getPrecioLitroCombustible());
        
        // 3.2 Obtener datos del camión desde servicio-flota
        if (tramo.getCamionReference() == null) {
            throw new RuntimeException("El tramo no tiene un camión asignado");
        }
        
        Long camionId = tramo.getCamionReference().getId();
        CamionDTO camion = flotaServiceClient.obtenerCamionPorId(camionId)
                .orElseThrow(() -> new RuntimeException("No se pudo obtener los datos del camión ID: " + camionId));
        logger.debug("Camión obtenido: consumo={} L/km, costoPorKm={}", 
                    camion.getConsumoCombustiblePorKm(), camion.getCostoPorKm());
        
        // 3.3 Calcular componentes del costo según el enunciado:
        // Cargo de Gestión por Tramo
        double cargoGestion = tarifa.getCargoGestionPorTramo();
        
        // Costo por Kilometraje
        double costoKilometraje = camion.getCostoPorKm() * tramo.getDistanciaKm();
        
        // Costo de Combustible
        double costoCombustible = camion.getConsumoCombustiblePorKm() 
                                  * tramo.getDistanciaKm() 
                                  * tarifa.getPrecioLitroCombustible();
        
        // Costo de Estadía (si el tramo termina en depósito)
        double costoEstadia = calcularCostoEstadia(tramo, tarifa);
        
        // Costo Total
        double costoTotalTramo = cargoGestion + costoKilometraje + costoCombustible + costoEstadia;
        
        logger.info("Costo real calculado para tramo {}: cargoGestion={}, costoKm={}, combustible={}, estadia={}, TOTAL={}", 
                   tramoId, cargoGestion, costoKilometraje, costoCombustible, costoEstadia, costoTotalTramo);
        
        // 3.4 Asignar costo real al tramo
        tramo.setCostoReal(costoTotalTramo);
        
        // 4. Actualizar estado del Tramo
        tramo.setEstado("FINALIZADO");
        tramo.setFechaRealFin(LocalDateTime.now());
        
        // 5. Buscar solicitud asociada
        Solicitud solicitud = solicitudRepository.findByRuta(tramo.getRuta())
                .orElseThrow(() -> new RuntimeException("No se encontró solicitud asociada al tramo"));
        
        // 6. Verificar si es el último tramo de la ruta
        List<Tramo> tramosRuta = tramoRepository.findByRutaOrderByOrdenAsc(tramo.getRuta());
        boolean todosFinalizados = tramosRuta.stream()
                .allMatch(t -> "FINALIZADO".equals(t.getEstado()) || t.getId().equals(tramoId));
        
        // 7. Si todos los tramos están finalizados, actualizar contenedor y solicitud
        if (todosFinalizados) {
            Contenedor contenedor = solicitud.getContenedor();
            contenedor.setEstado("ENTREGADO");
            contenedorRepository.save(contenedor);
            
            solicitud.setEstado("ENTREGADA");
            solicitudRepository.save(solicitud);
            
            logger.info("Ruta completada. Solicitud {} marcada como ENTREGADA", solicitud.getId());
        } else {
            // Si no es el último, marcar contenedor como en depósito intermedio
            Contenedor contenedor = solicitud.getContenedor();
            if (tramo.getTipo().contains("DEPOSITO")) {
                contenedor.setEstado("EN_DEPOSITO");
                contenedorRepository.save(contenedor);
            }
        }
        
        // 8. Guardar el tramo actualizado
        Tramo tramoGuardado = tramoRepository.save(tramo);
        
        // 9. Liberar el camión en servicio-flota (marca como disponible)
        try {
            flotaServiceClient.actualizarDisponibilidadCamion(camionId, true);
            logger.info("Camión ID {} marcado como disponible en servicio-flota", camionId);
        } catch (Exception e) {
            logger.warn("No se pudo actualizar disponibilidad del camión en servicio-flota: {}", e.getMessage());
            // Fallback: actualizar la referencia local
            CamionReference camionRef = tramo.getCamionReference();
            camionRef.setDisponible(true);
            camionReferenceRepository.save(camionRef);
            logger.debug("Camión {} liberado localmente como fallback", camionRef.getDominio());
        }
        
        // 10. Retornar el tramo actualizado como DTO
        return tramoMapper.toDTO(tramoGuardado);
    }
    
    /**
     * Calcula el costo de estadía si el tramo finaliza en un depósito
     * 
     * @param tramo El tramo que está finalizando
     * @param tarifa La tarifa activa con el costo de estadía diaria
     * @return Costo de estadía calculado (0 si no termina en depósito)
     */
    private double calcularCostoEstadia(Tramo tramo, TarifaDTO tarifa) {
        // Verificar si el tramo termina en un depósito
        if (tramo.getDepositoDestino() == null) {
            logger.debug("Tramo {} no termina en depósito, costo estadía = 0", tramo.getId());
            return 0.0;
        }
        
        // Calcular días de estadía
        LocalDateTime fechaInicio = tramo.getFechaRealInicio();
        LocalDateTime fechaFin = LocalDateTime.now(); // Momento de finalización
        
        if (fechaInicio == null) {
            logger.warn("Tramo {} no tiene fecha de inicio, asumiendo 0 días de estadía", tramo.getId());
            return 0.0;
        }
        
        // Calcular diferencia en días (redondeado hacia arriba)
        long horasEstadia = java.time.Duration.between(fechaInicio, fechaFin).toHours();
        int diasEstadia = (int) Math.ceil(horasEstadia / 24.0);
        
        // Si la estadía es menor a 1 día, cobrar 1 día mínimo
        if (diasEstadia < 1 && horasEstadia > 0) {
            diasEstadia = 1;
        }
        
        // Obtener costo de estadía diaria desde la tarifa
        double costoEstadiaDiaria = tarifa.getCostoEstadiaDiaria();
        double costoTotal = diasEstadia * costoEstadiaDiaria;
        
        logger.debug("Costo estadía para tramo {}: {} días × {} = {}", 
                    tramo.getId(), diasEstadia, costoEstadiaDiaria, costoTotal);
        
        return costoTotal;
    }
    
    // Aquí se podrían agregar más métodos de negocio en el futuro,
    // como actualizarEstado(Long id, String nuevoEstado), calcularTiempoViaje(Tramo tramo), etc.
}