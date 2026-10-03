package com.techcenter.api.controller;

import com.techcenter.api.model.Cliente;
import com.techcenter.api.repository.ClienteRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/clientes")
public class ClienteController {
    private final ClienteRepository clienteRepository;

    public ClienteController(ClienteRepository clienteRepository){
        this.clienteRepository=clienteRepository;

    }

    @GetMapping("/buscar")
    public Map<String,Object> buscar(@RequestParam String dni){
        return clienteRepository.findByDni(dni)
                .<Map<String,Object>>map(c->Map.of(

                        "encontrado",true,
                        "idCliente",c.getIdcliente(),
                        "nombres",c.getNombres(),
                        "apellidos",c.getApellidos(),
                        "direccion",c.getDireccion(),
                        "telefono",c.getTelefono()
                ))
                .orElse(Map.of("encontrado",false));
    }

    @PostMapping("/guardar")
    public Cliente guardar(@RequestBody Cliente cliente){

        cliente.setFecharegistro(LocalDateTime.now());
        cliente.setTipodocumento("DNI");
        cliente.setNumerodocumento(cliente.getDni());
        return clienteRepository.save(cliente);

    }
}
