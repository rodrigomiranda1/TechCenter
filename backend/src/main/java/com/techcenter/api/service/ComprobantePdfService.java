package com.techcenter.api.service;

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
import com.techcenter.api.model.Comprobante;
import com.techcenter.api.model.PedidosDetalle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ComprobantePdfService {

    private final String nombreComercial;
    private final String ruc;
    private final String direccion;

    public ComprobantePdfService(@Value("${techcenter.negocio.nombre:TechCenter Peru SAC}") String nombreComercial,
                                 @Value("${techcenter.negocio.ruc:20609876543}") String ruc,
                                 @Value("${techcenter.negocio.direccion:Av. Javier Prado Este 4200, Lima}") String direccion) {
        this.nombreComercial = nombreComercial;
        this.ruc = ruc;
        this.direccion = direccion;
    }

    public byte[] generar(Comprobante comprobante, List<PedidosDetalle> detalles) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 36, 36, 34, 34);
            PdfWriter.getInstance(document, out);
            document.open();

            Font title = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, new Color(33, 37, 41));
            Font text = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(71, 85, 105));
            Font bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, new Color(33, 37, 41));
            Font white = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
            Color brandColor = new Color(37, 99, 235); // Azul corporativo para TechCenter

            PdfPTable header = new PdfPTable(new float[] { 2.2f, 1f });
            header.setWidthPercentage(100);
            PdfPCell brand = cellSinBorde();
            brand.addElement(new Paragraph(nombreComercial, title));
            brand.addElement(new Paragraph("RUC " + ruc, text));
            brand.addElement(new Paragraph(direccion, text));
            header.addCell(brand);

            PdfPCell box = new PdfPCell();
            box.setBorderColor(brandColor);
            box.setBorderWidth(1.4f);
            box.setPadding(12);
            box.setHorizontalAlignment(Element.ALIGN_CENTER);
            box.addElement(centrado(comprobante.getTipo(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15, brandColor)));
            box.addElement(centrado("RUC " + ruc, bold));
            box.addElement(centrado(comprobante.getSerie() + "-" + String.format("%08d", comprobante.getCorrelativo()), title));
            header.addCell(box);
            document.add(header);

            agregarEspacio(document, 14);
            PdfPTable info = new PdfPTable(new float[] { 1f, 1f });
            info.setWidthPercentage(100);
            info.addCell(bloque("Cliente",
                    comprobante.getPedido().getCliente().getNombres() + " "
                            + valor(comprobante.getPedido().getCliente().getApellidos())
                            + "\nDocumento: " + valor(comprobante.getPedido().getCliente().getNumerodocumento())
                            + "\nDirección: " + valor(comprobante.getPedido().getDireccionentrega()),
                    text, brandColor));
            info.addCell(bloque("Operación",
                    "Pedido: #" + comprobante.getPedido().getIdpedido()
                            + "\nFecha: " + comprobante.getFechaemision().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                            + "\nPago: Mercado Pago sandbox\nEstado: " + comprobante.getEstado(),
                    text, brandColor));
            document.add(info);

            agregarEspacio(document, 14);
            PdfPTable table = new PdfPTable(new float[] { 3.4f, .8f, 1f, 1f });
            table.setWidthPercentage(100);
            table.addCell(headerCell("Producto", white, brandColor));
            table.addCell(headerCell("Cant.", white, brandColor));
            table.addCell(headerCell("P. Unit.", white, brandColor));
            table.addCell(headerCell("Total", white, brandColor));

            for (PedidosDetalle detalle : detalles) {
                table.addCell(bodyCell(detalle.getProducto().getNombre(), text, Element.ALIGN_LEFT));
                table.addCell(bodyCell(String.valueOf(detalle.getCantidad()), text, Element.ALIGN_CENTER));
                table.addCell(bodyCell("S/ " + detalle.getPreciounitario(), text, Element.ALIGN_RIGHT));
                table.addCell(bodyCell("S/ " + detalle.getTotallinea(), bold, Element.ALIGN_RIGHT));
            }
            document.add(table);

            agregarEspacio(document, 12);
            PdfPTable totals = new PdfPTable(new float[] { 1.2f, 1.8f });
            totals.setWidthPercentage(47);
            totals.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totals.addCell(totalCell("Subtotal", text));
            totals.addCell(totalCell("S/ " + comprobante.getSubtotal(), bold));
            totals.addCell(totalCell("IGV 18%", text));
            totals.addCell(totalCell("S/ " + comprobante.getIgv(), bold));
            totals.addCell(totalCell("Total", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, brandColor)));
            totals.addCell(totalCell("S/ " + comprobante.getTotal(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, brandColor)));
            document.add(totals);

            agregarEspacio(document, 18);
            Paragraph footer = new Paragraph(
                    "Este documento es una boleta/factura académica generada por TechCenter. No tiene validez tributaria SUNAT.",
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
        Paragraph paragraph = new Paragraph(value, font);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        return paragraph;
    }

    private void agregarEspacio(Document document, int size) throws Exception {
        document.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, size)));
    }

    private String valor(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}