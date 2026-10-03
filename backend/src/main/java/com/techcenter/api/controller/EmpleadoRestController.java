package com.techcenter.api.controller;


import com.techcenter.api.dto.*;
import com.techcenter.api.model.Comprobante;
import com.techcenter.api.model.Pago;
import com.techcenter.api.model.Pedido;
import com.techcenter.api.model.Producto;
import com.techcenter.api.repository.*;
import com.techcenter.api.service.InventarioService;
import com.techcenter.api.service.ProductoImagenService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/empleado")
@RequiredArgsConstructor
public class EmpleadoRestController {

    private final VentaRepository ventaRepository;
    private final PedidoRepository pedidoRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final ProductoRepository productoRepository;
    private final InventarioService inventarioService;
    private final ProductoImagenService productoImagenService;
    private final PedidoDetalleRepository pedidoDetalleRepository;
    private final PagoRepository pagoRepository;
    private final ComprobanteRepository comprobanteRepository;

    @GetMapping("/panel")
    public ResponseEntity<?> panel(HttpSession session) {
        if (!esEmpleado(session)) {
            return new ResponseEntity<>(Map.of("mensaje", "Acceso solo para empleados"), HttpStatus.FORBIDDEN);
        }

        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        LocalDateTime finHoy = inicioHoy.plusDays(1);
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        var stockBajo = inventarioService.productosConStockBajo().stream()
                .map(this::convertirProducto)
                .toList();

        var ultimasVentas = ventaRepository.findTop5ByOrderByFechaventaDesc().stream()
                .map(v -> new VentaReporteDTO(
                        v.getIdventa(),
                        v.getFechaventa() != null ? v.getFechaventa().format(formato) : "",
                        v.getCliente() != null ? v.getCliente().getNombres() : "Cliente General",
                        v.getUsuario() != null ? v.getUsuario().getUsername() : "Sistema",
                        v.getTipocomprobante(),
                        v.getSerie(),
                        v.getCorrelativo(),
                        v.getMetodopago(),
                        "Completada",
                        v.getTotal()
                ))
                .toList();

        EmpleadoPanelDTO panel = new EmpleadoPanelDTO(
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                ventaRepository.countByFechaventaBetween(inicioHoy, finHoy),
                movimientoRepository.countByFechamovimientoBetween(inicioHoy, finHoy),
                stockBajo,
                ventaRepository.count(),
                pedidoRepository.count(),
                productoRepository.count(),
                movimientoRepository.count(),
                ultimasVentas
        );

        return ResponseEntity.ok(Map.of("panel", panel));
    }

    @GetMapping("/inventario")
    public ResponseEntity<?> inventario(HttpSession session) {
        if (!esEmpleado(session)) {
            return new ResponseEntity<>(Map.of("mensaje", "Acceso solo para empleados"), HttpStatus.FORBIDDEN);
        }

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        var movimientos = movimientoRepository.findAll().stream()
                .sorted((a, b) -> {
                    if (a.getFechamovimiento() == null || b.getFechamovimiento() == null) return 0;
                    return b.getFechamovimiento().compareTo(a.getFechamovimiento());
                })
                .map(m -> new MovimientoDTO(
                        m.getIdmovimiento(),
                        m.getFechamovimiento() != null ? m.getFechamovimiento().format(formato) : "",
                        m.getProducto() != null ? m.getProducto().getNombre() : "-",
                        m.getTipomovimiento(),
                        m.getCantidad(),
                        m.getObservacion()
                ))
                .toList();

        var productos = productoRepository.findAll().stream().map(this::convertirProducto).toList();
        var stockBajo = inventarioService.productosConStockBajo().stream().map(this::convertirProducto).toList();

        InventarioDataDTO data = new InventarioDataDTO(movimientos, productos, stockBajo);
        return ResponseEntity.ok(Map.of("inventario", data));
    }

