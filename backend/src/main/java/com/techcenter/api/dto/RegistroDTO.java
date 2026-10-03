package com.techcenter.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroDTO {
    private String username;
    private String email;
    private String password;
    private String nombres;
    private String apellidos;
}
