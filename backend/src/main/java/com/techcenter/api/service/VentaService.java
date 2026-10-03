package com.techcenter.api.service;

import com.techcenter.api.model.*;
import com.techcenter.api.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VentaService {

    private static final BigDecimal IGV = new BigDecimal("0.18");

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final InventarioService inventarioService;

    @Transactional
    public void registrar(Long idCliente,Long idUsuario,String tipoComprobante,String serie,Integer correlativo,String tipoEntrega,String observacion,BigDecimal subtotal,
                          BigDecimal igv, BigDecimal total, String metodoPago,String detalleJson) throws Exception {

        Cliente cliente = clienteRepository.findById(idCliente).orElseThrow();
        Usuario usuario = usuarioRepository.findById(idUsuario).orElseThrow();

        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setUsuario(usuario);
        venta.setFechaventa(LocalDateTime.now());
        venta.setTipocomprobante(tipoComprobante);
        venta.setSerie(serie);
        venta.setCorrelativo(correlativo);
        venta.setTipoentrega(tipoEntrega);
        venta.setEstado("PENDIENTE");
        venta.setObservacion(observacion);
        venta.setMetodopago(metodoPago);
        venta.setSubtotal(subtotal);
        venta.setIgv(igv);
        venta.setTotal(total);
        ventaRepository.save(venta);

        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> productos =
                mapper.readValue(detalleJson, new TypeReference<List<Map<String, Object>>>() {});

        for (Map<String, Object> item : productos) {
            Long idProducto = Long.valueOf(item.get("id").toString());
            Integer cantidad = Integer.valueOf(item.get("cantidad").toString());
            BigDecimal precio = new BigDecimal(item.get("precio").toString());
            Producto producto = productoRepository.findById(idProducto).orElseThrow();

            if (producto.getStockactual() < cantidad) {
                throw new RuntimeException("Stock insuficiente de " + producto.getNombre());
            }

            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProducto(producto);
            detalle.setCantidad(cantidad);
            detalle.setPreciounitario(precio);
            detalleVentaRepository.save(detalle);

            producto.setStockactual(producto.getStockactual() - cantidad);
            productoRepository.save(producto);

            inventarioService.registrarMovimiento(producto.getIdproducto(),"SALIDA",cantidad,"Venta " + venta.getSerie() + "-"
                    + String.format("%08d", venta.getCorrelativo())
            );
        }
    }

    public String generarCorrelativo(String serie){

        Integer ultimo = ventaRepository.obtenerUltimoNumero(serie);

        if(ultimo==null){

            ultimo=0;

        }

        ultimo++;

        return String.format("%08d", ultimo);

    }

    public Page<Venta> listarPaginado(int pagina){

        Pageable pageable = PageRequest.of(pagina, 7);

        return ventaRepository.findAll(pageable);

    }

}
