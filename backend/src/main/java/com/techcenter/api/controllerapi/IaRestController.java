package com.techcenter.api.controllerapi;

import com.techcenter.api.dto.ConsultaIaDTO;
import com.techcenter.api.model.Cliente;
import com.techcenter.api.model.ConsultaIa;
import com.techcenter.api.model.Usuario;
import com.techcenter.api.repository.ClienteRepository;
import com.techcenter.api.repository.ConsultaIaRepository;
import com.techcenter.api.service.IaService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cliente/ia")
@RequiredArgsConstructor
public class IaRestController {

    private final IaService iaService;
    private final ClienteRepository clienteRepository;
    private final ConsultaIaRepository consultaIaRepository;

    @PostMapping("/preguntar")
    public ResponseEntity<Map<String, Object>> preguntar(@RequestBody Map<String, String> body, HttpSession session) {
        String pregunta = body.get("pregunta");
        if (pregunta == null || pregunta.isBlank()) {
            return new ResponseEntity<>(Map.of("mensaje", "La pregunta es obligatoria"), HttpStatus.BAD_REQUEST);
        }

        Cliente cliente = clienteActual(session);
        ConsultaIa consulta = iaService.responder(cliente, pregunta, cliente != null);

        if (cliente == null) {
            agregarConsultaTemporal(session, consulta);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("pregunta", consulta.getPregunta());
        response.put("respuesta", consulta.getRespuesta());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/historial")
    public ResponseEntity<Map<String, Object>> historial(HttpSession session) {
        Cliente cliente = clienteActual(session);

        List<ConsultaIa> consultas = cliente != null
                ? consultaIaRepository.findByClienteOrderByFechaconsultaDesc(cliente)
                : consultasTemporales(session);

        List<ConsultaIaDTO> dtos = consultas.stream().map(this::convertirDTO).toList();

        Map<String, Object> response = new HashMap<>();
        response.put("consultas", dtos);
        response.put("historialPersistente", cliente != null);
        return ResponseEntity.ok(response);
    }

    private ConsultaIaDTO convertirDTO(ConsultaIa consulta) {
        return new ConsultaIaDTO(
                consulta.getIdconsulta(),
                consulta.getPregunta(),
                consulta.getRespuesta(),
                consulta.getFechaconsulta() != null ? consulta.getFechaconsulta().toString() : null
        );
    }

    private Cliente clienteActual(HttpSession session) {
        Usuario usuario = usuarioActual(session);
        if (usuario != null) {
            return clienteRepository.findByUsuario(usuario).orElse(null);
        }
        return null;
    }

    private Usuario usuarioActual(HttpSession session) {
        Object usuario = session.getAttribute("usuarioActual");
        return usuario instanceof Usuario usuarioActual ? usuarioActual : null;
    }

    @SuppressWarnings("unchecked")
    private List<ConsultaIa> consultasTemporales(HttpSession session) {
        Object historial = session.getAttribute("historialIaTemporalApi");
        if (historial instanceof List<?>) {
            return (List<ConsultaIa>) historial;
        }
        List<ConsultaIa> nuevoHistorial = new ArrayList<>();
        session.setAttribute("historialIaTemporalApi", nuevoHistorial);
        return nuevoHistorial;
    }

    private void agregarConsultaTemporal(HttpSession session, ConsultaIa consulta) {
        List<ConsultaIa> historial = consultasTemporales(session);
        historial.add(0, consulta);
        if (historial.size() > 8) {
            historial.remove(historial.size() - 1);
        }
    }
}
