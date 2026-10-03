package com.techcenter.api.controller;

import com.techcenter.api.repository.*;
import com.techcenter.api.service.InventarioService;
import com.techcenter.api.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/empleado")
@RequiredArgsConstructor
public class EmpleadoController {
    private final VentaService ventaService;
    private final InventarioService inventarioService;

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final PedidoRepository pedidoRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;

    private final PedidoDetalleRepository pedidoDetalleRepository;
    private final PagoRepository pagoRepository;
    private final ComprobanteRepository comprobanteRepository;

    @GetMapping("")
    public String inicio(Model model) {
        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        LocalDateTime finHoy = inicioHoy.plusDays(1);
        model.addAttribute("fechaActual", LocalDate.now());
        model.addAttribute("ventasHoy", ventaRepository.countByFechaventaBetween(inicioHoy, finHoy));
        model.addAttribute("movimientosHoy", movimientoRepository.countByFechamovimientoBetween(inicioHoy, finHoy));
        model.addAttribute("stockBajo", inventarioService.productosConStockBajo());
        model.addAttribute("totalVentas", ventaRepository.count());
        model.addAttribute("totalPedidos", pedidoRepository.count());
        model.addAttribute("totalProductos", productoRepository.count());
        model.addAttribute("totalMovimientos", movimientoRepository.count());
        model.addAttribute("ultimasVentas", ventaRepository.findTop5ByOrderByFechaventaDesc());
        return "empleado/inicio";
    }

    @GetMapping("/pedidos")
    public String pedidos(Model model) {
        model.addAttribute("pedidos", pedidoRepository.findAllByOrderByFechapedidoDesc());
        return "empleado/pedidos";
    }

    @GetMapping("/inventario")
    public String inventario(Model model) {
        model.addAttribute("movimientos", movimientoRepository.findAll());
        model.addAttribute("productos", productoRepository.findAll());
        model.addAttribute("stockBajo", inventarioService.productosConStockBajo());
        return "empleado/inventario";
    }

    @PostMapping("/inventario/movimiento")
    public String registrarMovimiento(@RequestParam Long idProducto, @RequestParam String tipoMovimiento, @RequestParam Integer cantidad, @RequestParam(required = false) String observacion,
                                      RedirectAttributes redirect) {
        try {
            inventarioService.registrarMovimiento(idProducto,tipoMovimiento,cantidad,observacion);
            redirect.addFlashAttribute("mensajeExito","Movimiento registrado correctamente");
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/empleado/inventario";
    }

    @GetMapping("/pedidos/{id}")
    public String detallePedido(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        var pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido == null) {
            redirect.addFlashAttribute("error", "Pedido no encontrado");
            return "redirect:/empleado/pedidos";
        }
        model.addAttribute("pedido", pedido);
        model.addAttribute("detalles", pedidoDetalleRepository.findByPedido(pedido));
        model.addAttribute("pago", pagoRepository.findFirstByPedidoOrderByIdpagoDesc(pedido).orElse(null));
        model.addAttribute("comprobante", comprobanteRepository.findByPedido(pedido).orElse(null));
        return "empleado/pedido-detalle";
    }

}
