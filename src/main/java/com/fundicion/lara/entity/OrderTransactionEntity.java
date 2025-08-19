package com.fundicion.lara.entity;

import com.fundicion.lara.commons.emuns.DeliveryStatus;
import com.fundicion.lara.commons.emuns.MethodPayment;
import com.fundicion.lara.commons.emuns.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
@Table(name = "order_transactions")
public class OrderTransactionEntity extends AuditCommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_transaction_id")
    private Integer orderTransactionId;

    @Column(name = "extra_amount", nullable = false)
    private BigDecimal extraAmount;

    @Column(name = "description", length = 200)
    private String description;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(name = "item_count", nullable = false)
    private Integer itemCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "method_payment")
    private MethodPayment methodPayment;

    @Column(name = "invoice_number", length = 100)
    private String invoiceNumber;

    @Column(name = "client", nullable = false, length = 100)
    private String client;

    @Column(name = "amount_paid", nullable = false)
    private BigDecimal amountPaid;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 10)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false, length = 10)
    private DeliveryStatus deliveryStatus;

    @Column(name = "selling_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal sellingPrice;

    @Column(name = "operation_date", nullable = false)
    private LocalDate operationDate;

}
