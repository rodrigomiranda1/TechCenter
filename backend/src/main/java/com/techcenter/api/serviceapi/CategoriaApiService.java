package com.techcenter.api.serviceapi;

import com.techcenter.api.dto.CategoriaDTO;
import com.techcenter.api.model.Categoria;
import com.techcenter.api.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaApiService {

    private final CategoriaRepository categoriaRepository;

    public List<CategoriaDTO> listar() {

        return categoriaRepository.findAll()
                .stream()
                .map(this::convertirDTO)
                .toList();
    }

    public CategoriaDTO buscarPorId(Long id) {

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Categoría no encontrada"));

        return convertirDTO(categoria);
    }

    public void registrarCategoria(CategoriaDTO dto) {

        Categoria categoria = new Categoria();

        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        categoria.setActivo(true);

        categoriaRepository.save(categoria);
    }

    public void actualizarCategoria(CategoriaDTO dto) {

        Categoria categoria = categoriaRepository.findById(dto.getIdCategoria()).orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        categoria.setActivo(dto.getActivo());

        categoriaRepository.save(categoria);
    }

    public void eliminarCategoria(Long id) {

        int filas = categoriaRepository.desactivarCategoria(id);

        if (filas == 0) {
            throw new IllegalArgumentException("Categoría no encontrada");
        }
    }
    private CategoriaDTO convertirDTO(Categoria categoria) {

        CategoriaDTO dto = new CategoriaDTO();

        dto.setIdCategoria(categoria.getIdcategoria());
        dto.setNombre(categoria.getNombre());
        dto.setDescripcion(categoria.getDescripcion());
        dto.setActivo(categoria.getActivo());

        return dto;
    }
}
