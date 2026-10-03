package com.techcenter.api.controllerapi;

import com.techcenter.api.dto.ClientePerfilDTO;
import com.techcenter.api.model.Cliente;
import com.techcenter.api.model.Usuario;
import com.techcenter.api.repository.ClienteRepository;
import com.techcenter.api.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/cliente")
@RequiredArgsConstructor
public class ClienteRestController {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;

    @GetMapping("/perfil")
    public ResponseEntity<Map<String, Object>> obtenerPerfil(HttpSession session) {
        Usuario usuario = usuarioActual(session);
        if (usuario == null) {
            return new ResponseEntity<>(Map.of("mensaje", "No hay sesión activa"), HttpStatus.UNAUTHORIZED);
        }

        Cliente cliente = clienteRepository.findByUsuario(usuario).orElseGet(() -> crearClienteBasico(usuario));

        return ResponseEntity.ok(Map.of("perfil", convertirDTO(usuario, cliente)));
    }

    @PutMapping("/perfil")
    public ResponseEntity<Map<String, Object>> actualizarPerfil(@RequestBody ClientePerfilDTO dto, HttpSession session) {
        Usuario usuario = usuarioActual(session);
        if (usuario == null) {
            return new ResponseEntity<>(Map.of("mensaje", "No hay sesión activa"), HttpStatus.UNAUTHORIZED);
        }

        Cliente cliente = clienteRepository.findByUsuario(usuario).orElseGet(() -> crearClienteBasico(usuario));

        cliente.setNombres(dto.getNombres());
        cliente.setApellidos(dto.getApellidos());
        cliente.setTelefono(dto.getTelefono());
        cliente.setDireccion(dto.getDireccion());
        cliente.setTipodocumento(dto.getTipoDocumento() == null || dto.getTipoDocumento().isBlank() ? "DNI" : dto.getTipoDocumento());
        cliente.setNumerodocumento(dto.getNumeroDocumento());
        cliente.setEmailfacturacion(dto.getEmailFacturacion());
        cliente.setDireccionfiscal(dto.getDireccionFiscal());
        cliente.setRazonsocial(dto.getRazonSocial());
        clienteRepository.save(cliente);

        if (dto.getEmail() != null && !dto.getEmail().equalsIgnoreCase(usuario.getEmail())) {
            usuario.setEmail(dto.getEmail());
            usuarioRepository.save(usuario);
        }

        session.setAttribute("usuarioActual", usuario);

        return ResponseEntity.ok(Map.of(
                "mensaje", "Perfil actualizado correctamente",
                "perfil", convertirDTO(usuario, cliente)
        ));
    }

    private Cliente crearClienteBasico(Usuario usuario) {
        Cliente cliente = new Cliente();
        cliente.setUsuario(usuario);
        cliente.setNombres(usuario.getUsername());
        cliente.setEmailfacturacion(usuario.getEmail());
        cliente.setFecharegistro(LocalDateTime.now());
        return clienteRepository.save(cliente);
    }

    private ClientePerfilDTO convertirDTO(Usuario usuario, Cliente cliente) {
        ClientePerfilDTO dto = new ClientePerfilDTO();
        dto.setUsername(usuario.getUsername());
        dto.setEmail(usuario.getEmail());
        dto.setFotoPerfil(usuario.getFotoperfil());
        dto.setProveedorAuth(usuario.getProveedorauth());
        dto.setEmailVerificado(usuario.getEmailverificado());

        dto.setNombres(cliente.getNombres());
        dto.setApellidos(cliente.getApellidos());
        dto.setTelefono(cliente.getTelefono());
        dto.setDireccion(cliente.getDireccion());
        dto.setTelefonoVerificado(cliente.getTelefonoverificado());
        dto.setTipoDocumento(cliente.getTipodocumento());
        dto.setNumeroDocumento(cliente.getNumerodocumento());
        dto.setEmailFacturacion(cliente.getEmailfacturacion());
        dto.setRazonSocial(cliente.getRazonsocial());
        dto.setDireccionFiscal(cliente.getDireccionfiscal());

        return dto;
    }

    private Usuario usuarioActual(HttpSession session) {
        Object usuario = session.getAttribute("usuarioActual");
        return usuario instanceof Usuario usuarioActual ? usuarioActual : null;
    }
}
