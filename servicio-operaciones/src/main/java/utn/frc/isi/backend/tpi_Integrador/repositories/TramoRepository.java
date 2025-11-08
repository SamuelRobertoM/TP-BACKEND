package utn.frc.isi.backend.tpi_Integrador.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utn.frc.isi.backend.tpi_Integrador.models.Ruta;
import utn.frc.isi.backend.tpi_Integrador.models.Tramo;

import java.util.List;

@Repository // Buena práctica para indicar que es un componente de persistencia
public interface TramoRepository extends JpaRepository<Tramo, Long> {
    // Con solo esta línea, Spring Data JPA nos dará métodos como:
    // - save()
    // - findById()
    // - findAll()
    // - deleteById()
    // ¡Y muchos más, sin necesidad de implementarlos!
    
    /**
     * Busca todos los tramos de una ruta específica, ordenados por su orden ascendente
     * Útil para verificar el progreso de una ruta completa
     */
    List<Tramo> findByRutaOrderByOrdenAsc(Ruta ruta);
    
    /**
     * Busca todos los tramos de una ruta por su ID
     */
    List<Tramo> findByRutaId(Long rutaId);
    
    /**
     * RF#7: Busca tramos asignados a un camión específico que NO estén finalizados
     * Permite al transportista ver sus tramos pendientes de ejecución
     * 
     * @param camionId ID del camión asignado al transportista
     * @param estados Lista de estados a excluir (típicamente "FINALIZADO")
     * @return Lista de tramos asignados al camión que no están en los estados excluidos
     */
    List<Tramo> findByCamionReference_IdAndEstadoNotIn(Long camionId, List<String> estados);
}