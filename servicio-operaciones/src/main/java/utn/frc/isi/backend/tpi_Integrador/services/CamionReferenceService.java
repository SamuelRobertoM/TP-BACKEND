package utn.frc.isi.backend.tpi_Integrador.services;

import org.springframework.stereotype.Service;
import utn.frc.isi.backend.tpi_Integrador.models.CamionReference;
import utn.frc.isi.backend.tpi_Integrador.repositories.CamionReferenceRepository;

import java.util.List;
import java.util.Optional;

@Service // Marca esta clase como un componente de servicio de Spring
public class CamionReferenceService {

    private final CamionReferenceRepository camionReferenceRepository;

    // Inyección de dependencias a través del constructor (práctica recomendada)
    public CamionReferenceService(CamionReferenceRepository camionReferenceRepository) {
        this.camionReferenceRepository = camionReferenceRepository;
    }

    public List<CamionReference> obtenerTodos() {
        return camionReferenceRepository.findAll();
    }

    public Optional<CamionReference> obtenerPorId(Long id) {
        return camionReferenceRepository.findById(id);
    }

    public CamionReference crearCamionReference(CamionReference camionReference) {
        // Aquí podríamos agregar lógica de negocio.
        // Por ejemplo: validar que el camionId exista en el servicio-flota, sincronizar datos, etc.
        // Por ahora, solo lo guardamos.
        return camionReferenceRepository.save(camionReference);
    }

    public CamionReference actualizarCamionReference(Long id, CamionReference camionReference) {
        // Verificar si la referencia existe
        if (camionReferenceRepository.existsById(id)) {
            camionReference.setId(id); // Asegurar que el ID sea el correcto
            return camionReferenceRepository.save(camionReference);
        }
        return null; // Retorna null si no existe
    }

    public void eliminarCamionReference(Long id) {
        camionReferenceRepository.deleteById(id);
    }
    
    // Aquí se podrían agregar más métodos de negocio en el futuro,
    // como sincronizarConServicioFlota(Long camionId), buscarPorCamionId(Long camionId), etc.
}