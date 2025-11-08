package utn.frc.isi.backend.tpi_Integrador.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import utn.frc.isi.backend.tpi_Integrador.dtos.DepositoCreateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.DepositoDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.DepositoUpdateDTO;
import utn.frc.isi.backend.tpi_Integrador.mappers.DepositoMapper;
import utn.frc.isi.backend.tpi_Integrador.models.Deposito;
import utn.frc.isi.backend.tpi_Integrador.repositories.DepositoRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service // Marca esta clase como un componente de servicio de Spring
public class DepositoService {

    private static final Logger logger = LoggerFactory.getLogger(DepositoService.class);

    private final DepositoRepository depositoRepository;
    private final DepositoMapper depositoMapper;

    // Inyección de dependencias a través del constructor (práctica recomendada)
    public DepositoService(DepositoRepository depositoRepository, DepositoMapper depositoMapper) {
        this.depositoRepository = depositoRepository;
        this.depositoMapper = depositoMapper;
    }

    public List<DepositoDTO> obtenerTodos() {
        logger.info("Obteniendo todos los depositos");
        List<DepositoDTO> depositos = depositoRepository.findAll()
                .stream()
                .map(depositoMapper::toDTO)
                .collect(Collectors.toList());
        logger.info("Se encontraron {} depositos", depositos.size());
        return depositos;
    }

    public Optional<DepositoDTO> obtenerPorId(Long id) {
        logger.info("Buscando Deposito con ID: {}", id);
        Optional<DepositoDTO> depositoOpt = depositoRepository.findById(id)
                .map(depositoMapper::toDTO);
        if (depositoOpt.isEmpty()) {
            logger.warn("Deposito con ID: {} no encontrado", id);
        }
        return depositoOpt;
    }

    public DepositoDTO crearDeposito(DepositoCreateDTO dto) {
        logger.info("Creando nuevo deposito: {}", dto.getNombre());
        // Aquí podríamos agregar lógica de negocio.
        // Por ejemplo: validar coordenadas, verificar que no exista otro depósito en la misma ubicación, etc.
        Deposito deposito = depositoMapper.toEntity(dto);
        Deposito depositoGuardado = depositoRepository.save(deposito);
        logger.info("Deposito creado exitosamente con ID: {}", depositoGuardado.getId());
        return depositoMapper.toDTO(depositoGuardado);
    }

    public DepositoDTO actualizarDeposito(Long id, DepositoUpdateDTO dto) {
        logger.info("Actualizando deposito con ID: {}", id);
        // Buscar el depósito existente
        Optional<Deposito> depositoOpt = depositoRepository.findById(id);
        
        if (depositoOpt.isEmpty()) {
            logger.warn("Deposito con ID: {} no encontrado para actualizar", id);
            return null; // Retorna null si no existe
        }
        
        Deposito deposito = depositoOpt.get();
        depositoMapper.updateEntity(dto, deposito);
        Deposito depositoActualizado = depositoRepository.save(deposito);
        logger.info("Deposito con ID: {} actualizado exitosamente", id);
        return depositoMapper.toDTO(depositoActualizado);
    }

    public void eliminarDeposito(Long id) {
        logger.info("Eliminando deposito con ID: {}", id);
        depositoRepository.deleteById(id);
        logger.info("Deposito con ID: {} eliminado exitosamente", id);
    }
    
    // Aquí se podrían agregar más métodos de negocio en el futuro,
    // como buscarDepositosPorUbicacion(double latitud, double longitud, double radio), etc.
}