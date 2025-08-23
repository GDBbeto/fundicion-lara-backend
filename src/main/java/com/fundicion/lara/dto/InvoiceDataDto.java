package com.fundicion.lara.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDataDto implements Serializable {
    private String invoiceNumber;
    private BigDecimal amount;
    private String issuerRfc;
}
