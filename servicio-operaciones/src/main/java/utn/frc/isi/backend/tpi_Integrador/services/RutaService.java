package utn.frc.isi.backend.tpi_Integrador.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utn.frc.isi.backend.tpi_Integrador.dtos.Coordenada;
import utn.frc.isi.backend.tpi_Integrador.dtos.RutaCreateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.RutaDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.RutaTentativaDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.RutaUpdateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.TramoCreateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.TramoTentativoDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.googlemaps.Element;
import utn.frc.isi.backend.tpi_Integrador.mappers.RutaMapper;
import utn.frc.isi.backend.tpi_Integrador.models.Ruta;
import utn.frc.isi.backend.tpi_Integrador.models.Solicitud;
import utn.frc.isi.backend.tpi_Integrador.models.Tramo;
import utn.frc.isi.backend.tpi_Integrador.repositories.RutaRepository;
import utn.frc.isi.backend.tpi_Integrador.repositories.SolicitudRepository;
import utn.frc.isi.backend.tpi_Integrador.repositories.TramoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service // Marca esta clase como un componente de servicio de Spring
public class RutaService {

    private static final Logger logger = LoggerFactory.getLogger(RutaService.class);

    private final RutaRepository rutaRepository;
    private final SolicitudRepository solicitudRepository;
    private final TramoRepository tramoRepository;
    private final GoogleMapsService googleMapsService;
    private final RutaMapper rutaMapper;

    // Inyección de dependencias a través del constructor (práctica recomendada)
    public RutaService(RutaRepository rutaRepository, 
                      SolicitudRepository solicitudRepository, 
                      TramoRepository tramoRepository,
                      GoogleMapsService googleMapsService,
                      RutaMapper rutaMapper) {
        this.rutaRepository = rutaRepository;
        this.solicitudRepository = solicitudRepository;
        this.tramoRepository = tramoRepository;
        this.googleMapsService = googleMapsService;
        this.rutaMapper = rutaMapper;
    }

    /**
     * Obtiene todas las rutas
     * @return Lista de RutaDTO
     */
    public List<RutaDTO> obtenerTodas() {
        logger.info("Obteniendo todas las rutas");
        List<RutaDTO> rutas = rutaRepository.findAll()
                .stream()
                .map(rutaMapper::toDTO)
                .collect(Collectors.toList());
        logger.info("Se encontraron {} rutas", rutas.size());
        return rutas;
    }

    /**
     * Obtiene una ruta por ID
     * @param id ID de la ruta
     * @return Optional con RutaDTO si existe
     */
    public Optional<RutaDTO> obtenerPorId(Long id) {
        logger.info("Buscando Ruta con ID: {}", id);
        Optional<RutaDTO> rutaOpt = rutaRepository.findById(id)
                .map(rutaMapper::toDTO);
        if (rutaOpt.isEmpty()) {
            logger.warn("Ruta con ID: {} no encontrada", id);
        }
        return rutaOpt;
    }

    /**
     * Actualiza una ruta existente
     * @param id ID de la ruta a actualizar
     * @param dto DTO con los campos a actualizar
     * @return RutaDTO actualizada o null si no existe
     */
    public RutaDTO actualizarRuta(Long id, RutaUpdateDTO dto) {
        logger.info("Actualizando ruta con ID: {}", id);
        Optional<Ruta> rutaOpt = rutaRepository.findById(id);
        if (rutaOpt.isPresent()) {
            Ruta ruta = rutaOpt.get();
            rutaMapper.updateEntity(dto, ruta);
            Ruta rutaActualizada = rutaRepository.save(ruta);
            logger.info("Ruta con ID: {} actualizada exitosamente", id);
            return rutaMapper.toDTO(rutaActualizada);
        }
        logger.warn("Ruta con ID: {} no encontrada para actualizar", id);
        return null;
    }

    public void eliminarRuta(Long id) {
        logger.info("Eliminando ruta con ID: {}", id);
        rutaRepository.deleteById(id);
        logger.info("Ruta con ID: {} eliminada exitosamente", id);
    }
    
    /**
     * Calcula rutas tentativas para una solicitud específica.
     * Genera propuestas de rutas con información detallada de tramos, costos y tiempos.
     * Utiliza Google Maps Distance Matrix API para obtener distancias y tiempos reales.
     * 
     * @param solicitudId ID de la solicitud
     * @return Lista de rutas tentativas con sus respectivos tramos
     */
    public List<RutaTentativaDTO> calcularRutasTentativas(Long solicitudId) {
        logger.info("Calculando rutas tentativas para solicitud ID: {}", solicitudId);
        // Buscar la solicitud
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> {
                    logger.error("Solicitud no encontrada con ID: {}", solicitudId);
                    return new RuntimeException("Solicitud no encontrada con ID: " + solicitudId);
                });
        
