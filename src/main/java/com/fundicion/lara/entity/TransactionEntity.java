package com.fundicion.lara.entity;


import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

import java.math.BigDecimal;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transactions")
public class TransactionEntity extends AuditCommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "invoice_number", nullable = false, length = 100)
    private String invoiceNumber;

    @Column(name = "type", nullable = false, length = 50)
    private String type; // VENTA | COMPRA | GASTOS

    @Column(name = "operation_date", nullable = false)
    private LocalDate operationDate;

}
