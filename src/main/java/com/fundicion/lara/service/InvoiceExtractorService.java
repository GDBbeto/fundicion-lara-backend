package com.fundicion.lara.service;

import com.fundicion.lara.dto.InvoiceDataDto;
import com.fundicion.lara.exception.BadRequestException;
import com.fundicion.lara.exception.InternalException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class InvoiceExtractorService {
    public InvoiceDataDto findExtractInvoiceData(MultipartFile file) {
        try {
            if (!"application/pdf".equals(file.getContentType())) {
                throw new BadRequestException( "El archivo debe ser un PDF");
            }
            try (PDDocument document = PDDocument.load(file.getInputStream())) {
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(document);

               return extractInvoiceData(text);
            }
        } catch (IOException e) {
            throw new InternalException("Error al procesar el archivo: " + e.getMessage());
        } catch (Exception e) {
            throw new InternalException("Error inesperado: " + e.getMessage());
        }
    }

    private InvoiceDataDto extractInvoiceData(String text) {
        String normalizedText = text.replaceAll("\\s+", " ");

        String folio = extractFolioFiscal(normalizedText);
        BigDecimal total = extractTotal(normalizedText);
        return InvoiceDataDto.builder()
                .invoiceNumber(folio)
                .amount(total)
                .build();

    }

    private String extractFolioFiscal(String text) {
        Pattern pattern = Pattern.compile("Folio fiscal:\\s*([A-Z0-9-]+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }
    private BigDecimal extractTotal(String text) {
        Pattern pattern = Pattern.compile("Total\\s*\\$\\s*([0-9,]+\\.\\d{2})");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            try {
                String totalStr = matcher.group(1).trim().replace(",", "");
                return new BigDecimal(totalStr);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
