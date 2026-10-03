package com.techcenter.api.controllerapi;

import com.techcenter.api.dto.CategoriaDTO;
import com.techcenter.api.dto.MarcaDTO;
import com.techcenter.api.dto.ProductoAdminDTO;
import com.techcenter.api.model.Categoria;
import com.techcenter.api.model.Marca;
import com.techcenter.api.model.Producto;
import com.techcenter.api.repository.CategoriaRepository;
import com.techcenter.api.repository.MarcaRepository;
import com.techcenter.api.repository.ProductoRepository;
import com.techcenter.api.service.AuditoriaService;
import com.techcenter.api.service.ProductoImagenService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/operaciones")
@RequiredArgsConstructor
public class CatalogoAdminRestController {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final MarcaRepository marcaRepository;
    private final ProductoImagenService productoImagenService;
    private final AuditoriaService auditoriaService;

    @GetMapping("/catalogo")
    public ResponseEntity<?> catalogo(@RequestParam(defaultValue = "1") int pagina, HttpSession session) {
        if (!esAdmin(session)) return sinPermiso();

        var productosTodos = productoRepository.findAll().stream()
                .sorted((a, b) -> b.getIdproducto().compareTo(a.getIdproducto()))
                .toList();

        int tamanoPagina = 15;
        int totalPaginas = Math.max(1, (int) Math.ceil(productosTodos.size() / (double) tamanoPagina));
        int paginaActual = Math.max(1, Math.min(pagina, totalPaginas));
        int desde = (paginaActual - 1) * tamanoPagina;
        int hasta = Math.min(desde + tamanoPagina, productosTodos.size());
        var productos = productosTodos.subList(desde, hasta).stream().map(this::convertirProducto).toList();

        var categorias = categoriaRepository.findAll().stream().map(this::convertirCategoria).toList();
        var marcas = marcaRepository.findAll().stream().map(this::convertirMarca).toList();

        Map<String, Object> response = new java.util.HashMap<>();
        response.put("productos", productos);
        response.put("categorias", categorias);
        response.put("marcas", marcas);
        response.put("paginaActual", paginaActual);
        response.put("totalPaginas", totalPaginas);
        response.put("totalProductos", productosTodos.size());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/productos")
    public ResponseEntity<?> crearProducto(@RequestBody ProductoAdminDTO dto, HttpSession session) {
        if (!esAdmin(session)) return sinPermiso();
        Producto producto = new Producto();
        aplicarProducto(producto, dto);
        productoRepository.save(producto);
        productoImagenService.guardarImagenPrincipal(producto, dto.getUrlImagen());
        auditoriaService.registrar("Guardo producto", producto.getNombre());
        return new ResponseEntity<>(Map.of("mensaje", "Producto guardado correctamente"), HttpStatus.CREATED);
    }

    @PutMapping("/productos/{id}")
    public ResponseEntity<?> actualizarProducto(@PathVariable Long id, @RequestBody ProductoAdminDTO dto, HttpSession session) {
        if (!esAdmin(session)) return sinPermiso();
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto == null) return new ResponseEntity<>(Map.of("mensaje", "Producto no encontrado"), HttpStatus.NOT_FOUND);
        aplicarProducto(producto, dto);
        productoRepository.save(producto);
        productoImagenService.guardarImagenPrincipal(producto, dto.getUrlImagen());
        auditoriaService.registrar("Guardo producto", producto.getNombre());
        return ResponseEntity.ok(Map.of("mensaje", "Producto actualizado correctamente"));
    }

    @PutMapping("/productos/{id}/activar")
    public ResponseEntity<?> activarProducto(@PathVariable Long id, HttpSession session) {
        if (!esAdmin(session)) return sinPermiso();
        Producto producto = productoRepository.findById(id).orElseThrow();
        producto.setActivo(true);
        productoRepository.save(producto);
        auditoriaService.registrar("Activo producto", producto.getNombre());
        return ResponseEntity.ok(Map.of("mensaje", "Producto activado correctamente"));
    }

    @PutMapping("/productos/{id}/desactivar")
    public ResponseEntity<?> desactivarProducto(@PathVariable Long id, HttpSession session) {
        if (!esAdmin(session)) return sinPermiso();
        Producto producto = productoRepository.findById(id).orElseThrow();
        producto.setActivo(false);
        productoRepository.save(producto);
        auditoriaService.registrar("Desactivo producto", producto.getNombre());
        return ResponseEntity.ok(Map.of("mensaje", "Producto desactivado correctamente"));
    }

