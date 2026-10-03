package com.techcenter.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "proveedores")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Long idproveedor;

    private String ruc;

    @Column(name = "razon_social")
    private String razonsocial;

    private String telefono;
    private String correo;
    private String direccion;
}
