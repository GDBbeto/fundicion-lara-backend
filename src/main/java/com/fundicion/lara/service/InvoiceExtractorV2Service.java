package com.fundicion.lara.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fundicion.lara.commons.emuns.TransactionType;
import com.fundicion.lara.dto.InvoiceDataDto;
import com.fundicion.lara.dto.InvoicePatternConfig;
import com.fundicion.lara.exception.BadRequestException;
import com.fundicion.lara.exception.InternalException;
import com.fundicion.lara.utils.RegexPatterns;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.tika.Tika;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.fundicion.lara.commons.constants.Constants.*;

@Slf4j
@Service
public class InvoiceExtractorV2Service {

    private final Map<String, InvoicePatternConfig> patternConfigs;

    private static final Set<String> RFC_VENTAS = Set.of(LARA_ISSUER_RFC, LARA_M_ISSUER_RFC);
    private static final String RFC_VENTA_DEFAULT = LARA_ISSUER_RFC;

    private static final InvoicePatternConfig DEFAULT_PATTERN = InvoicePatternConfig.builder()
            .folioPattern(RegexPatterns.DEFAULT_FOLIO)
            .totalPattern(RegexPatterns.DEFAULT_TOTAL)
            .groupTotal(1)
            .build();

    public InvoiceExtractorV2Service() {
        this.patternConfigs = loadPatterns();
    }

    private Map<String, InvoicePatternConfig> loadPatterns() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(
                    new ClassPathResource("invoice-patterns.json").getInputStream(),
                    new TypeReference<>() {}
            );
        } catch (IOException e) {
            log.error("Error al cargar configuraciones de patrones: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    public InvoiceDataDto extractInvoiceData(MultipartFile file, TransactionType transactionType) {
        try {
            Tika tika = new Tika();
            String mimeType = tika.detect(file.getInputStream());
            if (!"application/pdf".equals(mimeType)) {
                throw new BadRequestException("El archivo debe ser un PDF válido.");
            }

            try (PDDocument document = PDDocument.load(file.getInputStream())) {
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(document);
                String normalizedText = text.replaceAll("\\s+", " ");

                String detectedRfc = detectRfc(normalizedText, transactionType);

                InvoicePatternConfig config = patternConfigs.getOrDefault(detectedRfc, DEFAULT_PATTERN);
                String folio = extractWithPattern(normalizedText, config.getFolioPattern());
                BigDecimal total = extractTotal(normalizedText, config.getTotalPattern(), config.getGroupTotal());

                Map<String, Object> pdfInfoFallback = null;
                if (folio == null && total == null) {
                    pdfInfoFallback = buildPdfInfoFallback(normalizedText);
                }

                return InvoiceDataDto.builder()
                        .invoiceNumber(folio)
                        .amount(total)
                        .issuerRfc(detectedRfc)
                        .pdfInfoFallback(pdfInfoFallback)
                        .build();
            }

        } catch (IOException e) {
            throw new InternalException("Error al procesar el archivo PDF", e.getMessage());
        }
    }

    private String detectRfc(String text, TransactionType transactionType) {
        text = text.toUpperCase();

        if (transactionType == TransactionType.SALE) {
            for (String rfc : RFC_VENTAS) {
                if (text.contains(rfc)) return rfc;
            }
            return RFC_VENTA_DEFAULT;
        } else if (transactionType == TransactionType.PURCHASE) {
            for (String rfc : patternConfigs.keySet()) {
                if (!RFC_VENTAS.contains(rfc) && text.contains(rfc)) return rfc;
            }
        }
        return null;
    }

    private String extractWithPattern(String text, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(text);
        if (matcher.find()) return matcher.group(1).trim();
        return null;
    }

    private BigDecimal extractTotal(String text, String regex, int group) {
        Matcher matcher = Pattern.compile(regex).matcher(text);
        if (matcher.find()) {
            try {
                return new BigDecimal(matcher.group(group).replace(",", "").trim());
            } catch (Exception e) {
                log.warn("Error al convertir el total: {}", e.getMessage());
            }
        }
        return null;
    }

    private Map<String, Object> buildPdfInfoFallback(String text) {
        Map<String, Object> fallback = new LinkedHashMap<>();

        // RFCs posibles
        Matcher matcherRfc = Pattern.compile(RegexPatterns.RFC, Pattern.CASE_INSENSITIVE).matcher(text);
        int count = 1;
        while (matcherRfc.find() && count <= 3) {
            String found = matcherRfc.group(1).toUpperCase(Locale.ROOT).trim();
            if (RFC_VENTAS.contains(found)) continue;
            if (RegexPatterns.isLikelyRfc(found) && !fallback.containsValue(found)) {
                fallback.put("possibleIssuerRfc" + count, found);
                count++;
            }
        }

        // Montos posibles
        String preprocessedText = text.replaceAll("(\\d{1,3}(?:,\\d{3})*\\.\\d{2})([A-Za-z])", "$1 $2");
        Matcher matcherAmount = Pattern.compile(RegexPatterns.TOTAL).matcher(preprocessedText);
        List<BigDecimal> amounts = new ArrayList<>();
        while (matcherAmount.find()) {
            try {
                amounts.add(new BigDecimal(matcherAmount.group(1).replace(",", "")));
            } catch (Exception ignored) {}
        }
        amounts.stream().distinct().sorted(Comparator.reverseOrder()).limit(3)
                .forEachOrdered(amount -> fallback.put("possibleInvoiceAmount" + (fallback.size() + 1), amount));

        // Folios posibles
        Matcher matcherFolio = Pattern.compile(RegexPatterns.FOLIO).matcher(text);
        count = 1;
        while (matcherFolio.find() && count <= 3) {
            String candidate = matcherFolio.group(1).trim();
            if (RegexPatterns.isLikelyFolio(candidate) && !fallback.containsValue(candidate)) {
                fallback.put("possibleInvoiceNumber" + count, candidate);
                count++;
            }
        }

        return fallback.isEmpty() ? null : fallback;
    }
}
