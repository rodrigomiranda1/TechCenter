package com.techcenter.api.controller;

import com.techcenter.api.model.*;
import com.techcenter.api.repository.*;
import com.techcenter.api.service.IaService;
import com.techcenter.api.service.ProductoImagenService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/cliente")
@RequiredArgsConstructor
public class ClientePortalController {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final MarcaRepository marcaRepository;
    private final ClienteRepository clienteRepository;
    private final FavoritoRepository favoritoRepository;
    private final ValoracionRepository valoracionRepository;
    private final ConsultaIaRepository consultaIaRepository;
    private final UsuarioRepository usuarioRepository;
    private final IaService iaService;
    private final ProductoImagenService productoImagenService;

    @GetMapping("/catalogo")
    public String catalogo(@RequestParam(required = false) String q,
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
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("q", q);
        model.addAttribute("paginaActual", paginaActual);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("totalProductos", productosFiltrados.size());
        return "cliente/catalogo";
    }

    @PostMapping("/favoritos/agregar")
    public String agregarFavorito(@RequestParam Long idCliente, @RequestParam Long idProducto,
                                  RedirectAttributes redirect) {
        Favorito favorito = new Favorito();
        favorito.setCliente(clienteRepository.findById(idCliente).orElseThrow());
        favorito.setProducto(productoRepository.findById(idProducto).orElseThrow());
        favoritoRepository.save(favorito);
        redirect.addFlashAttribute("mensajeExito", "Producto agregado a favoritos");
        return "redirect:/cliente/catalogo";
    }

    @PostMapping("/valoraciones/registrar")
    public String registrarValoracion(@RequestParam Long idCliente, @RequestParam Long idProducto,
                                      @RequestParam Integer estrellas, @RequestParam String comentario, RedirectAttributes redirect) {
        Valoracion valoracion = new Valoracion();
        valoracion.setCliente(clienteRepository.findById(idCliente).orElseThrow());
        valoracion.setProducto(productoRepository.findById(idProducto).orElseThrow());
        valoracion.setEstrellas(estrellas);
        valoracion.setComentario(comentario);
        valoracionRepository.save(valoracion);
        redirect.addFlashAttribute("mensajeExito", "Valoracion registrada");
        return "redirect:/cliente/catalogo";
    }

    @GetMapping("/favoritos")
    public String favoritos(@RequestParam(required = false) Long idCliente, Model model) {
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("favoritos", idCliente == null ? favoritoRepository.findAll()
                : favoritoRepository.findByCliente(clienteRepository.findById(idCliente).orElseThrow()));
        return "cliente/favoritos";
    }

    @GetMapping("/ia")
    public String ia(HttpSession session, Model model) {
        Cliente cliente = clienteActual(session);
        model.addAttribute("consultas", historialIa(cliente, session));
        model.addAttribute("historialPersistente", cliente != null);
        return "cliente/ia";
    }

    @GetMapping("/perfil")
    public String perfil(HttpSession session, Model model, RedirectAttributes redirect) {
        Usuario usuario = usuarioActual(session);
        if (usuario == null) {
            redirect.addFlashAttribute("error", "Inicia sesion para ver tu perfil");
            return "redirect:/login";
        }
        Cliente cliente = clienteRepository.findByUsuario(usuario).orElseGet(() -> crearClienteBasico(usuario));
        model.addAttribute("usuario", usuario);
        model.addAttribute("cliente", cliente);
        return "cliente/perfil";
    }

    @PostMapping("/perfil")
    public String guardarPerfil(@RequestParam String nombres, @RequestParam(required = false) String apellidos,
                                @RequestParam(required = false) String telefono, @RequestParam(required = false) String direccion,
                                @RequestParam(required = false) String tipoDocumento, @RequestParam(required = false) String numeroDocumento,
                                @RequestParam(required = false) String emailFacturacion, @RequestParam(required = false) String direccionFiscal,
                                @RequestParam(required = false) String razonSocial, @RequestParam String email, HttpSession session,
                                RedirectAttributes redirect) {
        Usuario usuario = usuarioActual(session);
        if (usuario == null) {
            redirect.addFlashAttribute("error", "Inicia sesion para actualizar tu perfil");
            return "redirect:/login";
        }
        Cliente cliente = clienteRepository.findByUsuario(usuario).orElseGet(() -> crearClienteBasico(usuario));
        cliente.setNombres(nombres);
        cliente.setApellidos(apellidos);
        cliente.setTelefono(telefono);
        cliente.setDireccion(direccion);
        cliente.setTipodocumento(tipoDocumento == null || tipoDocumento.isBlank() ? "DNI" : tipoDocumento);
        cliente.setNumerodocumento(numeroDocumento);
        cliente.setEmailfacturacion(emailFacturacion);
        cliente.setDireccionfiscal(direccionFiscal);
        cliente.setRazonsocial(razonSocial);
        clienteRepository.save(cliente);
        if (!email.equalsIgnoreCase(usuario.getEmail())) {
            usuario.setEmail(email);
        }
        usuarioRepository.save(usuario);
        session.setAttribute("usuarioActual", usuario);
        redirect.addFlashAttribute("mensajeExito", "Perfil actualizado correctamente");
        return "redirect:/cliente/perfil";
    }

