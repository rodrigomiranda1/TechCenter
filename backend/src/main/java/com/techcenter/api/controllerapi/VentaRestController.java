package com.techcenter.api.controllerapi;

import com.techcenter.api.dto.*;
import com.techcenter.api.model.Cliente;
import com.techcenter.api.model.DetalleVenta;
import com.techcenter.api.model.Venta;
import com.techcenter.api.repository.ClienteRepository;
import com.techcenter.api.repository.DetalleVentaRepository;
import com.techcenter.api.repository.VentaRepository;
import com.techcenter.api.service.VentaService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaRestController {

    private final VentaService ventaService;
    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ClienteRepository clienteRepository;

    @GetMapping("/clientes/buscar")
    public ResponseEntity<?> buscarCliente(@RequestParam String dni, HttpSession session) {
        if (!esEmpleado(session)) return sinPermiso();

        Cliente cliente = clienteRepository.findByDni(dni).orElse(null);
        if (cliente == null) {
            return ResponseEntity.ok(new ClienteBusquedaDTO(false, null, dni, null, null, null, null));
        }
        return ResponseEntity.ok(new ClienteBusquedaDTO(true, cliente.getIdcliente(), cliente.getDni(),
                cliente.getNombres(), cliente.getApellidos(), cliente.getTelefono(), cliente.getDireccion()));
    }

    @PostMapping("/clientes")
    public ResponseEntity<?> crearClienteRapido(@RequestBody ClienteRapidoDTO dto, HttpSession session) {
        if (!esEmpleado(session)) return sinPermiso();

        Cliente cliente = new Cliente();
        cliente.setDni(dto.getDni());
        cliente.setNombres(dto.getNombres());
        cliente.setApellidos(dto.getApellidos());
        cliente.setTelefono(dto.getTelefono());
        cliente.setDireccion(dto.getDireccion());
        cliente.setFecharegistro(LocalDateTime.now());
        clienteRepository.save(cliente);

        return new ResponseEntity<>(new ClienteBusquedaDTO(true, cliente.getIdcliente(), cliente.getDni(),
                cliente.getNombres(), cliente.getApellidos(), cliente.getTelefono(), cliente.getDireccion()), HttpStatus.CREATED);
    }

    @GetMapping("/correlativo")
    public ResponseEntity<String> correlativo(@RequestParam String serie, HttpSession session) {
        if (!esEmpleado(session)) return new ResponseEntity<>("", HttpStatus.FORBIDDEN);
        return ResponseEntity.ok(ventaService.generarCorrelativo(serie));
    }

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody VentaRegistroDTO dto, HttpSession session) {
        if (!esEmpleado(session)) return sinPermiso();

        try {
            tools.jackson.databind.ObjectMapper mapper = new tools.jackson.databind.ObjectMapper();
            List<Map<String, Object>> detalleParaJson = dto.getDetalle().stream().map(item -> {
                Map<String, Object> m = new HashMap<>();
                m.put("id", item.getId());
                m.put("cantidad", item.getCantidad());
                m.put("precio", item.getPrecio());
                return m;
            }).toList();
            String detalleJson = mapper.writeValueAsString(detalleParaJson);

            ventaService.registrar(dto.getIdCliente(), dto.getIdUsuario(), dto.getTipoComprobante(), dto.getSerie(),
                    dto.getCorrelativo(), dto.getTipoEntrega(), dto.getObservacion(), dto.getSubtotal(), dto.getIgv(),
                    dto.getTotal(), dto.getMetodoPago(), detalleJson);

            return new ResponseEntity<>(Map.of("mensaje", "Venta registrada correctamente"), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("mensaje", e.getMessage() != null ? e.getMessage() : "Error al registrar la venta"), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/listado")
    public ResponseEntity<?> listado(@RequestParam(defaultValue = "0") int page, HttpSession session) {
        if (!esEmpleado(session)) return sinPermiso();

        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");

        Page<Venta> pagina = ventaService.listarPaginado(page);

        List<VentaListadoItemDTO> contenido = pagina.getContent().stream().map(v -> new VentaListadoItemDTO(
                v.getIdventa(), v.getSerie(), v.getCorrelativo(),
                v.getCliente() != null ? v.getCliente().getNombres() + " " + (v.getCliente().getApellidos() != null ? v.getCliente().getApellidos() : "") : "-",
                v.getCliente() != null ? v.getCliente().getDni() : "-",
                v.getFechaventa() != null ? v.getFechaventa().format(formatoFecha) : "",
                v.getFechaventa() != null ? v.getFechaventa().format(formatoHora) : "",
                v.getMetodopago(), v.getTotal(), v.getEstado()
        )).toList();

        VentaPaginaDTO resultado = new VentaPaginaDTO(contenido, pagina.getNumber(), pagina.getTotalPages(),
                pagina.getTotalElements(), pagina.isFirst(), pagina.isLast());

        return ResponseEntity.ok(Map.of("pagina", resultado));
    }

    @GetMapping("/comprobante/{id}")
    public ResponseEntity<?> comprobante(@PathVariable Long id, HttpSession session) {
        if (!esEmpleado(session)) return sinPermiso();

        Venta venta = ventaRepository.findById(id).orElse(null);
        if (venta == null) {
            return new ResponseEntity<>(Map.of("mensaje", "Venta no encontrada"), HttpStatus.NOT_FOUND);
        }

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        List<DetalleVenta> detalles = detalleVentaRepository.findByVenta_Idventa(id);

        List<VentaComprobanteItemDTO> items = detalles.stream().map(d -> new VentaComprobanteItemDTO(
                d.getProducto().getNombre(), d.getCantidad(), d.getPreciounitario(), d.getImporte()
        )).toList();

        VentaComprobanteDTO dto = new VentaComprobanteDTO(
                venta.getIdventa(), venta.getTipocomprobante(), venta.getSerie(), venta.getCorrelativo(),
                venta.getFechaventa() != null ? venta.getFechaventa().format(formato) : "",
                venta.getCliente() != null ? venta.getCliente().getNombres() + " " + (venta.getCliente().getApellidos() != null ? venta.getCliente().getApellidos() : "") : "-",
                venta.getCliente() != null ? venta.getCliente().getDni() : "-",
                venta.getCliente() != null ? venta.getCliente().getDireccion() : "-",
                venta.getUsuario() != null ? venta.getUsuario().getUsername() : "-",
                venta.getMetodopago(), venta.getEstado(), venta.getTipoentrega(), venta.getObservacion(),
                venta.getSubtotal(), venta.getIgv(), venta.getTotal(), items
        );

        return ResponseEntity.ok(Map.of("comprobante", dto));
    }

    @SuppressWarnings("unchecked")
    private boolean esEmpleado(HttpSession session) {
        Object roles = session.getAttribute("roles");
        return roles instanceof Set<?> r && r.stream().anyMatch(rol ->
                "EMPLEADO".equals(String.valueOf(rol)) || "ADMIN".equals(String.valueOf(rol)));
    }

    private ResponseEntity<Map<String, Object>> sinPermiso() {
        return new ResponseEntity<>(Map.of("mensaje", "Acceso solo para empleados"), HttpStatus.FORBIDDEN);
    }
}