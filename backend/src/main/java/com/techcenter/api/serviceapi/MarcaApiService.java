package com.techcenter.api.serviceapi;

import com.techcenter.api.dto.MarcaDTO;
import com.techcenter.api.model.Marca;
import com.techcenter.api.repository.MarcaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarcaApiService {

    private final MarcaRepository marcaRepository;

    public List<MarcaDTO> listar() {
        return marcaRepository.findAll().stream().map(this::convertirDTO).toList();
    }

    public MarcaDTO buscarPorId(Long id) {
        Marca marca = marcaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Marca no encontrada"));

        return convertirDTO(marca);
    }

    public void registrarMarca(MarcaDTO dto) {
        Marca marca = new Marca();
        marca.setNombre(dto.getNombre());
        marca.setActivo(true);

        marcaRepository.save(marca);
    }

    public void actualizarMarca(MarcaDTO dto) {
        Marca marca = marcaRepository.findById(dto.getIdMarca()).orElseThrow(() -> new IllegalArgumentException("Marca no encontrada"));
        marca.setNombre(dto.getNombre());

        if (dto.getActivo() != null) {
            marca.setActivo(dto.getActivo());
        }

        marcaRepository.save(marca);
    }

    public void eliminarMarca(Long id) {
        Marca marca = marcaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Marca no encontrada"));
        marca.setActivo(false);

        marcaRepository.save(marca);
    }

    private MarcaDTO convertirDTO(Marca marca) {
        return new MarcaDTO(marca.getIdmarca(),marca.getNombre(),marca.getActivo()
        );
    }
}