    @PostMapping("/perfil/foto")
    public String guardarFoto(@RequestParam MultipartFile foto, HttpSession session, RedirectAttributes redirect) {
        Usuario usuario = usuarioActual(session);
        if (usuario == null) {
            redirect.addFlashAttribute("error", "Inicia sesion para actualizar tu foto");
            return "redirect:/login";
        }
        try {
            if (foto == null || foto.isEmpty()) {
                throw new IllegalArgumentException("Selecciona una imagen");
            }
            if (foto.getSize() > 2 * 1024 * 1024) {
                throw new IllegalArgumentException("La imagen no debe superar 2 MB");
            }
            String contentType = foto.getContentType() == null ? "" : foto.getContentType().toLowerCase(Locale.ROOT);
            String extension = switch (contentType) {
                case "image/jpeg" -> ".jpg";
                case "image/png" -> ".png";
                case "image/webp" -> ".webp";
                default -> throw new IllegalArgumentException("Solo se permiten imagenes JPG, PNG o WebP");
            };
            Path directory = Path.of("uploads", "perfiles");
            Files.createDirectories(directory);
            String filename = "perfil-" + usuario.getIdusuario() + "-" + System.currentTimeMillis() + extension;
            Files.copy(foto.getInputStream(), directory.resolve(filename));
            usuario.setFotoperfil("/uploads/perfiles/" + filename);
            usuarioRepository.save(usuario);
            session.setAttribute("usuarioActual", usuario);
            redirect.addFlashAttribute("mensajeExito", "Foto de perfil actualizada");
        } catch (IllegalArgumentException | IOException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/cliente/perfil";
    }

    @PostMapping("/ia/preguntar")
    public String preguntar(@RequestParam String pregunta, HttpSession session, Model model) {
        Cliente cliente = clienteActual(session);
        ConsultaIa consulta = iaService.responder(cliente, pregunta, cliente != null);
        if (cliente == null) {
            agregarConsultaTemporal(session, consulta);
        }
        model.addAttribute("consultas", historialIa(cliente, session));
        model.addAttribute("historialPersistente", cliente != null);
        model.addAttribute("pregunta", consulta.getPregunta());
        model.addAttribute("respuesta", consulta.getRespuesta());
        return "cliente/ia";
    }

    private Cliente clienteActual(HttpSession session) {
        Usuario usuarioActual = usuarioActual(session);
        if (usuarioActual != null) {
            return clienteRepository.findByUsuario(usuarioActual).orElse(null);
        }
        return null;
    }

    private Usuario usuarioActual(HttpSession session) {
        Object usuario = session.getAttribute("usuarioActual");
        return usuario instanceof Usuario usuarioActual ? usuarioActual : null;
    }

    private Cliente crearClienteBasico(Usuario usuario) {
        Cliente cliente = new Cliente();
        cliente.setUsuario(usuario);
        cliente.setNombres(usuario.getUsername());
        cliente.setEmailfacturacion(usuario.getEmail());
        cliente.setFecharegistro(LocalDateTime.now());
        return clienteRepository.save(cliente);
    }

    private List<ConsultaIa> historialIa(Cliente cliente, HttpSession session) {
        if (cliente != null) {
            return consultaIaRepository.findByClienteOrderByFechaconsultaDesc(cliente);
        }
        return consultasTemporales(session);
    }

    @SuppressWarnings("unchecked")
    private List<ConsultaIa> consultasTemporales(HttpSession session) {
        Object historial = session.getAttribute("historialIaTemporal");
        if (historial instanceof List<?>) {
            return (List<ConsultaIa>) historial;
        }
        List<ConsultaIa> nuevoHistorial = new ArrayList<>();
        session.setAttribute("historialIaTemporal", nuevoHistorial);
        return nuevoHistorial;
    }

    private void agregarConsultaTemporal(HttpSession session, ConsultaIa consulta) {
        List<ConsultaIa> historial = consultasTemporales(session);
        historial.add(0, consulta);
        if (historial.size() > 8) {
            historial.remove(historial.size() - 1);
        }
    }
}
