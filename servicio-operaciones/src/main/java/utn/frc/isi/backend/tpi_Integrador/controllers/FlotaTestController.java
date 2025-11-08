package utn.frc.isi.backend.tpi_Integrador.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utn.frc.isi.backend.tpi_Integrador.clients.FlotaServiceClient;
import utn.frc.isi.backend.tpi_Integrador.dtos.flota.CamionDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.flota.TarifaDTO;

/**
 * Controlador de prueba para verificar la comunicación con servicio-flota
 * Endpoints de ejemplo para consumir datos de Tarifas y Camiones
 */
@RestController
@RequestMapping("/api/test-flota")
public class FlotaTestController {

    private final FlotaServiceClient flotaServiceClient;

    public FlotaTestController(FlotaServiceClient flotaServiceClient) {
        this.flotaServiceClient = flotaServiceClient;
    }

    /**
     * GET /api/test-flota/tarifa-actual
     * Prueba: Obtiene la tarifa activa desde servicio-flota
     */
    @GetMapping("/tarifa-actual")
    public ResponseEntity<TarifaDTO> obtenerTarifaActual() {
        return flotaServiceClient.obtenerTarifaActiva()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * GET /api/test-flota/camion/{id}
     * Prueba: Obtiene información de un camión desde servicio-flota
     */
    @GetMapping("/camion/{id}")
    public ResponseEntity<CamionDTO> obtenerCamion(@PathVariable Long id) {
        return flotaServiceClient.obtenerCamionPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
