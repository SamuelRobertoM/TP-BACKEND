package utn.frc.isi.backend.tpi_Integrador.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utn.frc.isi.backend.tpi_Integrador.models.Contenedor;
import utn.frc.isi.backend.tpi_Integrador.models.Ruta;
import utn.frc.isi.backend.tpi_Integrador.models.Solicitud;

import java.util.Optional;

@Repository // Buena práctica para indicar que es un componente de persistencia
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    // Con solo esta línea, Spring Data JPA nos dará métodos como:
    // - save()
    // - findById()
    // - findAll()
    // - deleteById()
    // ¡Y muchos más, sin necesidad de implementarlos!
    
    /**
     * Buscar solicitud por contenedor
     * Usado en el seguimiento para obtener la solicitud asociada a un contenedor
     */
    Optional<Solicitud> findByContenedor(Contenedor contenedor);
    
    /**
     * Buscar solicitud por ruta
     * Usado en la asignación de camiones para validar capacidad
     */
    Optional<Solicitud> findByRuta(Ruta ruta);
}