    @PostMapping("/inventario/movimiento")
    public ResponseEntity<?> registrarMovimiento(@RequestBody MovimientoRegistroDTO dto, HttpSession session) {
        if (!esEmpleado(session)) {
            return new ResponseEntity<>(Map.of("mensaje", "Acceso solo para empleados"), HttpStatus.FORBIDDEN);
        }
        try {
            inventarioService.registrarMovimiento(dto.getIdProducto(), dto.getTipoMovimiento(), dto.getCantidad(), dto.getObservacion());
            return new ResponseEntity<>(Map.of("mensaje", "Movimiento registrado correctamente"), HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(Map.of("mensaje", ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/pedidos")
    public ResponseEntity<?> pedidos(HttpSession session) {
        if (!esEmpleado(session)) {
            return new ResponseEntity<>(Map.of("mensaje", "Acceso solo para empleados"), HttpStatus.FORBIDDEN);
        }

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        var lista = pedidoRepository.findAllByOrderByFechapedidoDesc().stream()
                .map(p -> new PedidoReporteDTO(
                        p.getIdpedido(),
                        p.getFechapedido() != null ? p.getFechapedido().format(formato) : "",
                        p.getCliente() != null ? p.getCliente().getNombres() + " " + (p.getCliente().getApellidos() != null ? p.getCliente().getApellidos() : "") : "-",
                        p.getUsuario() != null ? p.getUsuario().getUsername() : "-",
                        p.getEstado(),
                        p.getDireccionentrega(),
                        p.getTotal()
                ))
                .toList();

        return ResponseEntity.ok(Map.of("pedidos", lista));
    }

    @GetMapping("/pedidos/{id}")
    public ResponseEntity<?> detallePedido(@PathVariable Long id, HttpSession session) {
        if (!esEmpleado(session)) {
            return new ResponseEntity<>(Map.of("mensaje", "Acceso solo para empleados"), HttpStatus.FORBIDDEN);
        }

        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido == null) {
            return new ResponseEntity<>(Map.of("mensaje", "Pedido no encontrado"), HttpStatus.NOT_FOUND);
        }

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        List<PedidoDetalleDTO> detalles = pedidoDetalleRepository.findByPedido(pedido).stream()
                .map(d -> new PedidoDetalleDTO(d.getProducto().getNombre(), d.getCantidad(), d.getPreciounitario(), d.getTotallinea()))
                .toList();

        Pago pago = pagoRepository.findFirstByPedidoOrderByIdpagoDesc(pedido).orElse(null);
        PagoDTO pagoDTO = pago != null ? new PagoDTO(
                pago.getPasarela(), pago.getCodigooperacion(), pago.getMoneda(), pago.getMonto(),
                pago.getEstado(), pago.getRespuestapasarela()
        ) : null;

        Comprobante comprobante = comprobanteRepository.findByPedido(pedido).orElse(null);
        ComprobanteDTO comprobanteDTO = comprobante != null ? new ComprobanteDTO(
                comprobante.getIdcomprobante(), comprobante.getTipo(), comprobante.getSerie(), comprobante.getCorrelativo(),
                comprobante.getFechaemision() != null ? comprobante.getFechaemision().format(formato) : "",
                comprobante.getEstado()
        ) : null;

        PedidoConfirmacionDTO resultado = new PedidoConfirmacionDTO(
                pedido.getIdpedido(),
                pedido.getFechapedido() != null ? pedido.getFechapedido().format(formato) : "",
                pedido.getEstado(),
                pedido.getCliente() != null ? pedido.getCliente().getNombres() + " " + (pedido.getCliente().getApellidos() != null ? pedido.getCliente().getApellidos() : "") : "-",
                pedido.getCliente() != null ? pedido.getCliente().getDni() : "-",
                pedido.getDireccionentrega(),
                pedido.getObservacion(),
                pedido.getSubtotal(), pedido.getIgv(), pedido.getTotal(),
                detalles, pagoDTO, comprobanteDTO
        );

        return ResponseEntity.ok(Map.of("pedido", resultado));
    }

    private ProductoDTO convertirProducto(Producto p) {
        ProductoDTO dto = new ProductoDTO();
        dto.setIdProducto(p.getIdproducto());
        dto.setNombre(p.getNombre());
        dto.setStockActual(p.getStockactual());
        dto.setStockMinimo(p.getStockminimo());
        dto.setImagenUrl(productoImagenService.imagenPrincipal(p));
        return dto;
    }

    @SuppressWarnings("unchecked")
    private boolean esEmpleado(HttpSession session) {
        Object roles = session.getAttribute("roles");
        return roles instanceof Set<?> r && r.stream().anyMatch(rol ->
                "EMPLEADO".equals(String.valueOf(rol)) || "ADMIN".equals(String.valueOf(rol)));
    }
}
