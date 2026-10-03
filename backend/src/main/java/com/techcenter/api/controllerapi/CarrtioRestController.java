package com.techcenter.api.controllerapi;

import com.techcenter.api.dto.CarritoItemDTO;
import com.techcenter.api.dto.CarritoResumenDTO;
import com.techcenter.api.model.Producto;
import com.techcenter.api.repository.ProductoRepository;
import com.techcenter.api.service.ProductoImagenService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
public class CarrtioRestController {

    private final ProductoRepository productoRepository;
    private final ProductoImagenService productoImagenService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> ver(HttpSession session) {
        if (!haySesion(session)) return sinSesion();
        Map<String, Object> response = new HashMap<>();
        response.put("carrito", construirResumen(session));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/agregar")
    public ResponseEntity<Map<String, Object>> agregar(@RequestBody Map<String, Object> body, HttpSession session) {
        if (!haySesion(session)) return sinSesion();

        Long idProducto = Long.valueOf(body.get("idProducto").toString());
        Integer cantidad = body.get("cantidad") != null ? Integer.valueOf(body.get("cantidad").toString()) : 1;

        Map<Long, Integer> carrito = carrito(session);
        carrito.merge(idProducto, Math.max(1, cantidad), Integer::sum);
        session.setAttribute("carrito", carrito);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Producto agregado al carrito");
        response.put("carrito", construirResumen(session));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/cantidad")
    public ResponseEntity<Map<String, Object>> actualizarCantidad(@RequestBody Map<String, Object> body, HttpSession session) {
        if (!haySesion(session)) return sinSesion();

        Long idProducto = Long.valueOf(body.get("idProducto").toString());
        String accion = String.valueOf(body.get("accion"));

        Map<Long, Integer> carrito = carrito(session);
        int actual = carrito.getOrDefault(idProducto, 0);

        if ("sumar".equals(accion)) {
            carrito.put(idProducto, actual + 1);
        } else if ("restar".equals(accion)) {
            if (actual <= 1) {
                carrito.remove(idProducto);
            } else {
                carrito.put(idProducto, actual - 1);
            }
        }

        session.setAttribute("carrito", carrito);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Cantidad actualizada");
        response.put("carrito", construirResumen(session));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/quitar/{idProducto}")
    public ResponseEntity<Map<String, Object>> quitar(@PathVariable Long idProducto, HttpSession session) {
        if (!haySesion(session)) return sinSesion();

        Map<Long, Integer> carrito = carrito(session);
        carrito.remove(idProducto);
        session.setAttribute("carrito", carrito);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Producto retirado del carrito");
        response.put("carrito", construirResumen(session));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/vaciar")
    public ResponseEntity<Map<String, Object>> vaciar(HttpSession session) {
        if (!haySesion(session)) return sinSesion();

        session.removeAttribute("carrito");

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Carrito vaciado");
        response.put("carrito", construirResumen(session));
        return ResponseEntity.ok(response);
    }

    private boolean haySesion(HttpSession session) {
        return session.getAttribute("usuarioActual") != null;
    }

    private ResponseEntity<Map<String, Object>> sinSesion() {
        return new ResponseEntity<>(Map.of("mensaje", "Inicia sesión para gestionar tu carrito"), HttpStatus.UNAUTHORIZED);
    }

    private CarritoResumenDTO construirResumen(HttpSession session) {
        Map<Long, Integer> carrito = carrito(session);
        java.util.List<CarritoItemDTO> items = new java.util.ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (var entry : carrito.entrySet()) {
            Producto producto = productoRepository.findById(entry.getKey()).orElse(null);
            if (producto == null) continue;

            Integer cantidad = entry.getValue();
            BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(cantidad));
            total = total.add(subtotal);

            items.add(new CarritoItemDTO(
                    producto.getIdproducto(),
                    producto.getCodigo(),
                    producto.getNombre(),
                    producto.getPrecio(),
                    cantidad,
                    subtotal,
                    producto.getStockactual(),
                    productoImagenService.imagenPrincipal(producto)
            ));
        }

        return new CarritoResumenDTO(items, total);
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
