package utn.frc.isi.backend.tpi_Integrador.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import utn.frc.isi.backend.tpi_Integrador.models.Tarifa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TarifaRepository extends JpaRepository<Tarifa, Long> {
    
    /**
     * Buscar la única tarifa que está marcada como activa
     * @param activa true para buscar la tarifa activa
     * @return Optional con la tarifa activa si existe
     */
    Optional<Tarifa> findByActiva(boolean activa);
    
    /**
     * Buscar tarifas vigentes en una fecha específica
     * @param fecha la fecha para verificar vigencia
     * @return Lista de tarifas vigentes en esa fecha
     */
    @Query("SELECT t FROM Tarifa t WHERE t.vigenciaDesde <= :fecha AND " +
           "(t.vigenciaHasta IS NULL OR t.vigenciaHasta >= :fecha)")
    List<Tarifa> findTarifasVigentesEnFecha(LocalDateTime fecha);
    
    /**
     * Buscar todas las tarifas ordenadas por fecha de vigencia descendente
     * @return Lista de tarifas ordenadas por vigencia más reciente
     */
    List<Tarifa> findAllByOrderByVigenciaDesdeDesc();
    
    /**
     * Verificar si existe una tarifa activa
     * @return true si existe al menos una tarifa activa
     */
    boolean existsByActiva(boolean activa);
}