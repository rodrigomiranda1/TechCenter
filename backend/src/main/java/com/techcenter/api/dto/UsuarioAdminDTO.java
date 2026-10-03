package com.techcenter.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioAdminDTO {
    private Long idUsuario;
    private String username;
    private String email;
    private String password;
    private Boolean activo;
    private String proveedorAuth;
    private String fotoPerfil;
    private Set<String> roles;
    private Long idRol;
}
