package utn.frc.isi.backend.tpi_Integrador.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tarifas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tarifa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double costoKmBase;

    @Column(nullable = false)
    private double precioLitroCombustible;

    @Column(nullable = false)
    private double cargoGestionPorTramo;
    
    @Column(nullable = false)
    private double costoEstadiaDiaria;

    @Column(nullable = false)
    private LocalDateTime vigenciaDesde;

    private LocalDateTime vigenciaHasta;

    @Column(nullable = false)
    private boolean activa = true;

    // Constructor para creación con parámetros básicos
    public Tarifa(double costoKmBase, double precioLitroCombustible, 
                  double cargoGestionPorTramo, double costoEstadiaDiaria,
                  LocalDateTime vigenciaDesde) {
        this.costoKmBase = costoKmBase;
        this.precioLitroCombustible = precioLitroCombustible;
        this.cargoGestionPorTramo = cargoGestionPorTramo;
        this.costoEstadiaDiaria = costoEstadiaDiaria;
        this.vigenciaDesde = vigenciaDesde;
        this.activa = true;
    }
}