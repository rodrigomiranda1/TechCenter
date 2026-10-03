package com.techcenter.api.controllerapi;

import com.techcenter.api.dto.*;
import com.techcenter.api.model.Comprobante;
import com.techcenter.api.model.Pago;
import com.techcenter.api.model.Pedido;
import com.techcenter.api.model.Usuario;
import com.techcenter.api.repository.ComprobanteRepository;
import com.techcenter.api.repository.PagoRepository;
import com.techcenter.api.repository.PedidoDetalleRepository;
import com.techcenter.api.repository.PedidoRepository;
import com.techcenter.api.service.PedidoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class CheckoutRestController {

    private final PedidoService pedidoService;
    private final PedidoRepository pedidoRepository;
    private final PedidoDetalleRepository pedidoDetalleRepository;
    private final PagoRepository pagoRepository;
    private final ComprobanteRepository comprobanteRepository;

    @PostMapping("/confirmar")
    public ResponseEntity<Map<String, Object>> confirmar(@RequestBody CheckoutConfirmarDTO dto, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            validarPagoDemo(dto.getMetodoPago(), dto.getNumeroTarjeta(), dto.getCodigoOperacion());

            Usuario usuario = (Usuario) session.getAttribute("usuarioActual");
            @SuppressWarnings("unchecked")
            Map<Long, Integer> carrito = (Map<Long, Integer>) session.getAttribute("carrito");

            Pedido pedido = pedidoService.confirmarPedido(usuario, carrito, dto.getDireccionEntrega(),
                    dto.getTipoComprobante(), dto.getMetodoPago());

            session.removeAttribute("carrito");

            response.put("mensaje", "Pago sandbox aprobado. Comprobante emitido.");
            response.put("idPedido", pedido.getIdpedido());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            response.put("mensaje", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.put("mensaje", "Ocurrió un error al procesar el pago");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/confirmacion/{id}")
    public ResponseEntity<Map<String, Object>> confirmacion(@PathVariable Long id) {
        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido == null) {
            return new ResponseEntity<>(Map.of("mensaje", "Pedido no encontrado"), HttpStatus.NOT_FOUND);
        }

        PedidoConfirmacionDTO resultado = construirConfirmacion(pedido);
        return ResponseEntity.ok(Map.of("pedido", resultado));
    }

    private PedidoConfirmacionDTO construirConfirmacion(Pedido pedido) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        List<PedidoDetalleDTO> detalles = pedidoDetalleRepository.findByPedido(pedido).stream()
                .map(d -> new PedidoDetalleDTO(d.getProducto().getNombre(), d.getCantidad(), d.getPreciounitario(),
                        d.getTotallinea()))
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

        return new PedidoConfirmacionDTO(
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
    }

    private void validarPagoDemo(String metodoPago, String numeroTarjeta, String codigoOperacion) {
        if ("TARJETA_DEMO".equals(metodoPago)) {
            String numero = numeroTarjeta == null ? "" : numeroTarjeta.replaceAll("\\s+", "");
            if (!numero.equals("4509953566233704") && !numero.equals("5031755734530604")) {
                throw new IllegalArgumentException("Usa una tarjeta demo válida de Mercado Pago");
            }
        }
        if ("YAPE_DEMO".equals(metodoPago)) {
            String codigo = codigoOperacion == null ? "" : codigoOperacion.trim();
            if (!codigo.matches("^[0-9A-Za-z-]{5,20}$")) {
                throw new IllegalArgumentException("Ingresa el código de operación Yape demo");
            }
        }
    }
}
