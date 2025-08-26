package com.fundicion.lara.service;

import com.fundicion.lara.dto.InvoiceDataDto;
import com.fundicion.lara.dto.InvoicePatternConfig;
import com.fundicion.lara.exception.BadRequestException;
import com.fundicion.lara.exception.ConflictException;
import com.fundicion.lara.exception.InternalException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.fundicion.lara.commons.constants.Constants.*;

@Service
public class InvoiceExtractorService {
    private final Map<String, InvoicePatternConfig> rfcPatterns = new HashMap<>();

    public InvoiceExtractorService() {

        rfcPatterns.put(LARA_ISSUER_RFC, InvoicePatternConfig.builder()
                .folioPattern(LARA_FOLIO_PATTERN)
                .totalPattern(LARA_TOTAL_PATTERN)
                .groupTotal(GROUP_TOTAL_DEFAULT)
                .build());

        rfcPatterns.put(JACQUELINE_ISSUER_RFC, InvoicePatternConfig.builder()
                .folioPattern(JACQUELINE_FOLIO_PATTERN)
                .totalPattern(JACQUELINE_TOTAL_PATTERN)
                .groupTotal(GROUP_TOTAL_DEFAULT)
                .build());

        rfcPatterns.put(GLOBAL_GAS_ISSUER_RFC, InvoicePatternConfig.builder()
                .folioPattern(GLOBAL_GAS_FOLIO_PATTERN)
                .totalPattern(GLOBAL_GAS_TOTAL_PATTERN)
                .groupTotal(GLOBAL_GAS_GROUP_TOTAL)
                .build());

        rfcPatterns.put(OSCAR_ISSUER_RFC, InvoicePatternConfig.builder()
                .folioPattern(OSCAR_FOLIO_PATTERN)
                .totalPattern(OSCAR_TOTAL_PATTERN)
                .groupTotal(GROUP_TOTAL_DEFAULT)
                .build());
    }

    public InvoiceDataDto findExtractInvoiceData(MultipartFile file) {
        try {
            if (!"application/pdf".equals(file.getContentType())) {
                throw new BadRequestException("El archivo debe ser un PDF");
            }
            try (PDDocument document = PDDocument.load(file.getInputStream())) {
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(document);

                return extractInvoiceData(text);
            }
        } catch (IOException e) {
            throw new InternalException(ERROR_INVOICE_EXTRACT);
        } catch (Exception e) {
            if (e instanceof BadRequestException) {
                throw new ConflictException(ERROR_INVOICE_EXTRACT);
            }
            throw new InternalException(ERROR_INVOICE_EXTRACT);
        }
    }

    private InvoiceDataDto extractInvoiceData(String text) {
        String normalizedText = text.replaceAll("\\s+", " ");
        System.out.println(normalizedText);
        var rfc = extractIssuerRfcPattern(normalizedText);
        InvoicePatternConfig config = rfcPatterns.get(rfc);
        if (config == null) {
            config = rfcPatterns.get(ISSUER_RFC_DEFAULT);
            rfc = extractIssuerRfc(normalizedText);
        }

        String folio = extractFolioFiscal(normalizedText, config.getFolioPattern());
        BigDecimal total = extractTotal(normalizedText, config.getTotalPattern(), config.getGroupTotal());

        if (rfc == null && folio == null && total == null) {
            throw new ConflictException(ERROR_INVOICE_EXTRACT);
        }

        return InvoiceDataDto.builder()
                .invoiceNumber(folio)
                .amount(total)
                .issuerRfc(rfc)
                .build();
    }

    private String extractIssuerRfcPattern(String text) {
        for (Map.Entry<String, InvoicePatternConfig> entry : rfcPatterns.entrySet()) {
            String rfc = entry.getKey();
            if (text.matches(".*\\b" + rfc + "\\b.*")) { // Usar expresión regular para buscar el RFC
                return rfc;
            }
        }
        return null;
    }

    private String extractFolioFiscal(String text, String folioPattern) {
        Pattern pattern = Pattern.compile(folioPattern);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    private BigDecimal extractTotal(String text, String totalPattern, int group) {
        Pattern pattern = Pattern.compile(totalPattern);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            try {
                String totalStr = matcher.group(group).trim().replace(",", "");
                return new BigDecimal(totalStr);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private String extractIssuerRfc(String text) {
        Pattern pattern = Pattern.compile(RFC_PATTERN_DEFAULT);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }
}
