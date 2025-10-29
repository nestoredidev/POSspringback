package com.api.pos_backend.shared.PDF;

import com.api.pos_backend.dto.SaleDTO;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
//import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PdfService {

    public byte[] generateSalePdf(SaleDTO sale) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            document.setMargins(24, 24, 36, 24);

            PdfFont regular = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

            DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("es", "PE"));
            symbols.setDecimalSeparator('.');
            symbols.setGroupingSeparator(',');
            DecimalFormat df = new DecimalFormat("#,##0.00", symbols);

            // Encabezado con logo y datos de la empresa
            Table header = new Table(UnitValue.createPercentArray(new float[]{70, 30}))
                    .useAllAvailableWidth();
            Cell left = new Cell().setBorder(Border.NO_BORDER);
            left.add(new Paragraph("LA BODEGA ICE S.A.C.")
                    .setFont(bold).setFontSize(14).setFontColor(ColorConstants.BLACK));
            left.add(new Paragraph("RUC: 12345678901\nJr. Las Palmeras 356, Ancash-Huaraz-Independecia\nTel: (01) 123-4567")
                    .setFont(regular).setFontSize(9).setFontColor(ColorConstants.DARK_GRAY));

            Cell right = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT);

            // Logo opcional desde resources \`/static/logo.png\`
            try {
                URL logoUrl = getClass().getResource("/static/logo.png");
                if (logoUrl != null) {
                    Image logo = new Image(ImageDataFactory.create(logoUrl)).setAutoScale(true);
                    logo.setMaxHeight(40);
                    logo.setMaxWidth(40);
                    right.add(logo);
                }
            } catch (Exception ignore) {}

            header.addCell(left);
            header.addCell(right);
            document.add(header);

            document.add(new Paragraph("FACTURA DE VENTA")
                    .setFont(bold).setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(6));

//            document.add(new LineSeparator().setMarginTop(6).setMarginBottom(10));

            // Datos de la venta
            Table info = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                    .useAllAvailableWidth();
            info.addCell(infoCell("Factura :", String.valueOf(sale.getId()), regular, bold));
            info.addCell(infoCell("Fecha:", String.valueOf(sale.getDate()), regular, bold));
            info.addCell(infoCell("Vendedor:", String.valueOf(sale.getUserName()), regular, bold));
            if (sale.getCouponCode() != null) {
                info.addCell(infoCell("Cupón:", sale.getCouponCode(), regular, bold));
            } else {
                info.addCell(new Cell().setBorder(Border.NO_BORDER)); // balancear filas
            }
            document.add(info);

            document.add(new Paragraph(" "));

            // Tabla de productos
            Table items = new Table(UnitValue.createPercentArray(new float[]{50f, 15f, 15f, 20f}))
                    .useAllAvailableWidth();

            items.addHeaderCell(headerCell("Producto", bold));
            items.addHeaderCell(headerCell("Cantidad", bold));
            items.addHeaderCell(headerCell("Precio Unit.", bold));
            items.addHeaderCell(headerCell("Subtotal", bold));

            if (sale.getSaleDetails() != null) {
                sale.getSaleDetails().forEach(d -> {
                    items.addCell(bodyCellLeft(d.getProductName(), regular));
                    items.addCell(bodyCellRight(String.valueOf(d.getQuantity()), regular));
                    items.addCell(bodyCellRight("S/ " + df.format(d.getPriceAtSale()), regular));
                    double sub = d.getPriceAtSale() * d.getQuantity();
                    items.addCell(bodyCellRight("S/ " + df.format(sub), regular));
                });
            }

            document.add(items);

//            document.add(new LineSeparator().setMarginTop(8).setMarginBottom(8));

            // Totales (asumiendo total con IGV incluido 18%)
            BigDecimal total = BigDecimal.valueOf(sale.getTotalAmount()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal base = total.divide(BigDecimal.valueOf(1.18), 2, RoundingMode.HALF_UP);
            BigDecimal igv = total.subtract(base);

            Table totals = new Table(UnitValue.createPercentArray(new float[]{60, 40}))
                    .setWidth(UnitValue.createPercentValue(45))
                    .setHorizontalAlignment(HorizontalAlignment.RIGHT);

            totals.addCell(totalLabelCell("Op. Gravada", regular));
            totals.addCell(totalValueCell("S/ " + df.format(base), regular));

            totals.addCell(totalLabelCell("IGV 18%", regular));
            totals.addCell(totalValueCell("S/ " + df.format(igv), regular));

            if (sale.getCouponCode() != null) {
                totals.addCell(totalLabelCell("Cupón aplicado", regular));
                totals.addCell(totalValueCell(sale.getCouponCode(), regular));
            }

            Cell totalLabel = new Cell().add(new Paragraph("TOTAL").setFont(bold))
                    .setBorder(Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY);
            Cell totalValue = new Cell().add(new Paragraph("S/ " + df.format(total)).setFont(bold))
                    .setBorder(Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY);

            totals.addCell(totalLabel);
            totals.addCell(totalValue);

            document.add(totals);

            // Pie de página
            document.add(new Paragraph("Gracias por su compra")
                    .setFont(regular)
                    .setFontSize(9)
                    .setFontColor(ColorConstants.GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(14));

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF: " + e.getMessage(), e);
        }
    }

    private Cell headerCell(String text, PdfFont bold) {
        return new Cell()
                .add(new Paragraph(text).setFont(bold).setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(ColorConstants.BLACK)
                .setTextAlignment(TextAlignment.CENTER);
    }

    private Cell bodyCellLeft(String text, PdfFont font) {
        return new Cell().add(new Paragraph(text).setFont(font))
                .setTextAlignment(TextAlignment.LEFT);
    }

    private Cell bodyCellRight(String text, PdfFont font) {
        return new Cell().add(new Paragraph(text).setFont(font))
                .setTextAlignment(TextAlignment.RIGHT);
    }

    private Cell infoCell(String label, String value, PdfFont regular, PdfFont bold) {
        Paragraph p = new Paragraph()
                .add(label == null ? "" : label).setFont(bold).setFontSize(10)
                .add(" ")
                .add(value == null ? "-" : value).setFont(regular).setFontSize(10);
        return new Cell().add(p).setBorder(Border.NO_BORDER);
    }

    private Cell totalLabelCell(String text, PdfFont font) {
        return new Cell().add(new Paragraph(text).setFont(font))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT);
    }

    private Cell totalValueCell(String text, PdfFont font) {
        return new Cell().add(new Paragraph(text).setFont(font))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT);
    }
}