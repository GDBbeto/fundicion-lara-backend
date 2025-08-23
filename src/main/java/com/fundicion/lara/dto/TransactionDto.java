package com.fundicion.lara.dto;


import com.fundicion.lara.commons.emuns.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private Long transactionId;

    private Integer orderTransactionId;

    private BigDecimal amount;

    private String description;

    private String invoiceNumber;

    private String issuerRfc;

    private TransactionType type;

    private String status = "A";

    private LocalDate operationDate;
}
