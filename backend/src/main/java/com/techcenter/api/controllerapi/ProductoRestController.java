package com.techcenter.api.controllerapi;

import com.techcenter.api.dto.ProductoDTO;
import com.techcenter.api.serviceapi.ProductoApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoRestController {

    private final ProductoApiService productoApiService;

    // GET /api/productos
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listar() {
        return ResponseEntity.ok(productoApiService.listar());
    }

    // GET /api/productos/5
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> buscarPorId(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            ProductoDTO producto = productoApiService.buscarPorId(id);
            return ResponseEntity.ok(Map.of("producto", producto));
        } catch (IllegalArgumentException e) {
            response.put("mensaje", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    // POST /api/productos
    @PostMapping
    public ResponseEntity<Map<String, Object>> registrar(@RequestBody ProductoDTO dto) {
        Map<String, Object> response = new HashMap<>();
        try {
            productoApiService.registrarProducto(dto);
            response.put("mensaje", "Producto registrado correctamente");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            response.put("mensaje", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // PUT /api/productos/5
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(@PathVariable Long id, @RequestBody ProductoDTO dto) {
        Map<String, Object> response = new HashMap<>();
        try {
            dto.setIdProducto(id);
            productoApiService.actualizarProducto(dto);
            response.put("mensaje", "Producto actualizado correctamente");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("mensaje", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    // DELETE /api/productos/5
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            productoApiService.eliminarProducto(id);
            response.put("mensaje", "Producto eliminado correctamente");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("mensaje", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
