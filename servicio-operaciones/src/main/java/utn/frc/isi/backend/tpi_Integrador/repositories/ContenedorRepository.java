package utn.frc.isi.backend.tpi_Integrador.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import utn.frc.isi.backend.tpi_Integrador.models.Contenedor;

import java.util.List;

@Repository // Buena práctica para indicar que es un componente de persistencia
public interface ContenedorRepository extends JpaRepository<Contenedor, Long> {
    // Con solo esta línea, Spring Data JPA nos dará métodos como:
    // - save()
    // - findById()
    // - findAll()
    // - deleteById()
    // ¡Y muchos más, sin necesidad de implementarlos!
    
    /**
     * RF#5: Buscar contenedores pendientes (no entregados)
     * Retorna todos los contenedores que NO están en estado ENTREGADO
     */
    @Query("SELECT c FROM Contenedor c WHERE c.estado IN ('EN_ORIGEN', 'EN_DEPOSITO')")
    List<Contenedor> findContenedoresPendientes();
}