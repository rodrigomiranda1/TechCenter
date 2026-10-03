package com.techcenter.api.serviceapi;

import com.techcenter.api.dto.LoginDTO;
import com.techcenter.api.dto.UsuarioDTO;
import com.techcenter.api.model.Usuario;
import com.techcenter.api.repository.UsuarioRepository;
import com.techcenter.api.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioApiService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordService passwordService;

    public List<UsuarioDTO> listar() {

        return usuarioRepository.findAll().stream().map(this::convertirDTO).toList();
    }

    public UsuarioDTO login(LoginDTO dto) {

        String login = dto.getUsuario() == null ? "" : dto.getUsuario().trim();

        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(login).or(() -> usuarioRepository.findByEmailIgnoreCase(login))
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (Boolean.FALSE.equals(usuario.getActivo())) {
            throw new IllegalArgumentException("El usuario se encuentra inactivo");
        }

        if (!passwordService.coincide(dto.getPassword(), usuario.getPasswordhash())) {
            throw new IllegalArgumentException("Contraseña incorrecta");
        }

        return convertirDTO(usuario);
    }

    private UsuarioDTO convertirDTO(Usuario usuario) {

        Set<String> roles = usuario.getRoles().stream().map(rol -> rol.getNombre()).collect(Collectors.toSet());

        return new UsuarioDTO(usuario.getIdusuario(), usuario.getUsername(), usuario.getEmail(), usuario.getActivo(), roles);
    }
}
