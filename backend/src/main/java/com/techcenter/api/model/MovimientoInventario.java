package com.techcenter.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "movimientos_inventario")
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento")
    private Long idmovimiento;

    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;

    @Column(name = "tipo_movimiento")
    private String tipomovimiento;

    private Integer cantidad;
    private String observacion;

    @Column(name = "fecha_movimiento")
    private LocalDateTime fechamovimiento;
}