        // Verificar que tenga una ruta asignada
        Ruta ruta = solicitud.getRuta();
        if (ruta == null) {
            logger.error("La solicitud ID: {} no tiene una ruta asignada", solicitudId);
            throw new RuntimeException("La solicitud no tiene una ruta asignada");
        }
        
        List<RutaTentativaDTO> rutasTentativas = new ArrayList<>();
        
        // Formatear coordenadas para Google Maps API (formato: "lat,lng")
        String origenLatLng = ruta.getLatitudOrigen() + "," + ruta.getLongitudOrigen();
        String destinoLatLng = ruta.getLatitudDestino() + "," + ruta.getLongitudDestino();
        
        // Obtener información de Google Maps
        Optional<Element> elementOpt = googleMapsService.obtenerInformacionDistancia(origenLatLng, destinoLatLng);
        
        double distanciaKm;
        double tiempoHoras;
        
        if (elementOpt.isPresent()) {
            Element element = elementOpt.get();
            // Convertir distancia de metros a kilómetros
            distanciaKm = element.getDistance().getValue() / 1000.0;
            // Convertir duración de segundos a horas
            tiempoHoras = element.getDuration().getValue() / 3600.0;
        } else {
            // Si Google Maps falla, lanzar excepción
            throw new RuntimeException("No se pudo calcular la distancia usando Google Maps API. Verifique las coordenadas y la conectividad.");
        }
        
        // Estimar costo ($5 por km como ejemplo)
        double costoEstimado = distanciaKm * 5.0;
        
        // Crear objeto Coordenada para punto inicio
        Coordenada puntoInicio = new Coordenada();
        puntoInicio.setLatitud(ruta.getLatitudOrigen());
        puntoInicio.setLongitud(ruta.getLongitudOrigen());
        
        // Crear objeto Coordenada para punto fin
        Coordenada puntoFin = new Coordenada();
        puntoFin.setLatitud(ruta.getLatitudDestino());
        puntoFin.setLongitud(ruta.getLongitudDestino());
        
        // Crear el tramo único (ruta directa)
        TramoTentativoDTO tramo = new TramoTentativoDTO();
        tramo.setOrden(1);
        tramo.setTipo("ORIGEN-DESTINO");
        tramo.setPuntoInicio(puntoInicio);
        tramo.setPuntoFin(puntoFin);
        tramo.setDistanciaKm(distanciaKm);
        tramo.setTiempoEstimadoHoras(tiempoHoras);
        tramo.setCostoAproximado(costoEstimado);
        tramo.setObservaciones("Ruta directa sin paradas intermedias (calculada con Google Maps)");
        
        // Crear la ruta tentativa
        RutaTentativaDTO rutaTentativa = new RutaTentativaDTO();
        rutaTentativa.setTramos(List.of(tramo));
        rutaTentativa.setCostoEstimadoTotal(costoEstimado);
        rutaTentativa.setTiempoEstimadoTotal(tiempoHoras);
        rutaTentativa.setDistanciaTotal(distanciaKm);
        rutaTentativa.setCantidadTramos(1);
        rutaTentativa.setCantidadDepositos(0);
        rutaTentativa.setTipoRuta("DIRECTA");
        rutaTentativa.setDescripcion("Ruta directa de " + String.format("%.2f", distanciaKm) + " km sin paradas intermedias");
        
        rutasTentativas.add(rutaTentativa);
        
