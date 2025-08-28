package com.fundicion.lara.commons.constants;

public class Constants {
    public static final String IMGBB_API_URL = "https://api.imgbb.com/1/upload";

    public static final String LARA_ISSUER_RFC = "LABV570329UX6";
    public static final String LARA_FOLIO_PATTERN = "Folio:\\s*([A-Z0-9-]+)";
    public static final String LARA_TOTAL_PATTERN = "Total\\s*\\$\\s*([0-9,]+\\.\\d{2})";

    public static final String JACQUELINE_ISSUER_RFC = "AUMJ9811073H3";
    public static final String JACQUELINE_FOLIO_PATTERN = "Folio fiscal:\\s*([A-Z0-9-]+)";
    public static final String JACQUELINE_TOTAL_PATTERN = "Total\\s*\\$\\s*([0-9,]+\\.\\d{2})";

    public static final String GLOBAL_GAS_ISSUER_RFC = "GGA030723741";
    public static final String GLOBAL_GAS_FOLIO_PATTERN = "FOLIO:\\s*CH\\s*([0-9]+)";
    public static final String GLOBAL_GAS_TOTAL_PATTERN = "TOTAL:\\s*\\$\\s*([0-9,]+\\.\\d{2})\\s*\\$\\s*([0-9,]+\\.\\d{2})";
    public static final int GLOBAL_GAS_GROUP_TOTAL = 2;

    public static final String OSCAR_ISSUER_RFC = "SAGO660417PR5";
    public static final String OSCAR_FOLIO_PATTERN = "Factura\\s*(\\d+)";
    public static final String OSCAR_TOTAL_PATTERN = " TOTAL\\s*\\$\\s*([0-9,]+\\.\\d{2})";

    public static final String ISSUER_RFC_DEFAULT = JACQUELINE_ISSUER_RFC;
    public static final String RFC_PATTERN_DEFAULT = "RFC emisor:\\s*([A-Z0-9]+)";
    public static final int GROUP_TOTAL_DEFAULT = 1;


    // message
    public static final String ERROR_INVOICE_EXTRACT = "No pudimos extraer la información automáticamente. Por favor, ingresa los datos manualmente para avanzar.";
}
