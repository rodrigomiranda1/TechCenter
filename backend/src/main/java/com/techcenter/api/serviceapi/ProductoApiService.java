package com.techcenter.api.serviceapi;

import com.techcenter.api.dto.ProductoDTO;
import com.techcenter.api.model.Producto;
import com.techcenter.api.repository.CategoriaRepository;
import com.techcenter.api.repository.MarcaRepository;
import com.techcenter.api.repository.ProductoRepository;
import com.techcenter.api.service.ProductoImagenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoApiService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final MarcaRepository marcaRepository;
    private final ProductoImagenService productoImagenService;

    public List<ProductoDTO> listar() {

        return productoRepository.findAll()
                .stream()
                .map(this::convertirDTO)
                .toList();
    }

    public ProductoDTO buscarPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Producto no encontrado"));

        return convertirDTO(producto);
    }

    public void registrarProducto(ProductoDTO dto) {

        Producto producto = new Producto();

        producto.setCodigo(dto.getCodigo());
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStockactual(dto.getStockActual());
        producto.setStockminimo(dto.getStockMinimo());
        producto.setActivo(dto.getActivo());

        if (dto.getIdCategoria() != null) {
            producto.setCategoria(categoriaRepository.findById(dto.getIdCategoria()).orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada")));
        }

        if (dto.getIdMarca() != null) {
            producto.setMarca(marcaRepository.findById(dto.getIdMarca()).orElseThrow(() -> new IllegalArgumentException("Marca no encontrada"))
            );
        }
        productoRepository.save(producto);
    }

    public void actualizarProducto(ProductoDTO dto) {

        Producto producto = productoRepository.findById(dto.getIdProducto()).orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        producto.setCodigo(dto.getCodigo());
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStockactual(dto.getStockActual());
        producto.setStockminimo(dto.getStockMinimo());
        producto.setActivo(dto.getActivo());

        if (dto.getIdCategoria() != null) {
            producto.setCategoria(categoriaRepository.findById(dto.getIdCategoria()).orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"))
            );
        }

        if (dto.getIdMarca() != null) {
            producto.setMarca(marcaRepository.findById(dto.getIdMarca()).orElseThrow(() -> new IllegalArgumentException("Marca no encontrada")));
        }
        productoRepository.save(producto);
    }

    @Transactional
    public void eliminarProducto(Long id) {

        int filasActualizadas = productoRepository.desactivarProducto(id);

        if (filasActualizadas == 0) {
            throw new IllegalArgumentException("Producto no encontrado");
        }
    }

    private ProductoDTO convertirDTO(Producto producto) {

        ProductoDTO dto = new ProductoDTO();

        dto.setIdProducto(producto.getIdproducto());
        dto.setCodigo(producto.getCodigo());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setStockActual(producto.getStockactual());
        dto.setStockMinimo(producto.getStockminimo());
        dto.setActivo(producto.getActivo());

        if (producto.getCategoria() != null) {
            dto.setIdCategoria(producto.getCategoria().getIdcategoria());
            dto.setNombreCategoria(producto.getCategoria().getNombre());
        }

        if (producto.getMarca() != null) {
            dto.setIdMarca(producto.getMarca().getIdmarca());
            dto.setNombreMarca(producto.getMarca().getNombre());
        }

        dto.setImagenUrl(productoImagenService.imagenPrincipal(producto));
        return dto;
    }

}
