package com.fundicion.lara.service;

import com.fundicion.lara.dto.ProductDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.awt.Color;

@Slf4j
@Service("productExcelService")
public class ProductExcelService {

    private static final int START_COL = 1; // Columna B
    private static final int TABLE_COLS = 4;
    private static final int MIN_WIDTH_250PX = 36 * 256; // ~250px

    private static final int MIN_WIDTH_180PX = 36 * 180; // ~250px


    private static final float ROW_HEIGHT_30PX = 22.5f;
    private static final float ROW_HEIGHT_50PX = 37.5f;

    public static final String PRIMARY = "#194957"; /// #025669
    public static final String PRIMARY_LIGHT = "#D0EDF1";
    public static final String WHITE = "#FFFFFF";

    public byte[] generateExcel(List<ProductDto> products) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Map<String, List<ProductDto>> productsByClient =
                    products.stream()
                            .filter(p -> p.getClient() != null && !p.getClient().isBlank())
                            .collect(Collectors.groupingBy(p -> p.getClient().toUpperCase()));

            productsByClient.forEach((client, clientProducts) ->
                    createClientSheet(workbook, client, clientProducts)
            );

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Error generating Excel", e);
            throw new RuntimeException("Error generating Excel");
        }
    }

    private void createClientSheet(Workbook workbook, String client, List<ProductDto> products) {
        Sheet sheet = workbook.createSheet(client);
        sheet.setDisplayGridlines(false);
        sheet.setPrintGridlines(false);
        XSSFWorkbook xssfWorkbook = (XSSFWorkbook) workbook;

        int rowIndex = 0;

        // ===== Styles =====
        CellStyle companyStyle = createArialStyle(workbook, 11, true, HorizontalAlignment.CENTER, null);
        CellStyle subtitleStyle = createArialStyle(workbook, 11, false, HorizontalAlignment.CENTER, null);

        CellStyle titleStyle = createArialStyle(workbook, 14, true, HorizontalAlignment.CENTER, WHITE);
        titleStyle.setFillForegroundColor(fromHex(PRIMARY, xssfWorkbook));
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle headerStyle = createHeaderStyle(workbook);

        CellStyle bodyStyle = createBodyStyle(workbook, false);
        CellStyle bodyZebraStyle = createBodyStyle(workbook, true);

        CellStyle productIdStyle = createBodyStyle(workbook, false);
        CellStyle productIdZebraStyle = createBodyStyle(workbook, true);

        CellStyle currencyStyle = createCurrencyStyle(workbook, false);
        CellStyle currencyZebraStyle = createCurrencyStyle(workbook, true);

        CellStyle clientStyle = createArialStyle(workbook, 24, true, HorizontalAlignment.CENTER, PRIMARY);
        CellStyle infoStyle = createArialStyle(workbook, 12, false, HorizontalAlignment.CENTER, null);
        CellStyle updateStyle = createArialStyle(workbook, 11, false, HorizontalAlignment.RIGHT, null);

        // ===== Header content =====
        rowIndex = createMergedRow(sheet, rowIndex,
                "TALLER DE FUNDICIÓN ARTESANAL \"LARA MORALES\"", companyStyle);

        rowIndex = createMergedRow(sheet, rowIndex,
                "Número de teléfono: 5565176020  o 5536699034", subtitleStyle);

        rowIndex = createMergedRow(sheet, rowIndex,
                "Dirección: Oriente 32 Mz 232 Lt 18, Col. Guadalupana 2da sección. CP 56616", subtitleStyle);

        rowIndex++;

        rowIndex = createMergedRow(sheet, rowIndex,
                "LISTA DE PRECIOS DE PRODUCTOS", titleStyle, ROW_HEIGHT_50PX);

        Row clientRow = sheet.createRow(rowIndex++);
        Cell clientCell = clientRow.createCell(START_COL + 3);
        clientCell.setCellValue(client);
        clientCell.setCellStyle(clientStyle);

        rowIndex = createMergedRow(sheet, rowIndex,
                "Para preguntar por artículos que no figuran en la lista, llame al número de teléfono", infoStyle);

        Row mailRow = sheet.createRow(rowIndex++);
        Cell mailCell = mailRow.createCell(START_COL);
        mailCell.setCellStyle(infoStyle);

        String text = "* o mándame un correo a: ";
        String email = "ericslaram@gmail.com";

        RichTextString richText = workbook.getCreationHelper()
                .createRichTextString(text + email);

        // Aplica negrita SOLO al correo
                Font boldFont = createBoldFont(workbook);
                richText.applyFont(text.length(), text.length() + email.length(), boldFont);

                mailCell.setCellValue(richText);

        // Merge igual que antes
                sheet.addMergedRegion(new CellRangeAddress(
                        mailRow.getRowNum(), mailRow.getRowNum(),
                        START_COL, START_COL + TABLE_COLS - 1
                ));


        Row updateRow = sheet.createRow(rowIndex++);
        Cell updateCell = updateRow.createCell(START_COL);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        updateCell.setCellValue("Última actualización: " + LocalDate.now().format(formatter));
        updateCell.setCellStyle(updateStyle);

        sheet.addMergedRegion(new CellRangeAddress(
                updateRow.getRowNum(), updateRow.getRowNum(),
                START_COL, START_COL + TABLE_COLS - 1
        ));

        rowIndex++;

        // ===== Table Header =====
        Row headerRow = sheet.createRow(rowIndex++);
        headerRow.setHeightInPoints(ROW_HEIGHT_30PX);

        String[] headers = {
                "Número de producto", "Nombre", "Descripción", "Precio Unitario"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(START_COL + i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        sheet.setAutoFilter(new CellRangeAddress(
                headerRow.getRowNum(), headerRow.getRowNum(),
                START_COL, START_COL + TABLE_COLS - 1
        ));

        // ===== Data =====
        for (int i = 0; i < products.size(); i++) {
            ProductDto p = products.get(i);
            Row row = sheet.createRow(rowIndex++);
            row.setHeightInPoints(ROW_HEIGHT_30PX);

            boolean zebra = i % 2 != 0;

            createCell(row, START_COL,
                    p.getProductId(), zebra ? productIdZebraStyle : productIdStyle);

            createCell(row, START_COL + 1,
                    p.getName(), zebra ? bodyZebraStyle : bodyStyle);

            createCell(row, START_COL + 2,
                    p.getDescription(), zebra ? bodyZebraStyle : bodyStyle);

            Cell priceCell = row.createCell(START_COL + 3);
            priceCell.setCellValue(p.getSellingPrice().doubleValue());
            priceCell.setCellStyle(zebra ? currencyZebraStyle : currencyStyle);
        }

        // ===== Column widths =====
        for (int i = 0; i < TABLE_COLS; i++) {
            int col = START_COL + i;
            sheet.autoSizeColumn(col);

            sheet.setColumnWidth(col, Math.max(sheet.getColumnWidth(col), i >= 1 ? MIN_WIDTH_250PX : MIN_WIDTH_180PX));
        }
    }

    // ===== Helpers =====

    private int createMergedRow(Sheet sheet, int rowIndex, String value, CellStyle style) {
        Row row = sheet.createRow(rowIndex);
        Cell cell = row.createCell(START_COL);
        cell.setCellValue(value);
        cell.setCellStyle(style);

        sheet.addMergedRegion(new CellRangeAddress(
                rowIndex, rowIndex,
                START_COL, START_COL + TABLE_COLS - 1
        ));
        return rowIndex + 1;
    }

    private int createMergedRow(Sheet sheet, int rowIndex, String value, CellStyle style, float height) {
        Row row = sheet.createRow(rowIndex);
        row.setHeightInPoints(height);
        Cell cell = row.createCell(START_COL);
        cell.setCellValue(value);
        cell.setCellStyle(style);

        sheet.addMergedRegion(new CellRangeAddress(
                rowIndex, rowIndex,
                START_COL, START_COL + TABLE_COLS - 1
        ));
        return rowIndex + 1;
    }

    private void createCell(Row row, int col, Object value, CellStyle style) {
        Cell cell = row.createCell(col);
        if (value != null) {
            if (value instanceof Integer i) cell.setCellValue(i);
            else if (value instanceof BigDecimal bd) cell.setCellValue(bd.doubleValue());
            else cell.setCellValue(value.toString());
        }
        cell.setCellStyle(style);
    }

    // ===== Styles =====

    private CellStyle createArialStyle(
            Workbook wb, int size, boolean bold,
            HorizontalAlignment align, String hexColor
    ) {
        Font font = wb.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) size);
        font.setBold(bold);
        // 👉 Color HEX solo si es XSSFWorkbook
        if (hexColor != null && wb instanceof XSSFWorkbook xssfWorkbook) {
            XSSFFont xssfFont = (XSSFFont) font;
            XSSFColor xssfColor = new XSSFColor(
                    java.awt.Color.decode(hexColor),
                    xssfWorkbook.getStylesSource().getIndexedColors()
            );
            xssfFont.setColor(xssfColor);
        }

        CellStyle style = wb.createCellStyle();
        style.setFont(font);
        style.setAlignment(align);
        return style;
    }

    private CellStyle createHeaderStyle(Workbook wb) {
        XSSFWorkbook xssfWorkbook = (XSSFWorkbook) wb;
        Font font = wb.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());

        CellStyle style = wb.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(fromHex(PRIMARY, xssfWorkbook));

        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        applyBorders(style);
        return style;
    }

    private CellStyle createBodyStyle(Workbook wb, boolean zebra) {
        Font font = wb.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 12);

        CellStyle style = wb.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        if (zebra) {
            XSSFWorkbook xssfWorkbook = (XSSFWorkbook) wb;
            style.setFillForegroundColor(fromHex(PRIMARY_LIGHT, xssfWorkbook));
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }

        applyBorders(style);
        return style;
    }

    private CellStyle createCurrencyStyle(Workbook wb, boolean zebra) {
        CellStyle style = createBodyStyle(wb, zebra);
        DataFormat df = wb.createDataFormat();
        style.setDataFormat(df.getFormat("$#,##0.00"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private void applyBorders(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }


    private  XSSFColor fromHex(String hex, XSSFWorkbook wb) {
        Color color = Color.decode(hex);
        return new XSSFColor(color, wb.getStylesSource().getIndexedColors());
    }

    private Font createBoldFont(Workbook wb) {
        Font font = wb.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        return font;
    }
}
