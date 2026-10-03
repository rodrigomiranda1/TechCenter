package com.techcenter.api.controller;

import com.techcenter.api.repository.*;
import com.techcenter.api.service.VentaPdfService;
import com.techcenter.api.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;
    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final DetalleVentaRepository detalleRepository;
    private final ComprobanteRepository comprobanteRepository;
    private final VentaPdfService ventaPdfService;

    @GetMapping("")
    public String registrarVenta(Model model) {

        model.addAttribute("clientes", clienteRepository.findAll());

        model.addAttribute("usuarios", usuarioRepository.findAll());

        model.addAttribute("productos",
                productoRepository.findByActivoTrue());

        return "ventas/registrar";
    }

    @PostMapping("/registrar")
    public String registrar(@RequestParam Long idCliente, @RequestParam Long idUsuario, @RequestParam String tipoComprobante, @RequestParam String serie,
                            @RequestParam Integer correlativo, @RequestParam String tipoEntrega, @RequestParam(required = false) String observacion, @RequestParam BigDecimal subtotal,
                            @RequestParam BigDecimal igv, @RequestParam BigDecimal total, @RequestParam String metodoPago, @RequestParam String detalleJson, RedirectAttributes redirect) {
        try {
            ventaService.registrar( idCliente, idUsuario, tipoComprobante, serie, correlativo, tipoEntrega, observacion, subtotal, igv, total,
                    metodoPago, detalleJson);
            redirect.addFlashAttribute("mensajeExito", "Venta registrada correctamente");

        } catch (Exception e) {
            e.printStackTrace();
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ventas";
    }

    @GetMapping("/listado")
    public String listado(@RequestParam(defaultValue = "0") int page, Model model){

        var ventas = ventaService.listarPaginado(page);

        model.addAttribute("ventas", ventas);
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPaginas", ventas.getTotalPages());

        return "ventas/listado";
    }

    @GetMapping("/comprobante/{id}")
    public String comprobante(@PathVariable Long id, Model model) {

        model.addAttribute("venta", ventaRepository.findById(id).orElseThrow());

        model.addAttribute("detalles", detalleRepository.findByVenta_Idventa(id));

        return "ventas/comprobante";
    }

    @GetMapping("/correlativo")
    @ResponseBody
    public String obtenerCorrelativo(@RequestParam String serie){

        return ventaService.generarCorrelativo(serie);

    }

    @GetMapping("/comprobante/{id}/pdf")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable Long id) {

        var venta = ventaRepository.findById(id).orElseThrow();

        byte[] pdf = ventaPdfService.generar(venta, detalleRepository.findByVenta_Idventa(id));

        String filename = venta.getTipocomprobante().toLowerCase() + "-" + venta.getSerie() + "-" + String.format("%08d", venta.getCorrelativo()) + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(filename)
                        .build()
                        .toString())
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

}
