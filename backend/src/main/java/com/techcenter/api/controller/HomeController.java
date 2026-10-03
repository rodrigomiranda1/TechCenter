package com.techcenter.api.controller;

import com.techcenter.api.model.Cliente;
import com.techcenter.api.model.Producto;
import com.techcenter.api.model.Rol;
import com.techcenter.api.model.Usuario;
import com.techcenter.api.repository.*;
import com.techcenter.api.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final RolRepository rolRepository;
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final MarcaRepository marcaRepository;
    private final InventarioService inventarioService;
    private final ProductoImagenService productoImagenService;
    private final PasswordService passwordService;
    private final TwilioVerifyService twilioVerifyService;

    @GetMapping("/")
    public String inicio(@RequestParam(required = false) String q,
                         @RequestParam(defaultValue = "1") int pagina, Model model) {
        List<Producto> productosFiltrados = q == null || q.isBlank() ? productoRepository.findByActivoTrue()
                : productoRepository.findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(q, q);
        productosFiltrados = productosFiltrados.stream()
                .filter(producto -> !Boolean.FALSE.equals(producto.getActivo()))
                .sorted(Comparator.comparing(Producto::getIdproducto).reversed())
                .toList();

        int tamanoPagina = 12;
        int totalPaginas = Math.max(1, (int) Math.ceil(productosFiltrados.size() / (double) tamanoPagina));
        int paginaActual = Math.max(1, Math.min(pagina, totalPaginas));
        int desde = (paginaActual - 1) * tamanoPagina;
        int hasta = Math.min(desde + tamanoPagina, productosFiltrados.size());
        List<Producto> productos = productosFiltrados.subList(desde, hasta);

        model.addAttribute("productos", productos);
        model.addAttribute("imagenes", productoImagenService.imagenPrincipalPorProducto(productos));
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("marcas", marcaRepository.findAll());
        model.addAttribute("q", q);
        model.addAttribute("paginaActual", paginaActual);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("totalProductos", productosFiltrados.size());
        System.out.println("productos dentro de inicio son: " + productos.size());
        return "inicio";
    }

    @GetMapping("/producto/{id}")
    public String detalleProducto(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        var producto = productoRepository.findById(id).orElse(null);
        if (producto == null || Boolean.FALSE.equals(producto.getActivo())) {
            redirect.addFlashAttribute("error", "Producto no disponible");
            return "redirect:/";
        }
        var relacionados = producto.getCategoria() == null ? productoRepository.findByActivoTrue()
                : productoRepository.findByCategoriaAndActivoTrue(producto.getCategoria());
        model.addAttribute("producto", producto);
        model.addAttribute("imagen", productoImagenService.imagenPrincipal(producto));
        model.addAttribute("relacionados", relacionados.stream()
                .filter(p -> !p.getIdproducto().equals(producto.getIdproducto()))
                .limit(5)
                .toList());
        model.addAttribute("imagenes", productoImagenService.imagenPrincipalPorProducto(relacionados));
        return "producto-detalle";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String autenticar(@RequestParam String usuario, @RequestParam String password, HttpSession session,
                             RedirectAttributes redirect) {
        String login = usuario == null ? "" : usuario.trim();
        Usuario encontrado = usuarioRepository.findByUsernameIgnoreCase(login)
                .or(() -> usuarioRepository.findByEmailIgnoreCase(login))
                .orElse(null);
        if (encontrado == null || Boolean.FALSE.equals(encontrado.getActivo())) {
            redirect.addFlashAttribute("error", "Usuario no encontrado o inactivo");
            return "redirect:/login";
        }
        if (!passwordService.coincide(password, encontrado.getPasswordhash())) {
            redirect.addFlashAttribute("error", "Clave incorrecta");
            return "redirect:/login";
        }
        session.setAttribute("usuarioActual", encontrado);
        Set<String> roles = nombresRoles(encontrado);
        session.setAttribute("roles", roles);
        return destinoPorRol(roles);
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }

    @PostMapping("/registro/codigo/enviar")
    @ResponseBody
    public Map<String, Object> enviarCodigoRegistro(@RequestParam String telefono, HttpSession session) {
        String telefonoNormalizado = twilioVerifyService.normalizarTelefonoPeru(telefono);
        if (!twilioVerifyService.telefonoPeruValido(telefonoNormalizado)) {
            return Map.of("ok", false, "mensaje", "Ingresa un celular peruano valido. Ejemplo: 900759618");
        }
        session.setAttribute("telefonoRegistro", telefonoNormalizado);
        session.removeAttribute("codigoRegistroDemo");

        if (twilioVerifyService.enviarCodigo(telefonoNormalizado)) {
            return Map.of("ok", true, "modoDemo", false, "mensaje", "Codigo enviado por SMS a " + telefonoNormalizado);
        }

        String codigoDemo = twilioVerifyService.codigoDemo();
        session.setAttribute("codigoRegistroDemo", codigoDemo);
        return Map.of("ok", true, "modoDemo", true, "codigoDemo", codigoDemo,
                "mensaje", "No se pudo usar Twilio en este momento. Se activo codigo demo academico.");
    }

    @PostMapping("/registro/enviar")
    public String enviarCodigo(@RequestParam String nombres, @RequestParam(required = false) String apellidos,
                               @RequestParam String telefono, @RequestParam(required = false) String direccion, @RequestParam String username,
                               @RequestParam String email, @RequestParam String password, @RequestParam String codigo,
                               HttpSession session, RedirectAttributes redirect) {
        String usernameLimpio = username == null ? "" : username.trim();
        String emailLimpio = email == null ? "" : email.trim();
        if (usuarioRepository.existsByUsernameIgnoreCase(usernameLimpio)
                || usuarioRepository.existsByEmailIgnoreCase(emailLimpio)) {
            redirect.addFlashAttribute("error", "El usuario o correo ya existe");
            return "redirect:/registro";
        }

        String telefonoNormalizado = twilioVerifyService.normalizarTelefonoPeru(telefono);
        if (!codigoRegistroValido(session, telefonoNormalizado, codigo)) {
            redirect.addFlashAttribute("error", "Codigo incorrecto. Revisa el SMS o solicita uno nuevo.");
            return "redirect:/registro";
        }

        RegistroTemporal registro = new RegistroTemporal();
        registro.setNombres(nombres);
        registro.setApellidos(apellidos);
        registro.setTelefono(telefonoNormalizado);
        registro.setDireccion(direccion);
        registro.setUsername(usernameLimpio);
        registro.setEmail(emailLimpio);
        registro.setPassword(password);
        registro.setCodigo(codigo);
        session.setAttribute("registroTemporal", registro);
        return crearCuentaVerificada(session, redirect, true);
    }

    @PostMapping("/registro/verificar")
    public String verificarRegistro(@RequestParam String codigo, HttpSession session, RedirectAttributes redirect) {
        RegistroTemporal registro = (RegistroTemporal) session.getAttribute("registroTemporal");
        if (registro == null) {
            redirect.addFlashAttribute("error", "Primero completa el registro");
            return "redirect:/registro";
        }
        if (!registro.getCodigo().equals(codigo)) {
            redirect.addFlashAttribute("error", "Codigo incorrecto");
            return "redirect:/registro";
        }

        return crearCuentaVerificada(session, redirect, true);
    }

    private String crearCuentaVerificada(HttpSession session, RedirectAttributes redirect, boolean telefonoVerificado) {
        RegistroTemporal registro = (RegistroTemporal) session.getAttribute("registroTemporal");
        if (registro == null) {
            redirect.addFlashAttribute("error", "Primero completa el registro");
            return "redirect:/registro";
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(registro.getUsername());
        usuario.setEmail(registro.getEmail());
        usuario.setPasswordhash(passwordService.encriptar(registro.getPassword()));
        usuario.setActivo(true);
        usuario.setFechacreacion(LocalDateTime.now());
        usuario.setProveedorauth("LOCAL");
        Rol clienteRol = rolRepository.findByNombre("CLIENTE");
        if (clienteRol != null) {
            usuario.getRoles().add(clienteRol);
        }
        usuarioRepository.save(usuario);

        Cliente cliente = new Cliente();
        cliente.setUsuario(usuario);
        cliente.setNombres(registro.getNombres());
        cliente.setApellidos(registro.getApellidos());
        cliente.setTelefono(registro.getTelefono());
        cliente.setDireccion(registro.getDireccion());
        cliente.setTelefonoverificado(telefonoVerificado);
        cliente.setEmailfacturacion(registro.getEmail());
        cliente.setFecharegistro(LocalDateTime.now());
        clienteRepository.save(cliente);

        session.removeAttribute("registroTemporal");
        session.removeAttribute("telefonoRegistro");
        session.removeAttribute("codigoRegistroDemo");
        redirect.addFlashAttribute("mensajeExito", "Cuenta verificada. Ahora inicia sesion");
        return "redirect:/login";
    }

    private boolean codigoRegistroValido(HttpSession session, String telefono, String codigo) {
        Object telefonoSesion = session.getAttribute("telefonoRegistro");
        if (telefonoSesion == null || !telefonoSesion.equals(telefono)) {
            return false;
        }
        if (twilioVerifyService.configurado()) {
            boolean verificado = twilioVerifyService.verificarCodigo(telefono, codigo);
            if (verificado) {
                return true;
            }
        }
        Object codigoDemo = session.getAttribute("codigoRegistroDemo");
        return codigoDemo != null && codigoDemo.equals(codigo);
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        if (session.getAttribute("usuarioActual") == null) {
            return "redirect:/login";
        }

        Set<String> roles = rolesSesion(session);

        if (!roles.contains("ADMIN")) {

            if (roles.contains("EMPLEADO")) {
                return "redirect:/empleado";
            }
            return "redirect:/";
        }

        model.addAttribute("totalProductos", productoRepository.count());
        model.addAttribute("stockBajo", inventarioService.productosConStockBajo());

        return "dashboard";
    }

    private Set<String> nombresRoles(Usuario usuario) {
        Set<String> roles = new HashSet<>();
        usuario.getRoles().forEach(rol -> roles.add(rol.getNombre()));
        return roles;
    }

    private String destinoPorRol(Set<String> roles) {

        if (roles.contains("ADMIN")) {
            return "redirect:/dashboard";
        }

        if (roles.contains("EMPLEADO")) {
            return "redirect:/empleado";
        }
        return "redirect:/";
    }

    @SuppressWarnings("unchecked")
    private Set<String> rolesSesion(HttpSession session) {
        Object roles = session.getAttribute("roles");
        return roles instanceof Set<?> ? (Set<String>) roles : Set.of();
    }
}