    @DeleteMapping("/productos/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id, HttpSession session) {
        if (!esAdmin(session)) return sinPermiso();
        try {
            Producto producto = productoRepository.findById(id).orElseThrow();
            String nombre = producto.getNombre();
            productoImagenService.eliminarImagenes(producto);
            productoRepository.delete(producto);
            auditoriaService.registrar("Elimino producto", nombre);
            return ResponseEntity.ok(Map.of("mensaje", "Producto eliminado correctamente"));
        } catch (Exception ex) {
            return new ResponseEntity<>(Map.of("mensaje", "No se puede eliminar el producto porque tiene movimientos asociados. Puedes desactivarlo."), HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/categorias")
    public ResponseEntity<?> crearCategoria(@RequestBody CategoriaDTO dto, HttpSession session) {
        if (!esAdmin(session)) return sinPermiso();
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        categoria.setActivo(true);
        categoriaRepository.save(categoria);
        auditoriaService.registrar("Guardo categoria", categoria.getNombre());
        return new ResponseEntity<>(Map.of("mensaje", "Categoría guardada correctamente"), HttpStatus.CREATED);
    }

    @PutMapping("/categorias/{id}/activar")
    public ResponseEntity<?> activarCategoria(@PathVariable Long id, HttpSession session) {
        if (!esAdmin(session)) return sinPermiso();
        Categoria categoria = categoriaRepository.findById(id).orElseThrow();
        categoria.setActivo(true);
        categoriaRepository.save(categoria);
        auditoriaService.registrar("Activo categoria", categoria.getNombre());
        return ResponseEntity.ok(Map.of("mensaje", "Categoría activada correctamente"));
    }

    @PutMapping("/categorias/{id}/desactivar")
    public ResponseEntity<?> desactivarCategoria(@PathVariable Long id, HttpSession session) {
        if (!esAdmin(session)) return sinPermiso();
        Categoria categoria = categoriaRepository.findById(id).orElseThrow();
        categoria.setActivo(false);
        categoriaRepository.save(categoria);
        auditoriaService.registrar("Desactivo categoria", categoria.getNombre());
        return ResponseEntity.ok(Map.of("mensaje", "Categoría desactivada correctamente"));
    }

    @PostMapping("/marcas")
    public ResponseEntity<?> crearMarca(@RequestBody MarcaDTO dto, HttpSession session) {
        if (!esAdmin(session)) return sinPermiso();
        Marca marca = new Marca();
        marca.setNombre(dto.getNombre());
        marca.setActivo(true);
        marcaRepository.save(marca);
        auditoriaService.registrar("Guardo marca", marca.getNombre());
        return new ResponseEntity<>(Map.of("mensaje", "Marca guardada correctamente"), HttpStatus.CREATED);
    }

    @PutMapping("/marcas/{id}/activar")
    public ResponseEntity<?> activarMarca(@PathVariable Long id, HttpSession session) {
        if (!esAdmin(session)) return sinPermiso();
        Marca marca = marcaRepository.findById(id).orElseThrow();
        marca.setActivo(true);
        marcaRepository.save(marca);
        auditoriaService.registrar("Activo marca", marca.getNombre());
        return ResponseEntity.ok(Map.of("mensaje", "Marca activada correctamente"));
    }

    @PutMapping("/marcas/{id}/desactivar")
    public ResponseEntity<?> desactivarMarca(@PathVariable Long id, HttpSession session) {
        if (!esAdmin(session)) return sinPermiso();
        Marca marca = marcaRepository.findById(id).orElseThrow();
        marca.setActivo(false);
        marcaRepository.save(marca);
        auditoriaService.registrar("Desactivo marca", marca.getNombre());
        return ResponseEntity.ok(Map.of("mensaje", "Marca desactivada correctamente"));
    }

    @DeleteMapping("/marcas/{id}")
    public ResponseEntity<?> eliminarMarca(@PathVariable Long id, HttpSession session) {
        if (!esAdmin(session)) return sinPermiso();
        try {
            Marca marca = marcaRepository.findById(id).orElseThrow();
            String nombre = marca.getNombre();
            marcaRepository.delete(marca);
            auditoriaService.registrar("Elimino marca", nombre);
            return ResponseEntity.ok(Map.of("mensaje", "Marca eliminada correctamente"));
        } catch (Exception ex) {
            return new ResponseEntity<>(Map.of("mensaje", "No se puede eliminar la marca porque está asociada a productos"), HttpStatus.CONFLICT);
        }
    }

    private void aplicarProducto(Producto producto, ProductoAdminDTO dto) {
        producto.setCodigo(dto.getCodigo());
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStockactual(dto.getStockActual());
        producto.setStockminimo(dto.getStockMinimo());
        producto.setCategoria(categoriaRepository.findById(dto.getIdCategoria()).orElse(null));
        producto.setMarca(marcaRepository.findById(dto.getIdMarca()).orElse(null));
        producto.setActivo(dto.getActivo() == null || dto.getActivo());
    }

    private ProductoAdminDTO convertirProducto(Producto p) {
        ProductoAdminDTO dto = new ProductoAdminDTO();
        dto.setIdProducto(p.getIdproducto());
        dto.setCodigo(p.getCodigo());
        dto.setNombre(p.getNombre());
        dto.setDescripcion(p.getDescripcion());
        dto.setPrecio(p.getPrecio());
        dto.setStockActual(p.getStockactual());
        dto.setStockMinimo(p.getStockminimo());
        dto.setIdCategoria(p.getCategoria() != null ? p.getCategoria().getIdcategoria() : null);
        dto.setIdMarca(p.getMarca() != null ? p.getMarca().getIdmarca() : null);
        dto.setUrlImagen(productoImagenService.imagenPrincipal(p));
        dto.setActivo(p.getActivo());
        return dto;
    }

    private CategoriaDTO convertirCategoria(Categoria c) {
        return new CategoriaDTO(c.getIdcategoria(), c.getNombre(), c.getDescripcion(), c.getActivo());
    }

    private MarcaDTO convertirMarca(Marca m) {
        return new MarcaDTO(m.getIdmarca(), m.getNombre(), m.getActivo());
    }

    @SuppressWarnings("unchecked")
    private boolean esAdmin(HttpSession session) {
        Object roles = session.getAttribute("roles");
        return roles instanceof Set<?> r && r.stream().anyMatch(rol -> "ADMIN".equals(String.valueOf(rol)));
    }

    private ResponseEntity<Map<String, Object>> sinPermiso() {
        return new ResponseEntity<>(Map.of("mensaje", "Acceso solo para administradores"), HttpStatus.FORBIDDEN);
    }
}
