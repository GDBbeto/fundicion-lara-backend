package com.fundicion.lara.commons.emuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionType {
    SALE("SALE"),// VENTAS
    PURCHASE("PURCHASE"), // COMPRAS
    EXPENSE("EXPENSE"); // GASTOS
    private final String type;
}
