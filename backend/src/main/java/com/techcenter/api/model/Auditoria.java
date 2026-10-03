package com.techcenter.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_auditoria")
    private Long idauditoria;

    private String accion;
    private String entidad;
    private LocalDateTime fecha;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

}
