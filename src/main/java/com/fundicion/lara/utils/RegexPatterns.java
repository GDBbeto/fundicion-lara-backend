package com.fundicion.lara.utils;

import java.util.regex.Pattern;

public class RegexPatterns {

    // =====================================================
    // Patrones regex
    // =====================================================

    // RFC
    public static final String RFC = "\\b([A-Z&Ñ]{3,4}\\d{6}[A-Z0-9]{3})\\b";

    // Total (cantidad monetaria)
    public static final String TOTAL = "\\b\\$?([0-9]{1,3}(?:,[0-9]{3})*\\.\\d{2})\\b";

    // Folio / UUID
    public static final String FOLIO = "\\b([A-Z0-9-]{10,})\\b";

    // Fechas (para filtrar falsos positivos)
    public static final String DATE = "\\d{4}-\\d{2}-\\d{2}([T\\s]\\d{2}(:\\d{2})?)?";

    // Default JSON patterns
    public static final String DEFAULT_FOLIO = "Folio fiscal:\\s*([A-Z0-9-]+)";
    public static final String DEFAULT_TOTAL = "Total\\s*\\$\\s*([0-9,]+\\.\\d{2})";

    // UUID CFDI típico
    public static final String UUID_CFDI = "[A-F0-9]{8}-[A-F0-9]{4}-[A-F0-9]{4}-[A-F0-9]{4}-[A-F0-9]{12}";

    // Folio alfanumérico corto-medio
    public static final String FOLIO_ALPHANUMERIC = "[A-Z0-9-]{2,14}";

    // Solo dígitos cortos
    public static final String FOLIO_NUMERIC = "\\d{2,10}";

    // =====================================================
    // Métodos de validación común
    // =====================================================

    public static boolean isLikelyRfc(String value) {
        return value != null && value.toUpperCase().matches(RFC);
    }

    public static boolean isLikelyDate(String value) {
        return value != null && value.matches(DATE);
    }

    public static boolean isLikelyFolio(String candidate) {
        if (candidate == null) return false;
        String value = candidate.trim().toUpperCase();

        if (isLikelyDate(value)) return false;                       // ❌ Excluir fechas
        if (value.matches(RFC)) return false;                         // ❌ Excluir RFC
        if (!value.matches(".*\\d.*")) return false;                  // ❌ Sin números
        if (value.matches("\\d{14,}")) return false;                  // ❌ Números largos
        if (value.length() > 40) return false;                        // ❌ Muy largo

        if (value.matches(UUID_CFDI)) return true;                    // ✅ UUID
        if (value.matches(FOLIO_ALPHANUMERIC) && value.matches(".*[A-Z].*") && value.matches(".*\\d.*")) return true; // ✅ Alfanumérico
        if (value.matches(FOLIO_NUMERIC)) return true;                // ✅ Numérico corto

        return false;
    }
}
