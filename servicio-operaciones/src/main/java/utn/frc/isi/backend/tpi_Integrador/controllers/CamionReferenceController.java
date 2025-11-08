package utn.frc.isi.backend.tpi_Integrador.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utn.frc.isi.backend.tpi_Integrador.models.CamionReference;
import utn.frc.isi.backend.tpi_Integrador.services.CamionReferenceService;

import java.util.List;

@RestController
@RequestMapping("/api/camion-references")
public class CamionReferenceController {

    private final CamionReferenceService camionReferenceService;

    public CamionReferenceController(CamionReferenceService camionReferenceService) {
        this.camionReferenceService = camionReferenceService;
    }

    @GetMapping
    public ResponseEntity<List<CamionReference>> obtenerTodos() {
        List<CamionReference> camionReferences = camionReferenceService.obtenerTodos();
        return ResponseEntity.ok(camionReferences);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CamionReference> obtenerPorId(@PathVariable Long id) {
        return camionReferenceService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CamionReference> crear(@RequestBody CamionReference camionReference) {
        CamionReference nuevoCamionReference = camionReferenceService.crearCamionReference(camionReference);
        return ResponseEntity.ok(nuevoCamionReference);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CamionReference> actualizar(@PathVariable Long id, @RequestBody CamionReference camionReference) {
        CamionReference camionReferenceActualizado = camionReferenceService.actualizarCamionReference(id, camionReference);
        if (camionReferenceActualizado != null) {
            return ResponseEntity.ok(camionReferenceActualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        camionReferenceService.eliminarCamionReference(id);
        return ResponseEntity.noContent().build();
    }
}