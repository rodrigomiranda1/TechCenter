package com.techcenter.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "consultas_ia")
public class ConsultaIa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consulta")
    private Long idconsulta;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    private String pregunta;

    @Column(columnDefinition = "TEXT")
    private String respuesta;

    @Column(name = "fecha_consulta")
    private LocalDateTime fechaconsulta;
}
