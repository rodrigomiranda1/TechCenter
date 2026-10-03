package com.techcenter.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Long idcliente;

    @OneToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    private String nombres;
    private String apellidos;
    private String dni;
    private String telefono;
    private String direccion;

    @Column(name = "telefono_verificado")
    private Boolean telefonoverificado = false;

    @Column(name = "tipo_documento")
    private String tipodocumento = "DNI";

    @Column(name = "numero_documento")
    private String numerodocumento;

    @Column(name = "razon_social")
    private String razonsocial;

    @Column(name = "direccion_fiscal")
    private String direccionfiscal;

    @Column(name = "email_facturacion")
    private String emailfacturacion;

    @Column(name = "fecha_registro")
    private LocalDateTime fecharegistro;
}
