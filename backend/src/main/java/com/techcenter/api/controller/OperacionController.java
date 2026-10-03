package com.techcenter.api.controller;

import com.techcenter.api.model.*;
import com.techcenter.api.repository.*;
import com.techcenter.api.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/operaciones")
@RequiredArgsConstructor
public class OperacionController {

    private final CompraService compraService;
    private final VentaService ventaService;
    private final InventarioService inventarioService;
    private final CompraRepository compraRepository;
    private final DetalleCompraRepository detalleCompraRepository;
    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final ProveedorRepository proveedorRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoImagenService productoImagenService;
    private final CategoriaRepository categoriaRepository;
    private final MarcaRepository marcaRepository;
    private final AuditoriaService auditoriaService;
    private final PedidoRepository pedidoRepository;

    @GetMapping("/catalogo")
    public String catalogo(@RequestParam(defaultValue = "1") int pagina, Model model) {
        List<Producto> productosTodos = productoRepository.findAll().stream()
                .sorted(Comparator.comparing(Producto::getIdproducto).reversed())
                .toList();

        int tamanoPagina = 15;
        int totalPaginas = Math.max(1, (int) Math.ceil(productosTodos.size() / (double) tamanoPagina));

        int paginaActual = Math.max(1, Math.min(pagina, totalPaginas));

        int desde = (paginaActual - 1) * tamanoPagina;
        int hasta = Math.min(desde + tamanoPagina, productosTodos.size());

        List<Producto> productos = productosTodos.subList(desde, hasta);

        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("marcas", marcaRepository.findAll());
        model.addAttribute("categoriasActivas", categoriaRepository.findAll().stream().filter(c -> !Boolean.FALSE.equals(c.getActivo()))
                .toList());

        model.addAttribute("marcasActivas", marcaRepository.findAll().stream().filter(m -> !Boolean.FALSE.equals(m.getActivo()))
                .toList());

        model.addAttribute("productos", productos);
        model.addAttribute("imagenes", productoImagenService.imagenPrincipalPorProducto(productos));
        model.addAttribute("producto", new Producto());
        model.addAttribute("paginaActual", paginaActual);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("totalProductosAdmin", productosTodos.size());

        return "operaciones/catalogo";
    }

    @PostMapping("/categorias/guardar")
    public String guardarCategoria(Categoria categoria, RedirectAttributes redirect) {
        if (categoria.getActivo() == null) {
            categoria.setActivo(true);
        }
        categoriaRepository.save(categoria);
        auditoriaService.registrar("Guardo categoria", "Categoria");
        redirect.addFlashAttribute("mensajeExito", "Categoria guardada correctamente");
        return "redirect:/operaciones/catalogo";
    }

    @GetMapping("/categorias/activar/{id}")
    public String activarCategoria(@PathVariable Long id, RedirectAttributes redirect) {

        Categoria categoria = categoriaRepository.findById(id).orElseThrow();

        categoria.setActivo(true);
        categoriaRepository.save(categoria);
        auditoriaService.registrar("Activo categoria", categoria.getNombre());
        redirect.addFlashAttribute("mensajeExito","Categoria activada correctamente");

        return "redirect:/operaciones/catalogo";
    }

    @GetMapping("/categorias/desactivar/{id}")
    public String desactivarCategoria(@PathVariable Long id, RedirectAttributes redirect) {

        Categoria categoria = categoriaRepository.findById(id).orElseThrow();

        categoria.setActivo(false);
        categoriaRepository.save(categoria);
        auditoriaService.registrar("Desactivo categoria", categoria.getNombre());
        redirect.addFlashAttribute("mensajeExito","Categoria desactivada correctamente");

        return "redirect:/operaciones/catalogo";
    }

    @PostMapping("/marcas/guardar")
    public String guardarMarca(Marca marca, RedirectAttributes redirect) {
        if (marca.getActivo() == null) {
            marca.setActivo(true);
        }
        marcaRepository.save(marca);
        auditoriaService.registrar("Guardo marca", "Marca");
        redirect.addFlashAttribute("mensajeExito", "Marca guardada correctamente");
        return "redirect:/operaciones/catalogo";
    }

