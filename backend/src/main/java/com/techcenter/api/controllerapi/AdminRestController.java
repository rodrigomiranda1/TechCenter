package com.techcenter.api.controllerapi;

import com.techcenter.api.dto.*;
import com.techcenter.api.model.Pedido;
import com.techcenter.api.model.Producto;
import com.techcenter.api.model.Venta;
import com.techcenter.api.repository.AuditoriaRepository;
import com.techcenter.api.repository.PedidoRepository;
import com.techcenter.api.repository.ProductoRepository;
import com.techcenter.api.repository.VentaRepository;
import com.techcenter.api.service.InventarioService;
import com.techcenter.api.service.ProductoImagenService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminRestController {

    private final ProductoRepository productoRepository;
    private final InventarioService inventarioService;
    private final ProductoImagenService productoImagenService;
    private final AuditoriaRepository auditoriaRepository;
    private final PedidoRepository pedidoRepository;
    private final VentaRepository ventaRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> dashboard(HttpSession session) {
        if (!esAdmin(session)) {
            return new ResponseEntity<>(Map.of("mensaje", "Acceso solo para administradores"), HttpStatus.FORBIDDEN);
        }

        long totalProductos = productoRepository.count();
        var stockBajo = inventarioService.productosConStockBajo().stream()
                .map(this::convertirDTO)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("totalProductos", totalProductos);
        response.put("stockBajo", stockBajo);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/auditoria")
    public ResponseEntity<?> auditoria(HttpSession session) {
        if (!esAdmin(session)) {
            return new ResponseEntity<>(Map.of("mensaje", "Acceso solo para administradores"), HttpStatus.FORBIDDEN);
        }

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        var lista = auditoriaRepository.findAll().stream()
                .map(a -> new AuditoriaDTO(
                        a.getFecha() != null ? a.getFecha().format(formato) : "",
                        a.getAccion(),
                        a.getEntidad()
                ))
                .toList();

        return ResponseEntity.ok(Map.of("auditorias", lista));
    }

    @GetMapping("/pedidos")
    public ResponseEntity<?> pedidos(HttpSession session) {
        if (!esAdmin(session)) {
            return new ResponseEntity<>(Map.of("mensaje", "Acceso solo para administradores"), HttpStatus.FORBIDDEN);
        }

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        var lista = pedidoRepository.findAllByOrderByFechapedidoDesc().stream()
                .map(p -> new PedidoResumenDTO(
                        p.getIdpedido(),
                        p.getFechapedido() != null ? p.getFechapedido().format(formato) : "",
                        p.getCliente() != null ? p.getCliente().getNombres() : "-",
                        p.getTotal(),
                        p.getEstado()
                ))
                .toList();

        return ResponseEntity.ok(Map.of("pedidos", lista));
    }

    @GetMapping("/reportes")
    public ResponseEntity<?> reportes(
            @RequestParam(required = false) String ventasDesde,
            @RequestParam(required = false) String ventasHasta,
            @RequestParam(required = false) String pedidosDesde,
            @RequestParam(required = false) String pedidosHasta,
            HttpSession session) {

        if (!esAdmin(session)) {
            return new ResponseEntity<>(Map.of("mensaje", "Acceso solo para administradores"), HttpStatus.FORBIDDEN);
        }

        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        LocalDate vDesde = parsearFecha(ventasDesde);
        LocalDate vHasta = parsearFecha(ventasHasta);
        LocalDate pDesde = parsearFecha(pedidosDesde);
        LocalDate pHasta = parsearFecha(pedidosHasta);

        List<Venta> ventas = ventaRepository.findAll().stream()
                .filter(v -> v.getFechaventa() != null)
                .filter(v -> dentroDeRango(v.getFechaventa(), vDesde, vHasta))
                .sorted((a, b) -> b.getFechaventa().compareTo(a.getFechaventa()))
                .toList();

        List<VentaReporteDTO> ventasDTO = ventas.stream().map(v -> new VentaReporteDTO(
                v.getIdventa(),
                v.getFechaventa().format(formatoFecha),
                v.getCliente() != null ? v.getCliente().getNombres() + " " + (v.getCliente().getApellidos() != null ? v.getCliente().getApellidos() : "") : "-",
                v.getUsuario() != null ? v.getUsuario().getUsername() : "-",
                v.getTipocomprobante(),
                v.getSerie(),
                v.getCorrelativo(),
                v.getMetodopago(),
                v.getEstado(),
                v.getTotal()
        )).toList();

        BigDecimal totalVentas = ventas.stream().map(Venta::getTotal).filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Pedido> pedidos = pedidoRepository.findAll().stream()
                .filter(p -> p.getFechapedido() != null)
                .filter(p -> dentroDeRango(p.getFechapedido(), pDesde, pHasta))
                .sorted((a, b) -> b.getFechapedido().compareTo(a.getFechapedido()))
                .toList();

        List<PedidoReporteDTO> pedidosDTO = pedidos.stream().map(p -> new PedidoReporteDTO(
                p.getIdpedido(),
                p.getFechapedido().format(formatoFecha),
                p.getCliente() != null ? p.getCliente().getNombres() + " " + (p.getCliente().getApellidos() != null ? p.getCliente().getApellidos() : "") : "-",
                p.getUsuario() != null ? p.getUsuario().getUsername() : "-",
                p.getEstado(),
                p.getDireccionentrega(),
                p.getTotal()
        )).toList();

        BigDecimal totalPedidos = pedidos.stream().map(Pedido::getTotal).filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ReportesDTO resultado = new ReportesDTO(ventasDTO, ventasDTO.size(), totalVentas, pedidosDTO, pedidosDTO.size(), totalPedidos);
        return ResponseEntity.ok(Map.of("reportes", resultado));
    }

    private LocalDate parsearFecha(String valor) {
        if (valor == null || valor.isBlank()) return null;
        try {
            return LocalDate.parse(valor);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean dentroDeRango(LocalDateTime fecha, LocalDate desde, LocalDate hasta) {
        if (desde != null && fecha.isBefore(desde.atStartOfDay())) return false;
        if (hasta != null && !fecha.isBefore(hasta.plusDays(1).atStartOfDay())) return false;
        return true;
    }

    private ProductoDTO convertirDTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setIdProducto(producto.getIdproducto());
        dto.setNombre(producto.getNombre());
        dto.setStockActual(producto.getStockactual());
        dto.setStockMinimo(producto.getStockminimo());
        dto.setImagenUrl(productoImagenService.imagenPrincipal(producto));
        return dto;
    }

    @SuppressWarnings("unchecked")
    private boolean esAdmin(HttpSession session) {
        Object roles = session.getAttribute("roles");
        return roles instanceof Set<?> r && r.stream().anyMatch(rol -> "ADMIN".equals(String.valueOf(rol)));
    }
}
