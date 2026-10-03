package com.techcenter.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idusuario;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordhash;

    private Boolean activo = true;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechacreacion;

    @Column(name = "proveedor_auth")
    private String proveedorauth = "LOCAL";

    @Column(name = "google_id")
    private String googleid;

    @Column(name = "foto_perfil")
    private String fotoperfil;

    @Column(name = "email_verificado")
    private Boolean emailverificado = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuario_roles", joinColumns = @JoinColumn(name = "id_usuario"), inverseJoinColumns = @JoinColumn(name = "id_rol"))
    private Set<Rol> roles = new HashSet<>();
}
