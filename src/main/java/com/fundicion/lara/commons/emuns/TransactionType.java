package com.fundicion.lara.commons.emuns;

import com.fundicion.lara.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionType {
    SALE("SALE"),// VENTAS
    PURCHASE("PURCHASE"), // COMPRAS
    EXPENSE("EXPENSE"); // GASTOS
    private final String type;

    public static TransactionType fromString(String type) {
        for (TransactionType transactionType : TransactionType.values()) {
            if (transactionType.getType().equalsIgnoreCase(type)) {
                return transactionType;
            }
        }
        throw new BadRequestException("No enum constant for type: " + type);
    }
}
