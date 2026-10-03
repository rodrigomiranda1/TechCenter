package com.techcenter.api.controller;

import com.techcenter.api.model.Producto;
import com.techcenter.api.repository.ProductoRepository;
import com.techcenter.api.service.ProductoImagenService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class CarritoController {
    private final ProductoRepository productoRepository;
    private final ProductoImagenService productoImagenService;

    public CarritoController(ProductoRepository productoRepository, ProductoImagenService productoImagenService) {
        this.productoRepository = productoRepository;
        this.productoImagenService = productoImagenService;
    }

    @PostMapping("/carrito/agregar")
    public String agregar(@RequestParam Long idProducto, @RequestParam(defaultValue = "1") Integer cantidad,
                          HttpSession session, RedirectAttributes redirect) {
        if (session.getAttribute("usuarioActual") == null) {
            redirect.addFlashAttribute("error", "Inicia sesion para agregar productos al carrito");
            return "redirect:/login";
        }
        Map<Long, Integer> carrito = carrito(session);
        carrito.merge(idProducto, Math.max(1, cantidad), Integer::sum);
        session.setAttribute("carrito", carrito);
        redirect.addFlashAttribute("mensajeExito", "Producto agregado al carrito");
        return "redirect:/";
    }

    @GetMapping("/carrito")
    public String ver(HttpSession session, Model model, RedirectAttributes redirect) {
        if (session.getAttribute("usuarioActual") == null) {
            redirect.addFlashAttribute("error", "Inicia sesion para ver tu carrito");
            return "redirect:/login";
        }
        Map<Producto, Integer> items = new LinkedHashMap<>();
        BigDecimal total = BigDecimal.ZERO;
        for (var entry : carrito(session).entrySet()) {
            Producto producto = productoRepository.findById(entry.getKey()).orElse(null);
            if (producto != null) {
                items.put(producto, entry.getValue());
                total = total.add(producto.getPrecio().multiply(BigDecimal.valueOf(entry.getValue())));
            }
        }
        model.addAttribute("items", items);
        model.addAttribute("total", total);
        model.addAttribute("imagenes", productoImagenService.imagenPrincipalPorProducto(items.keySet().stream().toList()));
        return "carrito";
    }

    @PostMapping("/carrito/vaciar")
    public String vaciar(HttpSession session, RedirectAttributes redirect) {
        session.removeAttribute("carrito");
        redirect.addFlashAttribute("mensajeExito", "Carrito vaciado");
        return "redirect:/carrito";
    }

    @PostMapping("/carrito/quitar")
    public String quitar(@RequestParam Long idProducto, HttpSession session, RedirectAttributes redirect) {
        Map<Long, Integer> carrito = carrito(session);
        carrito.remove(idProducto);
        session.setAttribute("carrito", carrito);
        redirect.addFlashAttribute("mensajeExito", "Producto retirado del carrito");
        return "redirect:/carrito";
    }

    @PostMapping("/carrito/cantidad")
    public String actualizarCantidad(@RequestParam Long idProducto, @RequestParam String accion, HttpSession session,
                                     RedirectAttributes redirect) {
        Map<Long, Integer> carrito = carrito(session);
        int actual = carrito.getOrDefault(idProducto, 0);
        if ("sumar".equals(accion)) {
            carrito.put(idProducto, actual + 1);
            redirect.addFlashAttribute("mensajeExito", "Cantidad actualizada");
        } else if ("restar".equals(accion)) {
            if (actual <= 1) {
                carrito.remove(idProducto);
                redirect.addFlashAttribute("mensajeExito", "Producto retirado del carrito");
            } else {
                carrito.put(idProducto, actual - 1);
                redirect.addFlashAttribute("mensajeExito", "Cantidad actualizada");
            }
        }
        session.setAttribute("carrito", carrito);
        return "redirect:/carrito";
    }

    @SuppressWarnings("unchecked")
    private Map<Long, Integer> carrito(HttpSession session) {
        Object carrito = session.getAttribute("carrito");
        if (carrito instanceof Map<?, ?>) {
            return (Map<Long, Integer>) carrito;
        }
        return new LinkedHashMap<>();
    }
}
