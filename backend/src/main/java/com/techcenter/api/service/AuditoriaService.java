package com.techcenter.api.service;

import com.techcenter.api.model.Auditoria;
import com.techcenter.api.repository.AuditoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public void registrar(String accion, String entidad) {
        Auditoria auditoria = new Auditoria();
        auditoria.setAccion(accion);
        auditoria.setEntidad(entidad);
        auditoria.setFecha(LocalDateTime.now());
        auditoriaRepository.save(auditoria);
    }
}
