package com.techcenter.api.controller;

import com.techcenter.api.model.Cliente;
import com.techcenter.api.model.Proveedor;
import com.techcenter.api.model.Rol;
import com.techcenter.api.model.Usuario;
import com.techcenter.api.repository.*;
import com.techcenter.api.service.AuditoriaService;
import com.techcenter.api.service.PasswordService;
import com.techcenter.api.service.ProductoImagenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final CategoriaRepository categoriaRepository;
    private final MarcaRepository marcaRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    private final ProveedorRepository proveedorRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final AuditoriaRepository auditoriaRepository;
    private final AuditoriaService auditoriaService;
    private final ProductoImagenService productoImagenService;
    private final PasswordService passwordService;

    @GetMapping("/personas")
    public String personas(Model model) {
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("proveedores", proveedorRepository.findAll());
        model.addAttribute("usuarios", usuarioRepository.findAll());
        model.addAttribute("roles", rolRepository.findAll());
        return "admin/personas";
    }

    @PostMapping("/clientes/guardar")
    public String guardarCliente(Cliente cliente, RedirectAttributes redirect) {
        Cliente clienteGuardar = cliente;
        if (cliente.getIdcliente() != null) {
            Cliente existente = clienteRepository.findById(cliente.getIdcliente()).orElseThrow();
            existente.setNombres(cliente.getNombres());
            existente.setApellidos(cliente.getApellidos());
            existente.setDni(cliente.getDni());
            existente.setTelefono(cliente.getTelefono());
            existente.setDireccion(cliente.getDireccion());
            existente.setTipodocumento(cliente.getTipodocumento());
            existente.setNumerodocumento(cliente.getNumerodocumento());
            existente.setEmailfacturacion(cliente.getEmailfacturacion());
            existente.setRazonsocial(cliente.getRazonsocial());
            existente.setDireccionfiscal(cliente.getDireccionfiscal());
            clienteGuardar = existente;
        }
        clienteRepository.save(clienteGuardar);
        auditoriaService.registrar("Guardo cliente", clienteGuardar.getNombres());
        redirect.addFlashAttribute("mensajeExito", "Cliente guardado correctamente");
        return "redirect:/admin/personas";
    }

    @GetMapping("/clientes/eliminar/{id}")
    public String eliminarCliente(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            Cliente cliente = clienteRepository.findById(id).orElseThrow();
            String nombre = cliente.getNombres();
            clienteRepository.delete(cliente);
            auditoriaService.registrar("Elimino cliente", nombre);
            redirect.addFlashAttribute("mensajeExito", "Cliente eliminado correctamente");
        } catch (Exception ex) {
            redirect.addFlashAttribute("error", "No se puede eliminar el cliente porque tiene ventas, pedidos o historial asociado.");
        }
        return "redirect:/admin/personas";
    }

    @PostMapping("/proveedores/guardar")
    public String guardarProveedor(Proveedor proveedor, RedirectAttributes redirect) {
        proveedorRepository.save(proveedor);
        auditoriaService.registrar("Guardo proveedor", proveedor.getRazonsocial());
        redirect.addFlashAttribute("mensajeExito", "Proveedor guardado correctamente");
        return "redirect:/admin/personas";
    }

    @GetMapping("/proveedores/eliminar/{id}")
    public String eliminarProveedor(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            Proveedor proveedor = proveedorRepository.findById(id).orElseThrow();
            String razonSocial = proveedor.getRazonsocial();
            proveedorRepository.delete(proveedor);
            auditoriaService.registrar("Elimino proveedor", razonSocial);
            redirect.addFlashAttribute("mensajeExito", "Proveedor eliminado correctamente");
        } catch (Exception ex) {
            redirect.addFlashAttribute("error", "No se puede eliminar el proveedor porque tiene compras asociadas.");
        }
        return "redirect:/admin/personas";
    }

    @PostMapping("/usuarios/guardar")
    public String guardarUsuario(Usuario usuario, @RequestParam(required = false) Long idRol,
                                 RedirectAttributes redirect) {

        if (usuario.getIdusuario() == null) {

            if (usuarioRepository.existsByUsername(usuario.getUsername())) {
                redirect.addFlashAttribute("error",
                        "El usuario '" + usuario.getUsername() + "' ya existe.");
                return "redirect:/admin/personas";
            }
            if (usuarioRepository.existsByEmail(usuario.getEmail())) {
                redirect.addFlashAttribute("error",
                        "El correo '" + usuario.getEmail() + "' ya está registrado.");
                return "redirect:/admin/personas";
            }

        } else {
            Usuario usuarioExistente = usuarioRepository.findById(usuario.getIdusuario()).orElse(null);

            if (usuarioExistente != null &&
                    !usuarioExistente.getUsername().equals(usuario.getUsername()) &&
                    usuarioRepository.existsByUsername(usuario.getUsername())) {

                redirect.addFlashAttribute("error",
                        "El usuario '" + usuario.getUsername() + "' ya existe.");
                return "redirect:/admin/personas";
            }
            if (usuarioExistente != null &&
                    !usuarioExistente.getEmail().equals(usuario.getEmail()) &&
                    usuarioRepository.existsByEmail(usuario.getEmail())) {

                redirect.addFlashAttribute("error",
                        "El correo '" + usuario.getEmail() + "' ya está registrado.");
                return "redirect:/admin/personas";
            }
        }

        if (usuario.getPasswordhash() == null || usuario.getPasswordhash().isBlank()) {
            usuario.setPasswordhash("$2a$10$pendiente");
        } else if (!usuario.getPasswordhash().startsWith("$2a$")
                && !usuario.getPasswordhash().startsWith("$2b$")) {
            usuario.setPasswordhash(passwordService.encriptar(usuario.getPasswordhash()));
        }

        usuario.setActivo(usuario.getActivo() == null || usuario.getActivo());

        if (idRol != null) {
            Rol rol = rolRepository.findById(idRol).orElse(null);
            usuario.setRoles(new HashSet<>());

            if (rol != null) {
                usuario.getRoles().add(rol);
            }
        }

        try {
            usuarioRepository.save(usuario);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            redirect.addFlashAttribute("error",
                    "El correo '" + usuario.getEmail() + "' ya está registrado.");
            return "redirect:/admin/personas";
        }

        usuarioRepository.save(usuario);

        auditoriaService.registrar("Guardo usuario", usuario.getUsername());

        redirect.addFlashAttribute("mensajeExito",
                "Usuario guardado correctamente");

        return "redirect:/admin/personas";
    }

    @GetMapping("/usuarios/desactivar/{id}")
    public String desactivarUsuario(@PathVariable Long id, RedirectAttributes redirect) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow();
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
        redirect.addFlashAttribute("mensajeExito", "Usuario desactivado correctamente");
        return "redirect:/admin/personas";
    }

    @GetMapping("/usuarios/activar/{id}")
    public String activarUsuario(@PathVariable Long id, RedirectAttributes redirect) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow();
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
        redirect.addFlashAttribute("mensajeExito", "Usuario activado correctamente");
        return "redirect:/admin/personas";
    }

    @GetMapping("/auditoria")
    public String auditoria(Model model) {
        model.addAttribute("auditorias", auditoriaRepository.findAll());
        return "admin/auditoria";
    }
}
