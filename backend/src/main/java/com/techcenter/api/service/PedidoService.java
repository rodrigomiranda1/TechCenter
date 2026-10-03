package com.techcenter.api.service;

import com.techcenter.api.model.*;
import com.techcenter.api.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class PedidoService {

    private static final BigDecimal IGV = new BigDecimal("0.18");

    private final PedidoRepository pedidoRepository;
    private final PedidoDetalleRepository pedidoDetalleRepository;
    private final PagoRepository pagoRepository;
    private final ComprobanteRepository comprobanteRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    private final InventarioService inventarioService;
    private final String serieBoleta;
    private final String serieFactura;

    public PedidoService(PedidoRepository pedidoRepository, PedidoDetalleRepository pedidoDetalleRepository,
                         PagoRepository pagoRepository, ComprobanteRepository comprobanteRepository,
                         ProductoRepository productoRepository, ClienteRepository clienteRepository,
                         InventarioService inventarioService,
                         @Value("${techcenter.comprobantes.serie-boleta:B001}") String serieBoleta,
                         @Value("${techcenter.comprobantes.serie-factura:F001}") String serieFactura) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoDetalleRepository = pedidoDetalleRepository;
        this.pagoRepository = pagoRepository;
        this.comprobanteRepository = comprobanteRepository;
        this.productoRepository = productoRepository;
        this.clienteRepository = clienteRepository;
        this.inventarioService = inventarioService;
        this.serieBoleta = serieBoleta;
        this.serieFactura = serieFactura;
    }

    @Transactional
    public Pedido confirmarPedido(Usuario usuario, Map<Long, Integer> carrito, String direccionEntrega,
                                  String tipoComprobante, String metodoPago) {
        if (usuario == null) {
            throw new IllegalArgumentException("Inicia sesion para comprar");
        }
        if (carrito == null || carrito.isEmpty()) {
            throw new IllegalArgumentException("El carrito esta vacio");
        }

        Cliente cliente = clienteRepository.findByUsuario(usuario)
                .orElseThrow(() -> new IllegalArgumentException("No existe cliente asociado al usuario"));

        BigDecimal subtotal = BigDecimal.ZERO;
        for (var entry : carrito.entrySet()) {
            Producto producto = productoRepository.findById(entry.getKey()).orElseThrow();
            int cantidad = Math.max(1, entry.getValue());
            if (producto.getStockactual() == null || producto.getStockactual() < cantidad) {
                throw new IllegalArgumentException("Stock insuficiente para " + producto.getNombre());
            }
            subtotal = subtotal.add(producto.getPrecio().multiply(BigDecimal.valueOf(cantidad)));
        }
        subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);
        BigDecimal igv = subtotal.multiply(IGV).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(igv).setScale(2, RoundingMode.HALF_UP);

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setUsuario(usuario);
        pedido.setFechapedido(LocalDateTime.now());
        pedido.setEstado("PAGADO");
        pedido.setSubtotal(subtotal);
        pedido.setIgv(igv);
        pedido.setTotal(total);
        pedido.setDireccionentrega(direccionEntrega);
        pedido.setObservacion("Pago sandbox Mercado Pago - " + etiquetaMetodoPago(metodoPago));
        pedidoRepository.save(pedido);

        for (var entry : carrito.entrySet()) {
            Producto producto = productoRepository.findById(entry.getKey()).orElseThrow();
            int cantidad = Math.max(1, entry.getValue());
            PedidosDetalle detalle = new PedidosDetalle();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setCantidad(cantidad);
            detalle.setPreciounitario(producto.getPrecio());
            detalle.setTotallinea(producto.getPrecio().multiply(BigDecimal.valueOf(cantidad)).setScale(2, RoundingMode.HALF_UP));
            pedidoDetalleRepository.save(detalle);
            inventarioService.registrarMovimiento(producto.getIdproducto(), "SALIDA", cantidad,
                    "Pedido web #" + pedido.getIdpedido());
        }

        Pago pago = new Pago();
        pago.setPedido(pedido);
        pago.setPasarela("MERCADO_PAGO_SANDBOX");
        pago.setCodigooperacion("MP-SBX-" + pedido.getIdpedido() + "-" + System.currentTimeMillis());
        pago.setEstado("APROBADO");
        pago.setMoneda("PEN");
        pago.setMonto(total);
        pago.setFechapago(LocalDateTime.now());
        pago.setRespuestapasarela("Metodo: " + etiquetaMetodoPago(metodoPago)
                + ". Pago simulado con credenciales sandbox.");
        pagoRepository.save(pago);

        crearComprobante(pedido, tipoComprobante);
        return pedido;
    }

    private String etiquetaMetodoPago(String metodoPago) {
        return switch (metodoPago == null ? "" : metodoPago) {
            case "TARJETA_DEMO" -> "Tarjeta demo Mercado Pago";
            case "YAPE_DEMO" -> "Yape/QR demo";
            case "EFECTIVO_DEMO" -> "Pago efectivo demo";
            default -> "Tarjeta demo Mercado Pago";
        };
    }

    public Comprobante crearComprobante(Pedido pedido, String tipoComprobante) {
        String tipo = "FACTURA".equalsIgnoreCase(tipoComprobante) ? "FACTURA" : "BOLETA";
        String serie = "FACTURA".equals(tipo) ? serieFactura : serieBoleta;
        int correlativo = comprobanteRepository.findTopByTipoAndSerieOrderByCorrelativoDesc(tipo, serie)
                .map(c -> c.getCorrelativo() + 1)
                .orElse(1);

        Comprobante comprobante = new Comprobante();
        comprobante.setPedido(pedido);
        comprobante.setTipo(tipo);
        comprobante.setSerie(serie);
        comprobante.setCorrelativo(correlativo);
        comprobante.setFechaemision(LocalDateTime.now());
        comprobante.setSubtotal(pedido.getSubtotal());
        comprobante.setIgv(pedido.getIgv());
        comprobante.setTotal(pedido.getTotal());
        comprobante.setEstado("EMITIDO");
        comprobante.setObservacion("Comprobante academico sin validez SUNAT");
        return comprobanteRepository.save(comprobante);
    }
}