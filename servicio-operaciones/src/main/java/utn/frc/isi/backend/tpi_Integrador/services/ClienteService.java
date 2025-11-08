package utn.frc.isi.backend.tpi_Integrador.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import utn.frc.isi.backend.tpi_Integrador.dtos.ClienteCreateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.ClienteDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.ClienteUpdateDTO;
import utn.frc.isi.backend.tpi_Integrador.mappers.ClienteMapper;
import utn.frc.isi.backend.tpi_Integrador.models.Cliente;
import utn.frc.isi.backend.tpi_Integrador.repositories.ClienteRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service // Marca esta clase como un componente de servicio de Spring
public class ClienteService {

    private static final Logger logger = LoggerFactory.getLogger(ClienteService.class);

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    // Inyección de dependencias a través del constructor (práctica recomendada)
    public ClienteService(ClienteRepository clienteRepository, ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    public List<ClienteDTO> obtenerTodos() {
        logger.info("Obteniendo todos los clientes");
        List<ClienteDTO> clientes = clienteRepository.findAll()
                .stream()
                .map(clienteMapper::toDTO)
                .collect(Collectors.toList());
        logger.info("Se encontraron {} clientes", clientes.size());
        return clientes;
    }

    public Optional<ClienteDTO> obtenerPorId(Long id) {
        logger.info("Buscando Cliente con ID: {}", id);
        Optional<ClienteDTO> clienteOpt = clienteRepository.findById(id)
                .map(clienteMapper::toDTO);
        if (clienteOpt.isEmpty()) {
            logger.warn("Cliente con ID: {} no encontrado", id);
        }
        return clienteOpt;
    }

    public ClienteDTO crearCliente(ClienteCreateDTO dto) {
        logger.info("Creando nuevo cliente: {}", dto.getNombre());
        // Aquí podríamos agregar lógica de negocio.
        // Por ejemplo: validar formato de email, verificar que no exista otro cliente con el mismo email, etc.
        Cliente cliente = clienteMapper.toEntity(dto);
        Cliente clienteGuardado = clienteRepository.save(cliente);
        logger.info("Cliente creado exitosamente con ID: {}", clienteGuardado.getId());
        return clienteMapper.toDTO(clienteGuardado);
    }

    public ClienteDTO actualizarCliente(Long id, ClienteUpdateDTO dto) {
        logger.info("Actualizando cliente con ID: {}", id);
        // Buscar el cliente existente
        Optional<Cliente> clienteOpt = clienteRepository.findById(id);
        
        if (clienteOpt.isEmpty()) {
            logger.warn("Cliente con ID: {} no encontrado para actualizar", id);
            return null; // Retorna null si no existe
        }
        
        Cliente cliente = clienteOpt.get();
        clienteMapper.updateEntity(dto, cliente);
        Cliente clienteActualizado = clienteRepository.save(cliente);
        logger.info("Cliente con ID: {} actualizado exitosamente", id);
        return clienteMapper.toDTO(clienteActualizado);
    }

    public void eliminarCliente(Long id) {
        logger.info("Eliminando cliente con ID: {}", id);
        clienteRepository.deleteById(id);
        logger.info("Cliente con ID: {} eliminado exitosamente", id);
    }
    
    // Aquí se podrían agregar más métodos de negocio en el futuro,
    // como buscarClientePorEmail(String email), validarDatosContacto(Cliente cliente), etc.
}