package com.techcenter.api.controllerapi;

import com.techcenter.api.dto.LoginDTO;
import com.techcenter.api.dto.UsuarioDTO;
import com.techcenter.api.model.Usuario;
import com.techcenter.api.repository.RolRepository;
import com.techcenter.api.repository.UsuarioRepository;
import com.techcenter.api.service.PasswordService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@RequiredArgsConstructor
public class AuthController {
    private final UsuarioRepository usuarioRepository;
    private final PasswordService passwordService;
    private final com.techcenter.api.repository.RolRepository rolRepository;
    private final com.techcenter.api.repository.ClienteRepository clienteRepository;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginDTO dto, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        String login = dto.getUsuario() == null ? "" : dto.getUsuario().trim();

        System.out.println("=== INTENTO DE LOGIN ===");
        System.out.println("Login recibido: " + login);

        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(login)
                .or(() -> usuarioRepository.findByEmailIgnoreCase(login))
                .orElse(null);

        if (usuario == null || Boolean.FALSE.equals(usuario.getActivo())) {
            System.out.println("ERROR: Usuario no encontrado o inactivo");
            response.put("mensaje", "Usuario no encontrado o inactivo");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        System.out.println("Usuario hallado en BD: " + usuario.getUsername());
        System.out.println("Hash en BD: " + usuario.getPasswordhash());

        boolean match = passwordService.coincide(dto.getPassword(), usuario.getPasswordhash());
        System.out.println("¿Coincide la contraseña?: " + match);

        if (!match) {
            response.put("mensaje", "Contraseña incorrecta");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        Set<String> roles = new HashSet<>();
        usuario.getRoles().forEach(rol -> roles.add(rol.getNombre()));

        session.setAttribute("usuarioActual", usuario);
        session.setAttribute("roles", roles);

        UsuarioDTO usuarioDTO = new UsuarioDTO(usuario.getIdusuario(), usuario.getUsername(),
                usuario.getEmail(), usuario.getActivo(), roles);

        response.put("mensaje", "Inicio de sesión correcto");
        response.put("usuario", usuarioDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("mensaje", "Sesión cerrada"));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(HttpSession session) {
        Object usuarioActual = session.getAttribute("usuarioActual");
        if (usuarioActual == null) {
            return new ResponseEntity<>(Map.of("mensaje", "No hay sesión activa"), HttpStatus.UNAUTHORIZED);
        }
        Usuario usuario = (Usuario) usuarioActual;
        Set<String> roles = new HashSet<>();
        usuario.getRoles().forEach(rol -> roles.add(rol.getNombre()));
        UsuarioDTO usuarioDTO = new UsuarioDTO(usuario.getIdusuario(), usuario.getUsername(),
                usuario.getEmail(), usuario.getActivo(), roles);
        return ResponseEntity.ok(Map.of("usuario", usuarioDTO));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody com.techcenter.api.dto.RegistroDTO dto, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        String username = dto.getUsername() == null ? "" : dto.getUsername().trim();
        String email = dto.getEmail() == null ? "" : dto.getEmail().trim();
        String nombres = dto.getNombres() == null ? "" : dto.getNombres().trim();
        String apellidos = dto.getApellidos() == null ? "" : dto.getApellidos().trim();

        if (username.isBlank() || email.isBlank() || dto.getPassword() == null || dto.getPassword().isBlank()
                || nombres.isBlank()) {
            response.put("mensaje", "Usuario, correo, contraseña y nombres son obligatorios");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if (usuarioRepository.existsByUsernameIgnoreCase(username) || usuarioRepository.existsByEmailIgnoreCase(email)) {
            response.put("mensaje", "El usuario o correo ya existe");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        com.techcenter.api.model.Rol rolCliente = rolRepository.findByNombre("CLIENTE");
        if (rolCliente == null) {
            response.put("mensaje", "No existe el rol CLIENTE en la base de datos");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Usuario nuevo = new Usuario();
        nuevo.setUsername(username);
        nuevo.setEmail(email);
        nuevo.setPasswordhash(passwordService.encriptar(dto.getPassword()));
        nuevo.setActivo(true);
        nuevo.setFechacreacion(java.time.LocalDateTime.now());
        nuevo.setProveedorauth("LOCAL");
        nuevo.getRoles().add(rolCliente);

        usuarioRepository.save(nuevo);

        com.techcenter.api.model.Cliente cliente = new com.techcenter.api.model.Cliente();
        cliente.setUsuario(nuevo);
        cliente.setNombres(nombres);
        cliente.setApellidos(apellidos.isBlank() ? null : apellidos);
        cliente.setEmailfacturacion(email);
        cliente.setFecharegistro(java.time.LocalDateTime.now());
        clienteRepository.save(cliente);

        Set<String> roles = new HashSet<>();
        nuevo.getRoles().forEach(rol -> roles.add(rol.getNombre()));

        UsuarioDTO usuarioDTO = new UsuarioDTO(nuevo.getIdusuario(), nuevo.getUsername(),
                nuevo.getEmail(), nuevo.getActivo(), roles);

        response.put("mensaje", "Usuario registrado correctamente");
        response.put("usuario", usuarioDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
