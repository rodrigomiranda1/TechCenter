package com.techcenter.api.controllerapi;

import com.techcenter.api.dto.ClienteAdminDTO;
import com.techcenter.api.dto.ProveedorDTO;
import com.techcenter.api.dto.RolDTO;
import com.techcenter.api.dto.UsuarioAdminDTO;
import com.techcenter.api.model.Cliente;
import com.techcenter.api.model.Proveedor;
import com.techcenter.api.model.Rol;
import com.techcenter.api.model.Usuario;
import com.techcenter.api.repository.ClienteRepository;
import com.techcenter.api.repository.ProveedorRepository;
import com.techcenter.api.repository.RolRepository;
import com.techcenter.api.repository.UsuarioRepository;
import com.techcenter.api.service.AuditoriaService;
import com.techcenter.api.service.PasswordService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class PersonasRestController {

    private final ClienteRepository clienteRepository;
    private final ProveedorRepository proveedorRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final AuditoriaService auditoriaService;
    private final PasswordService passwordService;

    @GetMapping("/roles")
    public ResponseEntity<?> listarRoles(HttpSession session) {
        if (!esAdmin(session)) return sinPermiso();
        var roles = rolRepository.findAll().stream()
                .map(r -> new RolDTO(r.getIdrol(), r.getNombre())).toList();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/clientes")
    public ResponseEntity<?> listarClientes(HttpSession session) {
        if (!esAdmin(session)) return sinPermiso();
        var lista = clienteRepository.findAll().stream().map(this::convertirCliente).toList();
        return ResponseEntity.ok(lista);
    }

    @PostMapping("/clientes")
    public ResponseEntity<?> crearCliente(@RequestBody ClienteAdminDTO dto, HttpSession session) {
        if (!esAdmin(session)) return sinPermiso();

        if (dto.getDni() != null && !dto.getDni().isBlank() && !clienteRepository.findAllByDni(dto.getDni()).isEmpty()) {
            return new ResponseEntity<>(Map.of("mensaje", "El DNI '" + dto.getDni() + "' ya está registrado"), HttpStatus.CONFLICT);
        }

        Cliente cliente = new Cliente();
        aplicarCliente(cliente, dto);
        clienteRepository.save(cliente);
        auditoriaService.registrar("Guardo cliente", cliente.getNombres());
        return new ResponseEntity<>(Map.of("mensaje", "Cliente guardado correctamente", "cliente", convertirCliente(cliente)), HttpStatus.CREATED);
    }

    @PutMapping("/clientes/{id}")
    public ResponseEntity<?> actualizarCliente(@PathVariable Long id, @RequestBody ClienteAdminDTO dto, HttpSession session) {
        if (!esAdmin(session)) return sinPermiso();
        Cliente cliente = clienteRepository.findById(id).orElse(null);
        if (cliente == null) return new ResponseEntity<>(Map.of("mensaje", "Cliente no encontrado"), HttpStatus.NOT_FOUND);
        aplicarCliente(cliente, dto);
        clienteRepository.save(cliente);
        auditoriaService.registrar("Guardo cliente", cliente.getNombres());
        return ResponseEntity.ok(Map.of("mensaje", "Cliente actualizado correctamente", "cliente", convertirCliente(cliente)));
    }

    @DeleteMapping("/clientes/{id}")
    public ResponseEntity<?> eliminarCliente(@PathVariable Long id, HttpSession session) {
        if (!esAdmin(session)) return sinPermiso();
        try {
            Cliente cliente = clienteRepository.findById(id).orElseThrow();
            String nombre = cliente.getNombres();
            clienteRepository.delete(cliente);
            auditoriaService.registrar("Elimino cliente", nombre);
            return ResponseEntity.ok(Map.of("mensaje", "Cliente eliminado correctamente"));
        } catch (Exception ex) {
            return new ResponseEntity<>(Map.of("mensaje", "No se puede eliminar el cliente porque tiene ventas, pedidos o historial asociado."), HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/proveedores")
    public ResponseEntity<?> listarProveedores(HttpSession session) {
        if (!esAdmin(session)) return sinPermiso();
        var lista = proveedorRepository.findAll().stream().map(this::convertirProveedor).toList();
        return ResponseEntity.ok(lista);
    }

    @PostMapping("/proveedores")
    public ResponseEntity<?> crearProveedor(@RequestBody ProveedorDTO dto, HttpSession session) {
        if (!esAdmin(session)) return sinPermiso();
        Proveedor proveedor = new Proveedor();
        aplicarProveedor(proveedor, dto);
        proveedorRepository.save(proveedor);
        auditoriaService.registrar("Guardo proveedor", proveedor.getRazonsocial());
        return new ResponseEntity<>(Map.of("mensaje", "Proveedor guardado correctamente", "proveedor", convertirProveedor(proveedor)), HttpStatus.CREATED);
    }

    @PutMapping("/proveedores/{id}")
    public ResponseEntity<?> actualizarProveedor(@PathVariable Long id, @RequestBody ProveedorDTO dto, HttpSession session) {
        if (!esAdmin(session)) return sinPermiso();
        Proveedor proveedor = proveedorRepository.findById(id).orElse(null);
        if (proveedor == null) return new ResponseEntity<>(Map.of("mensaje", "Proveedor no encontrado"), HttpStatus.NOT_FOUND);
        aplicarProveedor(proveedor, dto);
        proveedorRepository.save(proveedor);
        auditoriaService.registrar("Guardo proveedor", proveedor.getRazonsocial());
        return ResponseEntity.ok(Map.of("mensaje", "Proveedor actualizado correctamente", "proveedor", convertirProveedor(proveedor)));
    }

    @DeleteMapping("/proveedores/{id}")
    public ResponseEntity<?> eliminarProveedor(@PathVariable Long id, HttpSession session) {
        if (!esAdmin(session)) return sinPermiso();
        try {
            Proveedor proveedor = proveedorRepository.findById(id).orElseThrow();
            String razonSocial = proveedor.getRazonsocial();
            proveedorRepository.delete(proveedor);
            auditoriaService.registrar("Elimino proveedor", razonSocial);
            return ResponseEntity.ok(Map.of("mensaje", "Proveedor eliminado correctamente"));
        } catch (Exception ex) {
            return new ResponseEntity<>(Map.of("mensaje", "No se puede eliminar el proveedor porque tiene compras asociadas."), HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/usuarios")
    public ResponseEntity<?> listarUsuarios(HttpSession session) {
        if (!esAdmin(session)) return sinPermiso();
        var lista = usuarioRepository.findAll().stream().map(this::convertirUsuario).toList();
        return ResponseEntity.ok(lista);
    }

    @PostMapping("/usuarios")
    public ResponseEntity<?> crearUsuario(@RequestBody UsuarioAdminDTO dto, HttpSession session) {
        if (!esAdmin(session)) return sinPermiso();

        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            return new ResponseEntity<>(Map.of("mensaje", "El usuario '" + dto.getUsername() + "' ya existe."), HttpStatus.CONFLICT);
        }
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            return new ResponseEntity<>(Map.of("mensaje", "El correo '" + dto.getEmail() + "' ya está registrado."), HttpStatus.CONFLICT);
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(dto.getUsername());
        usuario.setEmail(dto.getEmail());
        usuario.setFechacreacion(java.time.LocalDateTime.now());
        aplicarPassword(usuario, dto.getPassword());
        usuario.setActivo(true);
        aplicarRol(usuario, dto.getIdRol());

        try {
            usuarioRepository.save(usuario);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(Map.of("mensaje", "El correo ya está registrado."), HttpStatus.CONFLICT);
        }

        auditoriaService.registrar("Guardo usuario", usuario.getUsername());
        return new ResponseEntity<>(Map.of("mensaje", "Usuario guardado correctamente", "usuario", convertirUsuario(usuario)), HttpStatus.CREATED);
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody UsuarioAdminDTO dto, HttpSession session) {
        if (!esAdmin(session)) return sinPermiso();

        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) return new ResponseEntity<>(Map.of("mensaje", "Usuario no encontrado"), HttpStatus.NOT_FOUND);

        if (!usuario.getUsername().equals(dto.getUsername()) && usuarioRepository.existsByUsername(dto.getUsername())) {
            return new ResponseEntity<>(Map.of("mensaje", "El usuario '" + dto.getUsername() + "' ya existe."), HttpStatus.CONFLICT);
        }
        if (!usuario.getEmail().equals(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
            return new ResponseEntity<>(Map.of("mensaje", "El correo '" + dto.getEmail() + "' ya está registrado."), HttpStatus.CONFLICT);
        }

        usuario.setUsername(dto.getUsername());
        usuario.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            aplicarPassword(usuario, dto.getPassword());
        }
        aplicarRol(usuario, dto.getIdRol());

        try {
            usuarioRepository.save(usuario);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(Map.of("mensaje", "El correo ya está registrado."), HttpStatus.CONFLICT);
        }

        auditoriaService.registrar("Guardo usuario", usuario.getUsername());
        return ResponseEntity.ok(Map.of("mensaje", "Usuario actualizado correctamente", "usuario", convertirUsuario(usuario)));
    }

    @PutMapping("/usuarios/{id}/activar")
    public ResponseEntity<?> activarUsuario(@PathVariable Long id, HttpSession session) {
        if (!esAdmin(session)) return sinPermiso();
        Usuario usuario = usuarioRepository.findById(id).orElseThrow();
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
        return ResponseEntity.ok(Map.of("mensaje", "Usuario activado correctamente"));
    }

    @PutMapping("/usuarios/{id}/desactivar")
    public ResponseEntity<?> desactivarUsuario(@PathVariable Long id, HttpSession session) {
        if (!esAdmin(session)) return sinPermiso();
        Usuario usuario = usuarioRepository.findById(id).orElseThrow();
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
        return ResponseEntity.ok(Map.of("mensaje", "Usuario desactivado correctamente"));
    }

    private void aplicarCliente(Cliente cliente, ClienteAdminDTO dto) {
        cliente.setNombres(dto.getNombres());
        cliente.setApellidos(dto.getApellidos());
        cliente.setDni(dto.getDni());
        cliente.setTelefono(dto.getTelefono());
        cliente.setDireccion(dto.getDireccion());
        cliente.setTipodocumento(dto.getTipoDocumento());
        cliente.setNumerodocumento(dto.getNumeroDocumento());
        cliente.setEmailfacturacion(dto.getEmailFacturacion());
        cliente.setRazonsocial(dto.getRazonSocial());
        cliente.setDireccionfiscal(dto.getDireccionFiscal());
        if (cliente.getFecharegistro() == null) cliente.setFecharegistro(java.time.LocalDateTime.now());
    }

    private void aplicarProveedor(Proveedor proveedor, ProveedorDTO dto) {
        proveedor.setRuc(dto.getRuc());
        proveedor.setRazonsocial(dto.getRazonSocial());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setCorreo(dto.getCorreo());
        proveedor.setDireccion(dto.getDireccion());
    }

    private void aplicarPassword(Usuario usuario, String password) {
        if (password == null || password.isBlank()) {
            usuario.setPasswordhash("$2a$10$pendiente");
        } else if (!password.startsWith("$2a$") && !password.startsWith("$2b$")) {
            usuario.setPasswordhash(passwordService.encriptar(password));
        } else {
            usuario.setPasswordhash(password);
        }
    }

    private void aplicarRol(Usuario usuario, Long idRol) {
        if (idRol != null) {
            Rol rol = rolRepository.findById(idRol).orElse(null);
            usuario.setRoles(new HashSet<>());
            if (rol != null) usuario.getRoles().add(rol);
        }
    }

    private ClienteAdminDTO convertirCliente(Cliente c) {
        return new ClienteAdminDTO(c.getIdcliente(), c.getNombres(), c.getApellidos(), c.getDni(), c.getTelefono(),
                c.getDireccion(), c.getTipodocumento(), c.getNumerodocumento(), c.getEmailfacturacion(),
                c.getRazonsocial(), c.getDireccionfiscal());
    }

    private ProveedorDTO convertirProveedor(Proveedor p) {
        return new ProveedorDTO(p.getIdproveedor(), p.getRuc(), p.getRazonsocial(), p.getTelefono(), p.getCorreo(), p.getDireccion());
    }

    private UsuarioAdminDTO convertirUsuario(Usuario u) {
        Set<String> roles = u.getRoles().stream().map(Rol::getNombre).collect(Collectors.toSet());
        return new UsuarioAdminDTO(u.getIdusuario(), u.getUsername(), u.getEmail(), null, u.getActivo(),
                u.getProveedorauth(), u.getFotoperfil(), roles, null);
    }

    @SuppressWarnings("unchecked")
    private boolean esAdmin(HttpSession session) {
        Object roles = session.getAttribute("roles");
        return roles instanceof Set<?> r && r.stream().anyMatch(rol -> "ADMIN".equals(String.valueOf(rol)));
    }

    private ResponseEntity<Map<String, Object>> sinPermiso() {
        Map<String, Object> body = new HashMap<>();
        body.put("mensaje", "Acceso solo para administradores");
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }
}
