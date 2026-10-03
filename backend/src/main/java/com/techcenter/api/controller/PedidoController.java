package com.techcenter.api.controller;

import com.techcenter.api.model.Comprobante;
import com.techcenter.api.model.Pedido;
import com.techcenter.api.model.Usuario;
import com.techcenter.api.repository.*;
import com.techcenter.api.service.ComprobantePdfService;
import com.techcenter.api.service.PedidoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;
    private final PedidoRepository pedidoRepository;
    private final PedidoDetalleRepository pedidoDetalleRepository;
    private final PagoRepository pagoRepository;
    private final ComprobanteRepository comprobanteRepository;
    private final ComprobantePdfService comprobantePdfService;
    private final ProductoRepository productoRepository;

    @PostMapping("/checkout/confirmar")
    public String confirmar(@RequestParam(required = false) String direccionEntrega,
                            @RequestParam(defaultValue = "BOLETA") String tipoComprobante,
                            @RequestParam(defaultValue = "TARJETA_DEMO") String metodoPago,
                            @RequestParam(required = false) String numeroTarjeta,
                            @RequestParam(required = false) String codigoOperacion,
                            HttpSession session, RedirectAttributes redirect) {
        try {
            validarPagoDemo(metodoPago, numeroTarjeta, codigoOperacion);
            Usuario usuario = (Usuario) session.getAttribute("usuarioActual");
            @SuppressWarnings("unchecked")
            Map<Long, Integer> carrito = (Map<Long, Integer>) session.getAttribute("carrito");
            Pedido pedido = pedidoService.confirmarPedido(usuario, carrito, direccionEntrega, tipoComprobante, metodoPago);
            session.removeAttribute("carrito");
            redirect.addFlashAttribute("mensajeExito", "Pago sandbox aprobado. Comprobante emitido.");
            return "redirect:/checkout/confirmacion/" + pedido.getIdpedido();
        } catch (Exception ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
            return "redirect:/carrito";
        }
    }

    @PostMapping("/checkout/pago")
    public String pago(@RequestParam(required = false) String direccionEntrega,
                       @RequestParam(defaultValue = "BOLETA") String tipoComprobante,
                       @RequestParam(defaultValue = "TARJETA_DEMO") String metodoPago, HttpSession session, Model model,
                       RedirectAttributes redirect) {
        @SuppressWarnings("unchecked")
        Map<Long, Integer> carrito = (Map<Long, Integer>) session.getAttribute("carrito");
        if (carrito == null || carrito.isEmpty()) {
            redirect.addFlashAttribute("error", "El carrito esta vacio");
            return "redirect:/carrito";
        }
        model.addAttribute("direccionEntrega", direccionEntrega);
        model.addAttribute("tipoComprobante", tipoComprobante);
        model.addAttribute("metodoPago", metodoPago);
        model.addAttribute("total", calcularTotal(carrito));
        return "checkout-pago";
    }

    @GetMapping("/checkout/confirmacion/{id}")
    public String confirmacion(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido == null) {
            redirect.addFlashAttribute("error", "Pedido no encontrado");
            return "redirect:/";
        }
        model.addAttribute("pedido", pedido);
        model.addAttribute("detalles", pedidoDetalleRepository.findByPedido(pedido));
        model.addAttribute("pago", pagoRepository.findFirstByPedidoOrderByIdpagoDesc(pedido).orElse(null));
        model.addAttribute("comprobante", comprobanteRepository.findByPedido(pedido).orElse(null));
        return "checkout-confirmacion";
    }

    @GetMapping("/comprobantes/{id}/pdf")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable Long id) {
        Comprobante comprobante = comprobanteRepository.findById(id).orElseThrow();
        byte[] pdf = comprobantePdfService.generar(comprobante,
                pedidoDetalleRepository.findByPedido(comprobante.getPedido()));
        String filename = comprobante.getTipo().toLowerCase() + "-" + comprobante.getSerie() + "-"
                + String.format("%08d", comprobante.getCorrelativo()) + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(filename).build().toString())
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/admin/pedidos")
    public String pedidosAdmin(Model model) {
        model.addAttribute("pedidos", pedidoRepository.findAllByOrderByFechapedidoDesc());
        model.addAttribute("comprobantes", comprobanteRepository.findAll());
        return "admin/pedidos";
    }

    private void validarPagoDemo(String metodoPago, String numeroTarjeta, String codigoOperacion) {
        if ("TARJETA_DEMO".equals(metodoPago)) {
            String numero = numeroTarjeta == null ? "" : numeroTarjeta.replaceAll("\\s+", "");
            if (!numero.equals("4509953566233704") && !numero.equals("5031755734530604")) {
                throw new IllegalArgumentException("Usa una tarjeta demo valida de Mercado Pago");
            }
        }
        if ("YAPE_DEMO".equals(metodoPago)) {
            String codigo = codigoOperacion == null ? "" : codigoOperacion.trim();
            if (!codigo.matches("^[0-9A-Za-z-]{5,20}$")) {
                throw new IllegalArgumentException("Ingresa el codigo de operacion Yape demo");
            }
        }
    }

    private BigDecimal calcularTotal(Map<Long, Integer> carrito) {
        BigDecimal total = BigDecimal.ZERO;
        for (var entry : carrito.entrySet()) {
            total = total.add(productoRepository.findById(entry.getKey())
                    .map(producto -> producto.getPrecio().multiply(BigDecimal.valueOf(entry.getValue())))
                    .orElse(BigDecimal.ZERO));
        }
        return total;
    }
}