    @GetMapping("/marcas/activar/{id}")
    public String activarMarca(@PathVariable Long id, RedirectAttributes redirect) {

        Marca marca = marcaRepository.findById(id).orElseThrow();

        marca.setActivo(true);
        marcaRepository.save(marca);
        auditoriaService.registrar("Activo marca", marca.getNombre());
        redirect.addFlashAttribute("mensajeExito","Marca activada correctamente");

        return "redirect:/operaciones/catalogo";
    }

    @GetMapping("/marcas/desactivar/{id}")
    public String desactivarMarca(@PathVariable Long id, RedirectAttributes redirect) {

        Marca marca = marcaRepository.findById(id).orElseThrow();

        marca.setActivo(false);
        marcaRepository.save(marca);
        auditoriaService.registrar("Desactivo marca", marca.getNombre());
        redirect.addFlashAttribute("mensajeExito","Marca desactivada correctamente");

        return "redirect:/operaciones/catalogo";
    }

    @GetMapping("/marcas/eliminar/{id}")
    public String eliminarMarca(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            Marca marca = marcaRepository.findById(id).orElseThrow();
            String nombre = marca.getNombre();
            marcaRepository.delete(marca);
            auditoriaService.registrar("Elimino marca", nombre);
            redirect.addFlashAttribute("mensajeExito", "Marca eliminada correctamente");
        } catch (Exception ex) {
            redirect.addFlashAttribute("error", "No se puede eliminar la marca porque esta asociada a productos");
        }
        return "redirect:/operaciones/catalogo";
    }

    @PostMapping("/productos/guardar")
    public String guardarProducto(@RequestParam(required = false) Long idProducto, @RequestParam String codigo,
                                  @RequestParam String nombre, @RequestParam(required = false) String descripcion,
                                  @RequestParam BigDecimal precio, @RequestParam Integer stockActual, @RequestParam Integer stockMinimo,
                                  @RequestParam Long idCategoria, @RequestParam Long idMarca,
                                  @RequestParam(required = false) String urlImagen, RedirectAttributes redirect) {
        Producto producto = idProducto == null ? new Producto()
                : productoRepository.findById(idProducto).orElse(new Producto());
        producto.setCodigo(codigo);
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        producto.setStockactual(stockActual);
        producto.setStockminimo(stockMinimo);
        producto.setCategoria(categoriaRepository.findById(idCategoria).orElse(null));
        producto.setMarca(marcaRepository.findById(idMarca).orElse(null));
        producto.setActivo(true);
        productoRepository.save(producto);
        productoImagenService.guardarImagenPrincipal(producto, urlImagen);
        auditoriaService.registrar("Guardo producto", producto.getNombre());
        redirect.addFlashAttribute("mensajeExito", "Producto guardado correctamente");
        return "redirect:/operaciones/catalogo";
    }

    @GetMapping("/productos/desactivar/{id}")
    public String desactivarProducto(@PathVariable Long id, RedirectAttributes redirect) {
        Producto producto = productoRepository.findById(id).orElseThrow();
        producto.setActivo(false);
        productoRepository.save(producto);
        auditoriaService.registrar("Desactivo producto", producto.getNombre());
        redirect.addFlashAttribute("mensajeExito", "Producto desactivado correctamente");
        return "redirect:/operaciones/catalogo";
    }

    @GetMapping("/productos/activar/{id}")
    public String activarProducto(@PathVariable Long id, RedirectAttributes redirect) {
        Producto producto = productoRepository.findById(id).orElseThrow();
        producto.setActivo(true);
        productoRepository.save(producto);
        auditoriaService.registrar("Activo producto", producto.getNombre());
        redirect.addFlashAttribute("mensajeExito", "Producto activado correctamente");
        return "redirect:/operaciones/catalogo";
    }

    @GetMapping("/productos/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            Producto producto = productoRepository.findById(id).orElseThrow();
            String nombre = producto.getNombre();
            productoImagenService.eliminarImagenes(producto);
            productoRepository.delete(producto);
            auditoriaService.registrar("Elimino producto", nombre);
            redirect.addFlashAttribute("mensajeExito", "Producto eliminado correctamente");
        } catch (Exception ex) {
            redirect.addFlashAttribute("error", "No se puede eliminar el producto porque tiene movimientos asociados. Puedes desactivarlo.");
        }
        return "redirect:/operaciones/catalogo";
    }


    @GetMapping("/compras")
    public String compras(Model model) {
        model.addAttribute("compras", compraRepository.findAll());
        model.addAttribute("detalles", detalleCompraRepository.findAll());
        model.addAttribute("proveedores", proveedorRepository.findAll());
        model.addAttribute("productos", productoRepository.findByActivoTrue());
        return "operaciones/compras";
    }

    @PostMapping("/compras/registrar")
    public String registrarCompra(@RequestParam Long idProveedor, @RequestParam Long idProducto,
                                  @RequestParam Integer cantidad, @RequestParam BigDecimal costoUnitario, RedirectAttributes redirect) {
        compraService.registrar(idProveedor, idProducto, cantidad, costoUnitario);
        redirect.addFlashAttribute("mensajeExito", "Compra registrada y stock actualizado");
        return "redirect:/operaciones/compras";
    }

    @GetMapping("/reportes")
    public String reportes(
            @RequestParam(required = false) LocalDate ventasDesde,
            @RequestParam(required = false) LocalDate ventasHasta,
            @RequestParam(required = false) LocalDate pedidosDesde,
            @RequestParam(required = false) LocalDate pedidosHasta,
            Model model) {

        model.addAttribute("totalProductos", productoRepository.count());
        model.addAttribute("totalClientes", clienteRepository.count());
        model.addAttribute("totalProveedores", proveedorRepository.count());
        model.addAttribute("totalUsuarios", usuarioRepository.count());

        List<Venta> ventas = ventaRepository.findAll().stream().filter(v -> v.getFechaventa() != null)
                .filter(v -> {

                    if (ventasDesde != null) {
                        LocalDateTime inicio = ventasDesde.atStartOfDay();

                        if (v.getFechaventa().isBefore(inicio)) {
                            return false;
                        }
                    }

                    if (ventasHasta != null) {
                        LocalDateTime fin = ventasHasta.plusDays(1).atStartOfDay();

                        if (!v.getFechaventa().isBefore(fin)) {
                            return false;
                        }
                    }
                    return true;
                })
                .sorted(Comparator.comparing(Venta::getFechaventa,Comparator.reverseOrder())).toList();

        long cantidadVentas = ventas.size();

        BigDecimal totalVentas = ventas.stream().map(Venta::getTotal).filter(total -> total != null).reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("ventas", ventas);
        model.addAttribute("cantidadVentas", cantidadVentas);
        model.addAttribute("totalVentas", totalVentas);

        List<Pedido> pedidos = pedidoRepository.findAll().stream().filter(p -> p.getFechapedido() != null)
                .filter(p -> {

                    if (pedidosDesde != null) {
                        LocalDateTime inicio = pedidosDesde.atStartOfDay();

                        if (p.getFechapedido().isBefore(inicio)) {
                            return false;
                        }
                    }

                    if (pedidosHasta != null) {
                        LocalDateTime fin = pedidosHasta.plusDays(1).atStartOfDay();

                        if (!p.getFechapedido().isBefore(fin)) {
                            return false;
                        }
                    }
                    return true;
                })
                .sorted(Comparator.comparing(Pedido::getFechapedido, Comparator.reverseOrder())).toList();

        long cantidadPedidos = pedidos.size();

        long pedidosPagados = pedidos.stream().filter(p -> "PAGADO".equalsIgnoreCase(p.getEstado())).count();

        long pedidosPendientes = pedidos.stream().filter(p -> "PENDIENTE".equalsIgnoreCase(p.getEstado())).count();

        BigDecimal totalPedidos = pedidos.stream().map(Pedido::getTotal).filter(total -> total != null).reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("pedidos", pedidos);
        model.addAttribute("cantidadPedidos", cantidadPedidos);
        model.addAttribute("pedidosPagados", pedidosPagados);
        model.addAttribute("pedidosPendientes", pedidosPendientes);
        model.addAttribute("totalPedidos", totalPedidos);

        model.addAttribute("ventasDesde", ventasDesde);
        model.addAttribute("ventasHasta", ventasHasta);

        model.addAttribute("pedidosDesde", pedidosDesde);
        model.addAttribute("pedidosHasta", pedidosHasta);

        return "operaciones/reportes";
    }
}