        return rutasTentativas;
    }
    
    /**
     * Asigna una ruta definitiva a una solicitud (RF#4)
     * Crea una nueva ruta con sus tramos y la asocia a la solicitud.
     * Cambia el estado de la solicitud a "PROGRAMADA".
     * Utiliza Google Maps Distance Matrix API para calcular distancias reales.
     * 
     * @param solicitudId ID de la solicitud
     * @param dto DTO con la información de la ruta y sus tramos
     * @return RutaDTO de la ruta creada y asignada
     */
    @Transactional
    public RutaDTO asignarRutaASolicitud(Long solicitudId, RutaCreateDTO dto) {
        // 1. Buscar la solicitud
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con ID: " + solicitudId));
        
        // 2. Calcular totales de la ruta basándose en los tramos usando Google Maps
        double distanciaTotal = 0;
        double tiempoTotal = 0;
        
        for (TramoCreateDTO tramoDto : dto.getTramos()) {
            // Formatear coordenadas para Google Maps API
            String origenLatLng = tramoDto.getLatitudInicio() + "," + tramoDto.getLongitudInicio();
            String destinoLatLng = tramoDto.getLatitudFin() + "," + tramoDto.getLongitudFin();
            
            // Obtener información de Google Maps
            Optional<Element> elementOpt = googleMapsService.obtenerInformacionDistancia(origenLatLng, destinoLatLng);
            
            if (elementOpt.isPresent()) {
                Element element = elementOpt.get();
                // Convertir distancia de metros a kilómetros
                double distanciaTramo = element.getDistance().getValue() / 1000.0;
                distanciaTotal += distanciaTramo;
                // Convertir duración de segundos a horas
                tiempoTotal += element.getDuration().getValue() / 3600.0;
            } else {
                throw new RuntimeException("No se pudo calcular la distancia del tramo usando Google Maps API");
            }
        }
        
        // 3. Crear y guardar la nueva entidad Ruta
        Ruta nuevaRuta = new Ruta();
        
        // Usar coordenadas del primer y último tramo
        TramoCreateDTO primerTramo = dto.getTramos().get(0);
        TramoCreateDTO ultimoTramo = dto.getTramos().get(dto.getTramos().size() - 1);
        
        nuevaRuta.setOrigen("Origen de la ruta");
        nuevaRuta.setDestino("Destino de la ruta");
        nuevaRuta.setLatitudOrigen(primerTramo.getLatitudInicio());
        nuevaRuta.setLongitudOrigen(primerTramo.getLongitudInicio());
        nuevaRuta.setLatitudDestino(ultimoTramo.getLatitudFin());
        nuevaRuta.setLongitudDestino(ultimoTramo.getLongitudFin());
        nuevaRuta.setDistanciaKm(distanciaTotal);
        nuevaRuta.setTiempoEstimadoHoras((int) Math.ceil(tiempoTotal));
        
        Ruta rutaGuardada = rutaRepository.save(nuevaRuta);
        
        // 4. Crear y guardar cada Tramo de la ruta
        for (TramoCreateDTO tramoDto : dto.getTramos()) {
            Tramo nuevoTramo = new Tramo();
            nuevoTramo.setRuta(rutaGuardada);
            nuevoTramo.setOrden(tramoDto.getOrden());
            nuevoTramo.setTipo(tramoDto.getTipo());
            nuevoTramo.setLatitudInicio(tramoDto.getLatitudInicio());
            nuevoTramo.setLongitudInicio(tramoDto.getLongitudInicio());
            nuevoTramo.setLatitudFin(tramoDto.getLatitudFin());
            nuevoTramo.setLongitudFin(tramoDto.getLongitudFin());
            nuevoTramo.setEstado("PENDIENTE"); // Estado inicial
            nuevoTramo.setFechaEstimadaInicio(tramoDto.getFechaEstimadaInicio());
            nuevoTramo.setFechaEstimadaFin(tramoDto.getFechaEstimadaFin());
            
            // Calcular distancia del tramo usando Google Maps
            String origenLatLng = tramoDto.getLatitudInicio() + "," + tramoDto.getLongitudInicio();
            String destinoLatLng = tramoDto.getLatitudFin() + "," + tramoDto.getLongitudFin();
            
            Optional<Element> elementOpt = googleMapsService.obtenerInformacionDistancia(origenLatLng, destinoLatLng);
            
            if (elementOpt.isPresent()) {
                Element element = elementOpt.get();
                double distanciaTramo = element.getDistance().getValue() / 1000.0;
                double tiempoTramo = element.getDuration().getValue() / 3600.0;
                
                nuevoTramo.setDistanciaKm(distanciaTramo);
                nuevoTramo.setTiempoEstimadoHoras((int) Math.ceil(tiempoTramo));
            } else {
                throw new RuntimeException("No se pudo calcular la distancia del tramo usando Google Maps API");
            }
            
            // Por ahora, no manejamos depósitos (se implementará en futuro)
            // Los campos depositoOrigen y depositoDestino quedarán null
            
            tramoRepository.save(nuevoTramo);
        }
        
        // 5. Asociar la ruta a la solicitud y actualizar estado
        solicitud.setRuta(rutaGuardada);
        solicitud.setEstado("PROGRAMADA");
        solicitudRepository.save(solicitud);
        
        // 6. Retornar la ruta como DTO
        return rutaMapper.toDTO(rutaGuardada);
    }
    
    // Aquí se podrían agregar más métodos de negocio en el futuro,
    // como buscarRutasPorSolicitud(Long solicitudId), optimizarRuta(Ruta ruta), etc.
}