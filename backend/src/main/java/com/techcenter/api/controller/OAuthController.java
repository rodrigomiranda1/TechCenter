package com.techcenter.api.controller;

import com.techcenter.api.model.Cliente;
import com.techcenter.api.model.Rol;
import com.techcenter.api.model.Usuario;
import com.techcenter.api.repository.ClienteRepository;
import com.techcenter.api.repository.RolRepository;
import com.techcenter.api.repository.UsuarioRepository;
import com.techcenter.api.service.PasswordService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class OAuthController {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final RolRepository rolRepository;
    private final PasswordService passwordService;

    @GetMapping("/oauth2/google/success")
    public String googleSuccess(OAuth2AuthenticationToken authentication, HttpSession session,
                                RedirectAttributes redirect) {
        if (authentication == null) {
            redirect.addFlashAttribute("error", "No se pudo completar el acceso con Google");
            return "redirect:/login";
        }
        var attrs = authentication.getPrincipal().getAttributes();
        String googleId = String.valueOf(attrs.get("sub"));
        String email = String.valueOf(attrs.get("email"));
        String nombre = valor(attrs.get("given_name"), valor(attrs.get("name"), "Cliente"));
        String apellido = valor(attrs.get("family_name"), "");
        String foto = valor(attrs.get("picture"), "");
        boolean emailVerificado = Boolean.parseBoolean(String.valueOf(attrs.getOrDefault("email_verified", "true")));

        Usuario usuario = usuarioRepository.findByGoogleid(googleId)
                .or(() -> usuarioRepository.findByEmailIgnoreCase(email))
                .orElseGet(() -> crearUsuarioGoogle(googleId, email, foto, emailVerificado));

        usuario.setProveedorauth("GOOGLE");
        usuario.setGoogleid(googleId);
        usuario.setFotoperfil(foto);
        usuario.setEmailverificado(emailVerificado);
        usuario.setActivo(true);
        usuarioRepository.save(usuario);

        Usuario usuarioFinal = usuario;
        clienteRepository.findByUsuario(usuarioFinal).orElseGet(() -> crearClienteGoogle(usuarioFinal, nombre, apellido, email));

        session.setAttribute("usuarioActual", usuario);
        session.setAttribute("roles", nombresRoles(usuario));
        redirect.addFlashAttribute("mensajeExito", "Sesion iniciada con Google");
        return "redirect:/";
    }

    private Usuario crearUsuarioGoogle(String googleId, String email, String foto, boolean emailVerificado) {
        Usuario usuario = new Usuario();
        usuario.setUsername(usernameDesdeEmail(email));
        usuario.setEmail(email);
        usuario.setPasswordhash(passwordService.encriptar(UUID.randomUUID().toString()));
        usuario.setActivo(true);
        usuario.setFechacreacion(LocalDateTime.now());
        usuario.setProveedorauth("GOOGLE");
        usuario.setGoogleid(googleId);
        usuario.setFotoperfil(foto);
        usuario.setEmailverificado(emailVerificado);
        Rol clienteRol = rolRepository.findByNombre("CLIENTE");
        if (clienteRol != null) {
            usuario.getRoles().add(clienteRol);
        }
        return usuarioRepository.save(usuario);
    }

    private Cliente crearClienteGoogle(Usuario usuario, String nombre, String apellido, String email) {
        Cliente cliente = new Cliente();
        cliente.setUsuario(usuario);
        cliente.setNombres(nombre);
        cliente.setApellidos(apellido);
        cliente.setEmailfacturacion(email);
        cliente.setTelefonoverificado(false);
        cliente.setFecharegistro(LocalDateTime.now());
        return clienteRepository.save(cliente);
    }

    private String usernameDesdeEmail(String email) {
        String base = email == null || !email.contains("@") ? "google" : email.substring(0, email.indexOf('@'));
        base = base.replaceAll("[^a-zA-Z0-9._-]", "").toLowerCase();
        if (base.length() < 4) {
            base = "user" + base;
        }
        String candidato = base;
        int contador = 1;
        while (usuarioRepository.existsByUsername(candidato)) {
            candidato = base + contador++;
        }
        return candidato;
    }

    private Set<String> nombresRoles(Usuario usuario) {
        Set<String> roles = new HashSet<>();
        usuario.getRoles().forEach(rol -> roles.add(rol.getNombre()));
        return roles;
    }

    private String valor(Object valor, String defecto) {
        return valor == null ? defecto : String.valueOf(valor);
    }
}
