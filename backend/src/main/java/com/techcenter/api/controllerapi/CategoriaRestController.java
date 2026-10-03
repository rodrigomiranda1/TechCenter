package com.techcenter.api.controllerapi;

import com.techcenter.api.dto.CategoriaDTO;
import com.techcenter.api.serviceapi.CategoriaApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaRestController {

    private final CategoriaApiService categoriaApiService;

    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> listar() {

        return ResponseEntity.ok(
                categoriaApiService.listar()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                categoriaApiService.buscarPorId(id)
        );
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> registrarCategoria(
            @RequestBody CategoriaDTO dto) {

        Map<String, String> response = new HashMap<>();

        try {
            categoriaApiService.registrarCategoria(dto);
            response.put("mensaje", "Categoría creada correctamente");
            return new ResponseEntity<>(response,HttpStatus.CREATED);

        } catch (Exception e) {

            response.put("mensaje","Ocurrió un error al registrar la categoría");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> actualizarCategoria(
            @PathVariable Long id,
            @RequestBody CategoriaDTO dto) {

        Map<String, String> response = new HashMap<>();

        try {
            dto.setIdCategoria(id);
            categoriaApiService.actualizarCategoria(dto);
            response.put("mensaje","Categoría actualizada correctamente");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("mensaje","Ocurrió un error al actualizar la categoría");
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminarCategoria(
            @PathVariable Long id) {

        Map<String, String> response = new HashMap<>();

        try {
            categoriaApiService.eliminarCategoria(id);
            response.put("mensaje", "Categoría eliminada correctamente");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("mensaje","Ocurrió un error al eliminar la categoría");
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
