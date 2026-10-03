package com.techcenter.api.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.techcenter.api.model.DetalleVenta;
import com.techcenter.api.model.Venta;

@Service
public class VentaPdfService {

    private final String nombreComercial;
    private final String ruc;
    private final String direccion;

    public VentaPdfService(
            @Value("${techcenter.negocio.nombre:TechCenter Peru SAC}") String nombreComercial,
            @Value("${techcenter.negocio.ruc:20609876543}") String ruc,
            @Value("${techcenter.negocio.direccion:Av. Javier Prado Este 4200, Lima}") String direccion) {

        this.nombreComercial = nombreComercial;
        this.ruc = ruc;
        this.direccion = direccion;
    }

    public byte[] generar(Venta venta, List<DetalleVenta> detalles) {

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 36, 36, 34, 34);
            PdfWriter.getInstance(document, out);
            document.open();

            Font title = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, new Color(33, 37, 41));
            Font text = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(71, 85, 105));
            Font bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, new Color(33, 37, 41));
            Font white = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
            Color brandColor = new Color(37, 99, 235); // Azul primario TechCenter

            PdfPTable header = new PdfPTable(new float[] { 2.2f, 1f });
            header.setWidthPercentage(100);

            PdfPCell brand = cellSinBorde();
            brand.addElement(new Paragraph(nombreComercial, title));
            brand.addElement(new Paragraph("RUC " + ruc, text));
            brand.addElement(new Paragraph(direccion, text));
            brand.addElement(new Paragraph("Sistema de Ventas TechCenter", text));

            header.addCell(brand);

            PdfPCell box = new PdfPCell();
            box.setBorderColor(brandColor);
            box.setBorderWidth(1.4f);
            box.setPadding(12);
            box.setHorizontalAlignment(Element.ALIGN_CENTER);

            box.addElement(centrado(venta.getTipocomprobante(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15, brandColor)));
            box.addElement(centrado("RUC " + ruc, bold));
            box.addElement(centrado(venta.getSerie() + "-" + String.format("%08d", venta.getCorrelativo()), title));
            header.addCell(box);
            document.add(header);
            agregarEspacio(document, 14);

            PdfPTable info = new PdfPTable(new float[] { 1f, 1f });
            info.setWidthPercentage(100);
            info.addCell(bloque("Cliente", venta.getCliente().getNombres()
                    + " "
                    + valor(venta.getCliente().getApellidos())
                    + "\nDocumento: "
                    + valor(venta.getCliente().getDni())
                    + "\nEntrega: "
                    + valor(venta.getTipoentrega()), text, brandColor));
            info.addCell(bloque("Venta", "Venta #" + venta.getIdventa()
                    + "\nFecha: "
                    + venta.getFechaventa().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                    + "\nPago: "
                    + valor(venta.getMetodopago())
                    + "\nEstado: "
                    + valor(venta.getEstado()), text, brandColor));
            document.add(info);

            agregarEspacio(document, 14);

            PdfPTable table = new PdfPTable(new float[] { 3.4f, .8f, 1f, 1f });
            table.setWidthPercentage(100);

            table.addCell(headerCell("Producto", white, brandColor));
            table.addCell(headerCell("Cant.", white, brandColor));
            table.addCell(headerCell("P. Unit.", white, brandColor));
            table.addCell(headerCell("Total", white, brandColor));

            for (DetalleVenta detalle : detalles) {
                table.addCell(bodyCell(detalle.getProducto().getNombre(), text, Element.ALIGN_LEFT));
                table.addCell(bodyCell(String.valueOf(detalle.getCantidad()), text, Element.ALIGN_CENTER));
                table.addCell(bodyCell("S/ " + detalle.getPreciounitario(), text, Element.ALIGN_RIGHT));
                table.addCell(bodyCell("S/ " + (detalle.getPreciounitario().doubleValue() * detalle.getCantidad()), bold, Element.ALIGN_RIGHT));
            }

            document.add(table);
            agregarEspacio(document, 12);

            PdfPTable totals = new PdfPTable(new float[] { 2.5f, 1f });
            totals.setWidthPercentage(42);
            totals.setHorizontalAlignment(Element.ALIGN_RIGHT);

            totals.addCell(totalCell("Subtotal", text));
            totals.addCell(totalCell("S/ " + venta.getSubtotal(), bold));

            totals.addCell(totalCell("IGV 18%", text));
            totals.addCell(totalCell("S/ " + venta.getIgv(), bold));

            totals.addCell(totalCell("Total", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, brandColor)));
            totals.addCell(totalCell("S/ " + venta.getTotal(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, brandColor)));

            document.add(totals);
            agregarEspacio(document, 18);

            Paragraph footer = new Paragraph("Este documento es un comprobante generado por TechCenter para el registro de ventas.",
                    FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, new Color(100, 116, 139)));

            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);
            document.close();
            return out.toByteArray();

        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo generar el PDF", ex);
        }
    }

    private PdfPCell bloque(String titulo, String contenido, Font text, Color brandColor) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(10);
        cell.setBorderColor(new Color(226, 232, 240));

        cell.addElement(new Paragraph(titulo, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, brandColor)));
        cell.addElement(new Paragraph(contenido, text));

        return cell;
    }

    private PdfPCell headerCell(String value, Font font, Color background) {
        PdfPCell cell = new PdfPCell(new Phrase(value, font));
        cell.setBackgroundColor(background);
        cell.setPadding(8);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.NO_BORDER);

        return cell;
    }

    private PdfPCell bodyCell(String value, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(value, font));
        cell.setPadding(8);
        cell.setHorizontalAlignment(align);
        cell.setBorderColor(new Color(226, 232, 240));

        return cell;
    }

    private PdfPCell totalCell(String value, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(value, font));
        cell.setPadding(7);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorder(Rectangle.NO_BORDER);

        return cell;
    }

    private PdfPCell cellSinBorde() {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private Paragraph centrado(String value, Font font) {
        Paragraph p = new Paragraph(value, font);
        p.setAlignment(Element.ALIGN_CENTER);
        return p;
    }

    private void agregarEspacio(Document document, int size) throws Exception {
        document.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, size)));
    }

    private String valor(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}