package com.techcenter.api.service;

import com.techcenter.api.model.Producto;
import com.techcenter.api.model.ProductoImagen;
import com.techcenter.api.repository.ProductoImagenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductoImagenService {

    private static final String IMAGEN_DEFAULT = "/img/products/producto-default.svg";

    private final ProductoImagenRepository productoImagenRepository;

    public Map<Long, String> imagenPrincipalPorProducto(List<Producto> productos) {
        Map<Long, String> imagenes = new HashMap<>();
        for (Producto producto : productos) {
            imagenes.put(producto.getIdproducto(), imagenPrincipal(producto));
        }
        return imagenes;
    }

    public String imagenPrincipal(Producto producto) {
        if (producto == null || producto.getIdproducto() == null) {
            return IMAGEN_DEFAULT;
        }
        return productoImagenRepository.findFirstByProductoOrderByIdimagenAsc(producto)
                .map(ProductoImagen::getUrlimagen).filter(url -> !url.isBlank()).orElse(IMAGEN_DEFAULT);
    }

    @Transactional
    public void guardarImagenPrincipal(Producto producto, String urlImagen) {
        if (producto == null || urlImagen == null || urlImagen.isBlank()) {
            return;
        }
        ProductoImagen imagen = productoImagenRepository.findFirstByProductoOrderByIdimagenAsc(producto)
                .orElseGet(ProductoImagen::new);
        imagen.setProducto(producto);
        imagen.setUrlimagen(urlImagen.trim());
        productoImagenRepository.save(imagen);
    }

    @Transactional
    public void eliminarImagenes(Producto producto) {
        if (producto == null) {
            return;
        }
        productoImagenRepository.deleteAll(productoImagenRepository.findByProducto(producto));
    }
}
