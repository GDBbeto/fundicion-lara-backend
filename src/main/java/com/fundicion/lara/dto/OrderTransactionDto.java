package com.fundicion.lara.dto;

import com.fundicion.lara.commons.emuns.DeliveryStatus;
import com.fundicion.lara.commons.emuns.MethodPayment;
import com.fundicion.lara.commons.emuns.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OrderTransactionDto implements Serializable {
    private Integer orderTransactionId;
    private BigDecimal extraAmount;
    private String description;
    private Integer productId;
    private String productName;
    private Integer itemCount;
    private MethodPayment methodPayment;
    private String invoiceNumber;
    private String client;
    private BigDecimal amountPaid;
    private PaymentStatus paymentStatus;
    private DeliveryStatus deliveryStatus;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;
    private BigDecimal profit;
    private LocalDate operationDate;
    private Boolean addTransaction;

    public BigDecimal getProfit() {
        BigDecimal totalPurchase =purchasePrice.multiply(BigDecimal.valueOf(itemCount));
        BigDecimal totalSelling = sellingPrice.multiply(BigDecimal.valueOf(itemCount));
        return totalSelling.subtract(totalPurchase);
    }
}
