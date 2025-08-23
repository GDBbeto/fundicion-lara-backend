package com.fundicion.lara.entity;


import com.fundicion.lara.commons.emuns.TransactionType;
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

    @Column(name = "order_transaction_id")
    private Integer orderTransactionId;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "issuer_rfc")
    private String issuerRfc;

    @Column(name = "type", nullable = false, length = 50)
    private TransactionType type; // VENTA | COMPRA | GASTOS

    @Column(name = "status")
    private String status = "A"; // ACTIVO - INACTIVO

    @Column(name = "operation_date", nullable = false)
    private LocalDate operationDate;

}
