package com.techcenter.api.controllerapi;

import com.techcenter.api.dto.LoginDTO;
import com.techcenter.api.dto.UsuarioDTO;
import com.techcenter.api.serviceapi.UsuarioApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioRestController {

    private final UsuarioApiService usuarioApiService;

    // GET /api/usuarios
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {

        return ResponseEntity.ok(usuarioApiService.listar());
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody LoginDTO dto) {

        Map<String, Object> response = new HashMap<>();

        try {
            UsuarioDTO usuario = usuarioApiService.login(dto);
            response.put("mensaje", "Inicio de sesión correcto");
            response.put("usuario", usuario);

            return new ResponseEntity<>(response,HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            response.put("mensaje", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);

        } catch (Exception e) {
            response.put("mensaje", "Ocurrió un error al iniciar sesión");
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
