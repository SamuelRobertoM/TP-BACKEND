package utn.frc.isi.backend.tpi_Integrador.services;

import org.springframework.stereotype.Service;
import utn.frc.isi.backend.tpi_Integrador.models.DepositoReference;
import utn.frc.isi.backend.tpi_Integrador.repositories.DepositoReferenceRepository;

import java.util.List;
import java.util.Optional;

@Service // Marca esta clase como un componente de servicio de Spring
public class DepositoReferenceService {

    private final DepositoReferenceRepository depositoReferenceRepository;

    // Inyección de dependencias a través del constructor (práctica recomendada)
    public DepositoReferenceService(DepositoReferenceRepository depositoReferenceRepository) {
        this.depositoReferenceRepository = depositoReferenceRepository;
    }

    public List<DepositoReference> obtenerTodos() {
        return depositoReferenceRepository.findAll();
    }

    public Optional<DepositoReference> obtenerPorId(Long id) {
        return depositoReferenceRepository.findById(id);
    }

    public DepositoReference crearDepositoReference(DepositoReference depositoReference) {
        // Aquí podríamos agregar lógica de negocio.
        // Por ejemplo: validar que el depositoId exista en el servicio-flota, sincronizar datos, etc.
        // Por ahora, solo lo guardamos.
        return depositoReferenceRepository.save(depositoReference);
    }

    public DepositoReference actualizarDepositoReference(Long id, DepositoReference depositoReference) {
        // Verificar si la referencia existe
        if (depositoReferenceRepository.existsById(id)) {
            depositoReference.setId(id); // Asegurar que el ID sea el correcto
            return depositoReferenceRepository.save(depositoReference);
        }
        return null; // Retorna null si no existe
    }

    public void eliminarDepositoReference(Long id) {
        depositoReferenceRepository.deleteById(id);
    }
    
    // Aquí se podrían agregar más métodos de negocio en el futuro,
    // como sincronizarConServicioFlota(Long depositoId), buscarPorDepositoId(Long depositoId), etc.
}