package utn.frc.isi.backend.tpi_Integrador.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utn.frc.isi.backend.tpi_Integrador.models.DepositoReference;
import utn.frc.isi.backend.tpi_Integrador.services.DepositoReferenceService;

import java.util.List;

@RestController
@RequestMapping("/api/deposito-references")
public class DepositoReferenceController {

    private final DepositoReferenceService depositoReferenceService;

    public DepositoReferenceController(DepositoReferenceService depositoReferenceService) {
        this.depositoReferenceService = depositoReferenceService;
    }

    @GetMapping
    public ResponseEntity<List<DepositoReference>> obtenerTodos() {
        List<DepositoReference> depositoReferences = depositoReferenceService.obtenerTodos();
        return ResponseEntity.ok(depositoReferences);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepositoReference> obtenerPorId(@PathVariable Long id) {
        return depositoReferenceService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DepositoReference> crear(@RequestBody DepositoReference depositoReference) {
        DepositoReference nuevoDepositoReference = depositoReferenceService.crearDepositoReference(depositoReference);
        return ResponseEntity.ok(nuevoDepositoReference);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepositoReference> actualizar(@PathVariable Long id, @RequestBody DepositoReference depositoReference) {
        DepositoReference depositoReferenceActualizado = depositoReferenceService.actualizarDepositoReference(id, depositoReference);
        if (depositoReferenceActualizado != null) {
            return ResponseEntity.ok(depositoReferenceActualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        depositoReferenceService.eliminarDepositoReference(id);
        return ResponseEntity.noContent().build();
    }
}