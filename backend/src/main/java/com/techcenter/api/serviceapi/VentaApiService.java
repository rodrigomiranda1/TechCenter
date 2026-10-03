package com.techcenter.api.serviceapi;

import com.techcenter.api.dto.DetalleVentaDTO;
import com.techcenter.api.dto.VentaDTO;
import com.techcenter.api.model.DetalleVenta;
import com.techcenter.api.model.Venta;
import com.techcenter.api.repository.DetalleVentaRepository;
import com.techcenter.api.repository.VentaRepository;
import com.techcenter.api.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VentaApiService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final VentaService ventaService;

    public List<VentaDTO> listar() {
        return ventaRepository.findAll().stream().map(this::convertirDTO).toList();
    }

    public VentaDTO buscarPorId(Long id) {
        Venta venta = ventaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));

        return convertirDTO(venta);
    }

    public Page<VentaDTO> listarPaginado(int pagina) {
        return ventaService.listarPaginado(pagina).map(this::convertirDTO);
    }

    private VentaDTO convertirDTO(Venta venta) {

        VentaDTO dto = new VentaDTO();

        dto.setIdVenta(venta.getIdventa());

        if (venta.getCliente() != null) {
            dto.setIdCliente(venta.getCliente().getIdcliente());
        }

        if (venta.getUsuario() != null) {
            dto.setIdUsuario(venta.getUsuario().getIdusuario());
        }

        dto.setFechaVenta(venta.getFechaventa());
        dto.setTipoComprobante(venta.getTipocomprobante());
        dto.setSerie(venta.getSerie());
        dto.setCorrelativo(venta.getCorrelativo());
        dto.setTipoEntrega(venta.getTipoentrega());
        dto.setEstado(venta.getEstado());
        dto.setObservacion(venta.getObservacion());
        dto.setMetodoPago(venta.getMetodopago());
        dto.setSubtotal(venta.getSubtotal());
        dto.setIgv(venta.getIgv());
        dto.setTotal(venta.getTotal());

        List<DetalleVentaDTO> detalles = detalleVentaRepository.findByVenta_Idventa(venta.getIdventa()).stream().map(this::convertirDetalleDTO)
                .toList();

        dto.setDetalles(detalles);

        return dto;
    }

    public void registrar(VentaDTO dto) throws Exception {

        String detalleJson = convertirDetallesParaVentaService(dto.getDetalles());
        String correlativoTexto = ventaService.generarCorrelativo(dto.getSerie());
        Integer correlativo = Integer.valueOf(correlativoTexto);

        ventaService.registrar(
                dto.getIdCliente(),
                dto.getIdUsuario(),
                dto.getTipoComprobante(),
                dto.getSerie(),
                correlativo,
                dto.getTipoEntrega(),
                dto.getObservacion(),
                dto.getSubtotal(),
                dto.getIgv(),
                dto.getTotal(),
                dto.getMetodoPago(),
                detalleJson
        );
    }

    private String convertirDetallesParaVentaService(
            List<DetalleVentaDTO> detalles) throws Exception {

        List<java.util.Map<String, Object>> detallesConvertidos =
                detalles.stream()
                        .map(detalle -> {
                            java.util.Map<String, Object> item =
                                    new java.util.HashMap<>();

                            item.put("id", detalle.getIdProducto());
                            item.put("cantidad", detalle.getCantidad());
                            item.put("precio", detalle.getPrecioUnitario());

                            return item;
                        })
                        .toList();

        ObjectMapper mapper = new ObjectMapper();

        return mapper.writeValueAsString(detallesConvertidos);
    }

    private DetalleVentaDTO convertirDetalleDTO(DetalleVenta detalle) {

        DetalleVentaDTO dto = new DetalleVentaDTO();
        dto.setIdProducto(detalle.getProducto().getIdproducto());
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPreciounitario());

        return dto;
    }


}
