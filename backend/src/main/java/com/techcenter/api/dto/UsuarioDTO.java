package com.techcenter.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Long idUsuario;
    private String username;
    private String email;
    private Boolean activo;
    private Set<String> roles;
}
