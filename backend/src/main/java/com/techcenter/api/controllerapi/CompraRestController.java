package com.techcenter.api.controllerapi;


import com.techcenter.api.dto.CompraDTO;
import com.techcenter.api.dto.CompraRegistroDTO;
import com.techcenter.api.repository.CompraRepository;
import com.techcenter.api.repository.ProductoRepository;
import com.techcenter.api.repository.ProveedorRepository;
import com.techcenter.api.service.CompraService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/operaciones/compras")
@RequiredArgsConstructor
public class CompraRestController {

    private final CompraService compraService;
    private final CompraRepository compraRepository;
    private final ProveedorRepository proveedorRepository;
    private final ProductoRepository productoRepository;

    @GetMapping
    public ResponseEntity<?> listar(HttpSession session) {
        if (!esAdmin(session)) return sinPermiso();

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        var compras = compraRepository.findAll().stream()
                .sorted((a, b) -> b.getIdcompra().compareTo(a.getIdcompra()))
                .map(c -> new CompraDTO(
                        c.getIdcompra(),
                        c.getFechacompra() != null ? c.getFechacompra().format(formato) : "",
                        c.getProveedor() != null ? c.getProveedor().getRazonsocial() : "-",
                        c.getTotal()
                ))
                .toList();

        var proveedores = proveedorRepository.findAll();
        var productos = productoRepository.findByActivoTrue();

        Map<String, Object> response = new java.util.HashMap<>();
        response.put("compras", compras);
        response.put("proveedores", proveedores);
        response.put("productos", productos);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody CompraRegistroDTO dto, HttpSession session) {
        if (!esAdmin(session)) return sinPermiso();
        try {
            compraService.registrar(dto.getIdProveedor(), dto.getIdProducto(), dto.getCantidad(), dto.getCostoUnitario());
            return new ResponseEntity<>(Map.of("mensaje", "Compra registrada y stock actualizado"), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("mensaje", "Error al registrar la compra"), HttpStatus.BAD_REQUEST);
        }
    }

    @SuppressWarnings("unchecked")
    private boolean esAdmin(HttpSession session) {
        Object roles = session.getAttribute("roles");
        return roles instanceof Set<?> r && r.stream().anyMatch(rol -> "ADMIN".equals(String.valueOf(rol)));
    }

    private ResponseEntity<Map<String, Object>> sinPermiso() {
        return new ResponseEntity<>(Map.of("mensaje", "Acceso solo para administradores"), HttpStatus.FORBIDDEN);
    }
}
