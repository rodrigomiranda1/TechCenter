package com.techcenter.api.service;

import com.techcenter.api.model.MovimientoInventario;
import com.techcenter.api.model.Producto;
import com.techcenter.api.repository.MovimientoInventarioRepository;
import com.techcenter.api.repository.ProductoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventarioService {

    private final ProductoRepository productoRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;
    private final AuditoriaService auditoriaService;

    @Transactional
    public void registrarMovimiento(Long idProducto, String tipo, Integer cantidad, String observacion) {
        Producto producto = productoRepository.findById(idProducto).orElseThrow();
        int stock = producto.getStockactual() == null ? 0 : producto.getStockactual();
        int valor = cantidad == null ? 0 : cantidad;

        if ("ENTRADA".equals(tipo) || "DEVOLUCION".equals(tipo)) {
            producto.setStockactual(stock + valor);
        } else if ("SALIDA".equals(tipo)) {
            if (stock < valor) {
                throw new IllegalArgumentException("Stock insuficiente para registrar la salida");
            }
            producto.setStockactual(stock - valor);
        } else if ("AJUSTE".equals(tipo)) {
            producto.setStockactual(valor);
        }

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(producto);
        movimiento.setTipomovimiento(tipo);
        movimiento.setCantidad(valor);
        movimiento.setObservacion(observacion);
        movimiento.setFechamovimiento(LocalDateTime.now());

        productoRepository.save(producto);
        movimientoInventarioRepository.save(movimiento);
        auditoriaService.registrar("Movimiento de inventario " + tipo, "Producto " + producto.getNombre());
    }

    public List<Producto> productosConStockBajo() {
        return productoRepository.findAll().stream().filter(p -> p.getStockactual() != null
                && p.getStockminimo() != null && p.getStockactual() <= p.getStockminimo()).toList();
    }
}
