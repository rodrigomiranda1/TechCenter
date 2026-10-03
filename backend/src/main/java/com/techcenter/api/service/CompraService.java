package com.techcenter.api.service;

import com.techcenter.api.model.Compra;
import com.techcenter.api.model.DetalleCompra;
import com.techcenter.api.model.Producto;
import com.techcenter.api.model.Proveedor;
import com.techcenter.api.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CompraService {

    private final CompraRepository compraRepository;
    private final DetalleCompraRepository detalleCompraRepository;
    private final ProveedorRepository proveedorRepository;
    private final ProductoRepository productoRepository;
    private final InventarioService inventarioService;


    @Transactional
    public void registrar(Long idProveedor, Long idProducto, Integer cantidad, BigDecimal costoUnitario) {
        Proveedor proveedor = proveedorRepository.findById(idProveedor).orElseThrow();
        Producto producto = productoRepository.findById(idProducto).orElseThrow();
        int unidades = cantidad == null ? 0 : cantidad;
        BigDecimal costo = costoUnitario == null ? BigDecimal.ZERO : costoUnitario;

        Compra compra = new Compra();
        compra.setProveedor(proveedor);
        compra.setFechacompra(LocalDateTime.now());
        compra.setTotal(costo.multiply(BigDecimal.valueOf(unidades)));
        compraRepository.save(compra);

        DetalleCompra detalle = new DetalleCompra();
        detalle.setCompra(compra);
        detalle.setProducto(producto);
        detalle.setCantidad(unidades);
        detalle.setCostounitario(costo);
        detalleCompraRepository.save(detalle);

        inventarioService.registrarMovimiento(producto.getIdproducto(), "ENTRADA", unidades,
                "Compra registrada al proveedor " + proveedor.getRazonsocial());
    }
}
