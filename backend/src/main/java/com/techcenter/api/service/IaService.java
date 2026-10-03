package com.techcenter.api.service;

import com.techcenter.api.model.Cliente;
import com.techcenter.api.model.ConsultaIa;
import com.techcenter.api.model.Producto;
import com.techcenter.api.repository.ConsultaIaRepository;
import com.techcenter.api.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class IaService {

    private final ProductoRepository productoRepository;
    private final ConsultaIaRepository consultaIaRepository;

    public ConsultaIa responder(Cliente cliente, String pregunta, boolean guardarHistorial) {
        List<Producto> productos = buscarProductosRelevantes(pregunta);
        String respuesta = construirRespuesta(pregunta, productos);

        ConsultaIa consulta = new ConsultaIa();
        consulta.setCliente(cliente);
        consulta.setPregunta(pregunta);
        consulta.setRespuesta(respuesta);
        consulta.setFechaconsulta(LocalDateTime.now());
        if (guardarHistorial && cliente != null) {
            consultaIaRepository.save(consulta);
        }

        return consulta;
    }

    private List<Producto> buscarProductosRelevantes(String pregunta) {
        List<String> palabras = tokens(pregunta);

        if (palabras.isEmpty()) {
            return List.of();
        }
        return productoRepository.findByActivoTrue().stream()
                .map(producto -> new ProductoPuntuado(producto, puntuar(producto, palabras)))
                .filter(resultado -> resultado.puntaje() > 0)
                .sorted(Comparator.comparingInt(ProductoPuntuado::puntaje).reversed()
                        .thenComparing(resultado -> resultado.producto().getNombre()))
                .limit(5)
                .map(ProductoPuntuado::producto)
                .toList();
    }

    private int puntuar(Producto producto, List<String> palabras) {
        String nombre = normalizar(producto.getNombre());
        String descripcion = normalizar(producto.getDescripcion());
        String categoria = normalizar(producto.getCategoria() == null ? "" : producto.getCategoria().getNombre());
        String marca = normalizar(producto.getMarca() == null ? "" : producto.getMarca().getNombre());
        int puntaje = 0;
        for (String palabra : palabras) {
            if (nombre.contains(palabra)) {
                puntaje += 6;
            }
            if (categoria.contains(palabra)) {
                puntaje += 4;
            }
            if (descripcion.contains(palabra)) {
                puntaje += 3;
            }
            if (marca.contains(palabra)) {
                puntaje += 2;
            }
        }
        return puntaje;
    }

    private String construirRespuesta(String pregunta, List<Producto> productos) {
        if (productos.isEmpty()) {
            return "No encontre una coincidencia clara en el catalogo activo. Prueba con una necesidad mas concreta, por ejemplo: Celulares, Laptops, Procesadores, Tarjetas Gráficas o Placas Madre.";
        }

        StringBuilder respuesta = new StringBuilder();
        respuesta.append("Analizando tu consulta sobre \"").append(pregunta).append("\", estas opciones del catalogo encajan mejor: ");
        for (int i = 0; i < productos.size(); i++) {
            Producto producto = productos.get(i);
            if (i > 0) {
                respuesta.append("; ");
            }
            respuesta.append(producto.getNombre()).append(" porque pertenece a ")
                    .append(producto.getCategoria() == null ? "catalogo TechMax" : producto.getCategoria().getNombre())
                    .append(", cuesta S/ ").append(producto.getPrecio())
                    .append(" y tiene stock ").append(producto.getStockactual());
        }
        respuesta.append(". Mi sugerencia es tener una proforma o una lista de los productos que vas a necesitar comprar.");
        return respuesta.toString();
    }

    private List<String> tokens(String texto) {
        Set<String> ruido = Set.of("para", "como", "quiero", "necesito", "hacer", "con", "una", "unos", "las",
                "los", "del", "por", "que", "cual", "cuales", "algo", "este", "esta", "sirve", "comprar");
        return Arrays.stream(normalizar(texto).split("\\s+"))
                .map(String::trim)
                .filter(palabra -> palabra.length() > 2)
                .filter(palabra -> !ruido.contains(palabra))
                .distinct()
                .toList();
    }

    private String normalizar(String texto) {
        String valor = texto == null ? "" : texto.toLowerCase(Locale.ROOT);
        return Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private record ProductoPuntuado(Producto producto, int puntaje) {
    }
}
