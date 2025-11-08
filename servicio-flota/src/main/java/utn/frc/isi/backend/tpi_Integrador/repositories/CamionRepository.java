package utn.frc.isi.backend.tpi_Integrador.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import utn.frc.isi.backend.tpi_Integrador.models.Camion;

@Repository // Buena práctica para indicar que es un componente de persistencia
public interface CamionRepository extends JpaRepository<Camion, Long>, JpaSpecificationExecutor<Camion> {
    // Con solo esta línea, Spring Data JPA nos dará métodos como:
    // - save()
    // - findById()
    // - findAll()
    // - deleteById()
    // ¡Y muchos más, sin necesidad de implementarlos!
}