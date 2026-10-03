package com.techcenter.api.controllerapi;

import com.techcenter.api.dto.MarcaDTO;
import com.techcenter.api.serviceapi.MarcaApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/marcas")
@RequiredArgsConstructor
public class MarcaRestController {

    private final MarcaApiService marcaApiService;

    @GetMapping
    public ResponseEntity<List<MarcaDTO>> listar() {

        return ResponseEntity.ok(marcaApiService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarcaDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(marcaApiService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> registrar(
            @RequestBody MarcaDTO dto) {

        Map<String, String> response = new HashMap<>();

        try {
            marcaApiService.registrarMarca(dto);
            response.put("mensaje","Marca creada correctamente");

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            response.put("mensaje","Ocurrió un error al registrar la marca");

            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, String>> actualizar(
            @PathVariable Long id,
            @RequestBody MarcaDTO dto) {

        Map<String, String> response = new HashMap<>();

        try {
            dto.setIdMarca(id);
            marcaApiService.actualizarMarca(dto);
            response.put("mensaje","Marca actualizada correctamente");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("mensaje","Ocurrió un error al actualizar la marca");

            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(
            @PathVariable Long id) {

        Map<String, String> response = new HashMap<>();

        try {
            marcaApiService.eliminarMarca(id);
            response.put("mensaje","Marca eliminada correctamente");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("mensaje","Ocurrió un error al eliminar la marca");

            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
